package com.yosri.defensy.backend.modules.ingestion.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * ✅ Event triggered when a CSV file is successfully parsed.
 */
@Getter
public class CsvParsedEvent extends ApplicationEvent {

    private final String filePath;

    public CsvParsedEvent(Object source, String filePath) {
        super(source);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
