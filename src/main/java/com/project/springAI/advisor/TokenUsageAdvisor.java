/*
package com.project.springAI.advisor;

//import org.springframework.ai.chat.client.ChatClientRequest;
//import org.springframework.ai.chat.client.ChatClientResponse;
//import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
//import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class TokenUsageAdvisor implements CallAdvisor {
    @Override
    public ChatClientResponse adviseCall(ChatClientRequest chatClientRequest, CallAdvisorChain callAdvisorChain) {
        Logger logger = Logger.getLogger(TokenUsageAdvisor.class.getName());
        ChatClientResponse chatClientResponse = callAdvisorChain.nextCall(chatClientRequest);
        Usage usage = chatClientResponse.chatResponse().getMetadata().getUsage();

        if(usage != null){
            logger.info("Token Usage - " + usage.toString());
        }

        return chatClientResponse;
    }

    @Override
    public String getName() {
        return "TokenUsageAdvisor";
    }

    @Override
    public int getOrder() {
        return 1;
    }
}
*/
