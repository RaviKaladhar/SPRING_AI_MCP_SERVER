package com.project.springAI.config;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.transformer.splitter.TextSplitter;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.core.io.Resource;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class VectorRAGStore {
    private final VectorStore vectorStore;

    //In real-time APPS private data can be used as public data will be used for training the model and it will be available to everyone.
    @Value("classpath:Cricket_History.pdf")
    private Resource pdf;

    public VectorRAGStore(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }

    @PostConstruct
    public void loadPDF(){
        TikaDocumentReader tikaDocumentReader = new TikaDocumentReader(pdf);
        log.info("Uploading PDF document data to Vector >>>>>>> " + pdf.getFilename());
        TextSplitter textSplitter = TokenTextSplitter.builder().withChunkSize(500).withMaxNumChunks(1000).build();
        vectorStore.add(textSplitter.split(tikaDocumentReader.get()));
    }
}
