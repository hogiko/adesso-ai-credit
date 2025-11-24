package de.hogi.adesso.ai.credit.control;

import de.hogi.adesso.ai.credit.domain.PersonCsvMapper;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import lombok.extern.java.Log;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@Log
public class PersonLoader {

    public static final String PERSON_CSV = "/sd254_users.csv";

    /**
     * creates a vectorstore by either reading it from a file or generating it.
     *
     * @param embeddingModel
     * @return
     */
    @Bean
    SimpleVectorStore getVectorStore(EmbeddingModel embeddingModel) {
        var vectorStore = new SimpleVectorStore(embeddingModel);

        File creditFile = new File("ai-credit.json");

        if (creditFile.exists()) {
            log.info("load from store cache");
            vectorStore.load(creditFile);

        } else {
            log.info("load from csv file");
            var creditDocs = loadCreditDocuments();

            vectorStore.doAdd(creditDocs);
            vectorStore.save(creditFile);
            log.info("created store cache");
        }

        return vectorStore;
    }

    List<Document> loadCreditDocuments() {
        var result = new ArrayList<Document>();

        try (
            InputStream    csvStream = getClass().getResourceAsStream(PERSON_CSV);
            BufferedReader csvReader = new BufferedReader(new InputStreamReader(csvStream));
        ) {
            var header = csvReader.readLine().split(","); // read header
            for (String line = csvReader.readLine(); (line != null); line = csvReader.readLine()) {
               result.add(createDocument(header, line.split(",")));
            }

        } catch (IOException ioException) {
            log.severe("reading %s failed: %s".formatted(PERSON_CSV, ioException));
        }

        return result;
    }

    Document createDocument(String[] header, String[] columns) {

        // create person
        var valueMap = new HashMap<String, String>();
        for (int i = 0; (i < header.length && i < columns.length); i++) {
            valueMap.put(header[i], columns[i]);
        }
        var person = PersonCsvMapper.toPerson(valueMap);

        // split up name into firstName and lastName
        int pos = person.getName().indexOf(' ');
        person.setFirstName(person.getName().substring(0, pos));
        person.setLastName(person.getName().substring(pos));

        // create metaData
        var metaData = new HashMap<String, Object>();

        // create metaData
        for (int i = 0; (i < header.length); i++) {
            metaData.put(header[i], columns[i]);
        }
        metaData.put("Firstname", person.getFirstName());
        metaData.put("Lastname", person.getLastName());

        // create id and text
        var id = "%s %s.%s %s %s".formatted(columns[0], columns[3], columns[4], columns[10], columns[8]);
        var text = """
            %s is a Person from %s %s, identifies as %s and lives in %s %s.
            Born in %d.%s the current age is %d while retirement will be %d.
            The cities capita income is %s while the personal income is %s.
            The latitude is %s, the longitude is %s.
            The debt is %s with a FICO Score of %s with a number of %s credit cards.
            """.formatted(
                person.getName(), person.getZip(), person.getCity(), person.getGender(),
                person.getAddress(), (person.getApartment().isEmpty() ? "" : "apartment " + person.getApartment()),
                person.getBirthYear(), person.getBirthMonth(), person.getCurrentAge(), person.getRetirementAge(),
                person.getIncomeZip(), person.getIncomePerson(),
                person.getLatitude(), person.getLongitude(),
                person.getDebt(), person.getFicoScore(), person.getNumCreditCards()
            );

        return new Document(id, text, metaData);
    }

}
