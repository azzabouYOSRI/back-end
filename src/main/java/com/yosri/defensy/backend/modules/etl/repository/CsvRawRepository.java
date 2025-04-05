package com.yosri.defensy.backend.modules.etl.repository;

import com.yosri.defensy.backend.modules.etl.domain.CsvRawRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

/**
 * ✅ Repository for fetching raw CSV records from MongoDB.
 */
@Repository
public interface CsvRawRepository extends MongoRepository<CsvRawRecord, String> {

    /**
     * ✅ Fetches records ingested after a certain timestamp.
     *
     * @param timestamp The timestamp to filter recent data.
     * @return List of raw records ingested after this timestamp.
     */
    List<CsvRawRecord> findByIngestionTimestampAfter(Instant timestamp);
}
