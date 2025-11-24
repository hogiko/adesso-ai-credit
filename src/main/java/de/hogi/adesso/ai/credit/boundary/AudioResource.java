package de.hogi.adesso.ai.credit.boundary;

import java.util.Map;
import java.util.Optional;
import org.springframework.ai.audio.transcription.AudioTranscriptionPrompt;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.openai.OpenAiAudioTranscriptionModel;
import org.springframework.ai.openai.OpenAiAudioTranscriptionOptions;
import org.springframework.ai.openai.api.OpenAiAudioApi;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.core.io.ClassPathResource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AudioResource {

    static Map<String, String> oggFiles = Map.of(
        "1", "WhoHasMoreThan4CreditCards.ogg",
        "2", "WhoInWhitesCreekLivesInAnApartment.ogg",
        "3", "WhoIsHazelRobinson.ogg",
        "4", "WhoLivesInNewYork.ogg");

    OpenAiAudioTranscriptionModel transcriptionModel;
    ChatClient chatClient;
    VectorStore vectorStore;



    @GetMapping("/audio")
    public Map<String, Object> createTranscription(@RequestParam Optional<String> oggFileNumber) {
        if (oggFileNumber.isEmpty()) {
            return Map.of("oggFiles", oggFiles);
        }

        String oggFileName = oggFiles.get(oggFileNumber.get());
        if (oggFileName == null) {
            return Map.of("oggFiles", oggFiles, "badNumber", oggFileNumber.get());
        }

        var prompt = getTranscription(oggFileName);

        var answer = chatClient
            .prompt()
            .user(prompt)
            .call()
            .content();

        return Map.of(
            "oggFileName", oggFileName,
            "prompt", prompt,
            "answer", answer);
    }

    String getTranscription(String oggFileName) {
        var audioOptions = OpenAiAudioTranscriptionOptions.builder()
            .language("en")
            .temperature(0.5f)
            .responseFormat(OpenAiAudioApi.TranscriptResponseFormat.TEXT)
            .build();

        var audioPrompt = new AudioTranscriptionPrompt(new ClassPathResource(oggFileName), audioOptions);

        return transcriptionModel
            .call(audioPrompt)
            .getResult()
            .getOutput();
    }

    public AudioResource(OpenAiAudioTranscriptionModel transcriptionModel, ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        this.transcriptionModel = transcriptionModel;

        var searchRequest = SearchRequest.builder().topK(40 ).similarityThresholdAll().build();
        var qaAdvisor     = new QuestionAnswerAdvisor(vectorStore, searchRequest);
        this.chatClient = chatClientBuilder
            .defaultAdvisors(qaAdvisor)
            .build();

        this.vectorStore = vectorStore;
    }

}