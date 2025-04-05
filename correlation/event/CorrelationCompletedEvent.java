package com.yosri.defensy.backend.modules.correlation.event;

import com.yosri.defensy.backend.modules.correlation.domain.CorrelationResult;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * ✅ Event triggered when correlation is successfully completed.
 */
@Getter
public class CorrelationCompletedEvent extends ApplicationEvent {

    private  CorrelationResult correlationResult;

    public CorrelationCompletedEvent(Object source, CorrelationResult correlationResult) {
        super(source);
        this.correlationResult = correlationResult;
    }
}
