package com.yosri.defensy.backend.modules.ingestion.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * ✅ Event triggered when CSV parsing fails.
 */
@Getter
public class CsvParsingFailedEvent extends ApplicationEvent {

    private final String filePath;

    public CsvParsingFailedEvent(Object source, String filePath) {
        super(source);
        this.filePath = filePath;
    }

    public String getFilePath() {
        return filePath;
    }
}
