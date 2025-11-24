package de.hogi.adesso.ai.credit.control;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.stereotype.Service;

@Service
public class OpenAiService   {

    final ChatModel chatClient;


    public String askAI(String prompt) {
        var result = chatClient.call(prompt);
            System.out.println("result1 =" + result);
        return result;
    }

        OpenAiService(ChatModel chatClient) {
        this.chatClient = chatClient;
    }
}
