package io.github.reconsolidated.pwcgpasik.application;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Profile(value = "!test")
public class ChatAgentWrapper {
    // Needed to wrap the AiService interface due to issues with mocking for test reasons
    private final DocumentChatAgent documentChatAgent;

    public String answer(String userMessage, String documentContent) {
        return documentChatAgent.answer(userMessage, documentContent);
    }
}
