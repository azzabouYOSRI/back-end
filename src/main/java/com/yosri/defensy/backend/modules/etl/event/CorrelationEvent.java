package com.yosri.defensy.backend.modules.etl.event;

import lombok.*;
import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CorrelationEvent {
    private String id;
    private Instant timestamp;
    private Map<String, String> correlatedAttributes;
}
