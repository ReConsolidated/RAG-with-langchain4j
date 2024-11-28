package io.github.reconsolidated.pwcgpasik.application;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;

@AiService
public interface DocumentChatAgent {

    @SystemMessage("""
            You are an assistant designed to help users interact with their documents.
            Use the provided document contents to answer their questions. If the information is not available
            in the document, clearly state that. Related document contents are {{relatedContents}}
            """)
    String answer(@UserMessage String userMessage, @V("relatedContents") String documentContent);
}
