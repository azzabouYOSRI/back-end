package com.yosri.defensy.backend.modules.etl.event;

import com.yosri.defensy.backend.modules.etl.domain.EtlProcessedRecord;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * ✅ Event triggered when ETL processing is successfully completed.
 */
@Getter
public class EtlProcessingCompletedEvent extends ApplicationEvent {

    private EtlProcessedRecord processedRecord;

    public EtlProcessingCompletedEvent(Object source, EtlProcessedRecord processedRecord) {
        super(source);
        this.processedRecord = processedRecord;
    }
}
