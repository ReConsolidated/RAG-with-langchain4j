package io.github.reconsolidated.pwcgpasik.application;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.DocumentSplitters;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiTokenizer;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.UUID;

import static dev.langchain4j.model.openai.OpenAiChatModelName.GPT_4_O_MINI;

@Service
public class DocumentIngestor {
    private final EmbeddingStoreIngestor ingestor;


    public DocumentIngestor(@Qualifier(value = "embeddingStore") EmbeddingStore<TextSegment> embeddingStore,
                            EmbeddingModel embeddingModel) {
        DocumentSplitter splitter = DocumentSplitters
                .recursive(100, 0, new OpenAiTokenizer(GPT_4_O_MINI));
        this.ingestor = EmbeddingStoreIngestor.builder()
                .documentSplitter(splitter)
                .embeddingModel(embeddingModel)
                .embeddingStore(embeddingStore)
                .build();
    }

    public UUID ingestDocument(Document document) {
        UUID documentId = UUID.randomUUID();
        document.metadata().put("documentId", documentId);
        ingestor.ingest(document);
        return documentId;
    }
}
