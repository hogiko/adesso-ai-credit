package de.hogi.adesso.ai.credit.boundary;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class ChatResource {

    ChatClient chatClient;

    @GetMapping("/ask")
    public String ask(@RequestParam String prompt) {
        return chatClient
            .prompt()
            .user(prompt)
            .call()
            .content();
    }

    public ChatResource(ChatClient.Builder chatClientBuilder) {
        this.chatClient = chatClientBuilder
            .build();
    }

}