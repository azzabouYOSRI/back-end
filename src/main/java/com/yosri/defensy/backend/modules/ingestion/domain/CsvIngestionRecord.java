package com.yosri.defensy.backend.modules.ingestion.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "csv_ingestion") // ✅ Stores CSV data in MongoDB
public class CsvIngestionRecord {

    @Id
    private String id; // ✅ Auto-generated by MongoDB

    private Map<String, String> data; // ✅ Flexible structure for CSV fields

    @Builder.Default
    private Instant timestamp = Instant.now(); // ✅ Capture ingestion time

}
