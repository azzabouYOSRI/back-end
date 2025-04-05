package com.yosri.defensy.backend.modules.etl.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

import java.util.Map;

@Getter
public class EtlProcessingFailedEvent extends ApplicationEvent {
    private final Map<String, String> failedRecord;
    private final String error;

    public EtlProcessingFailedEvent(Object source, Map<String, String> failedRecord, String error) {
        super(source);
        this.failedRecord = failedRecord;
        this.error = error;
    }
}
