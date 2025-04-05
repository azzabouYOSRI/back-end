package com.yosri.defensy.backend.modules.etl.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "etl_processed_records")
public class EtlProcessedRecord {

    @Id
    private String id;

    @Field(type = FieldType.Long)
    private Long timestamp;  // No default value here, will be set explicitly

    private Map<String, String> dynamicAttributes;
}
