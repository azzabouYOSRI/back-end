package com.yosri.defensy.backend.modules.ingestion.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class CsvFileDetectedEvent extends ApplicationEvent {
    private final String filePath;

    public CsvFileDetectedEvent(Object source, String filePath) {
        super(source);
        this.filePath = filePath;
    }
}
