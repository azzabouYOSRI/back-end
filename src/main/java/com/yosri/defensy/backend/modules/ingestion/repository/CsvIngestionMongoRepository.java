package com.yosri.defensy.backend.modules.ingestion.repository;

import com.yosri.defensy.backend.modules.ingestion.domain.CsvIngestionRecord;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CsvIngestionMongoRepository extends MongoRepository<CsvIngestionRecord, String> {
}
