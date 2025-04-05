package com.yosri.defensy.backend.modules.etl.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;
import java.util.Map;

/**
 * ✅ MongoDB Raw CSV Record Entity
 * - This is only used for fetching raw ingested data from MongoDB.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "csv_ingestion")  // ✅ Fetch from `csv_ingestion` collection
public class CsvRawRecord {

    @Id
    private String id;  // Unique MongoDB ID

    private Map<String, String> rawData;  // Raw event data
    private Instant ingestionTimestamp;  // Timestamp of ingestion
}
