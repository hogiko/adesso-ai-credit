package de.hogi.adesso.ai.credit.boundary;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class PersonResource {

    ChatClient chatClient;
    VectorStore vectorStore;

    @GetMapping("/person")
    public String personQuestion(@RequestParam String prompt) {
        return chatClient
            .prompt()
            .user(prompt)
            .call()
            .content();
    }

    public PersonResource(ChatClient.Builder chatClientBuilder, VectorStore vectorStore) {
        var searchRequest = SearchRequest.builder().topK(40 ).similarityThresholdAll().build();
        var qaAdvisor     = new QuestionAnswerAdvisor(vectorStore, searchRequest);
        this.chatClient = chatClientBuilder
            .defaultAdvisors(qaAdvisor)
            .build();
        this.vectorStore = vectorStore;
    }


}