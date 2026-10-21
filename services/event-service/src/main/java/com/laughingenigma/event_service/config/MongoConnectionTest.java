package com.laughingenigma.event_service.config;

import org.bson.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Component;

@Profile("dev")
@Component
public class MongoConnectionTest implements ApplicationRunner {

    private static final Logger log =
            LoggerFactory.getLogger(MongoConnectionTest.class);

    private final MongoTemplate mongoTemplate;

    public MongoConnectionTest(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    @Override
    public void run(ApplicationArguments args) {
        // Verify connectivity and authentication.
        Document result = mongoTemplate.executeCommand(
                new Document("ping", 1)
        );

        log.info("MongoDB ping successful: {}", result.get("ok"));

        // Verify access to the existing collection.
        long count = mongoTemplate
                .getCollection("event-details")
                .countDocuments();

        log.info(
                "MongoDB connection verified. event-details document count: {}",
                count
        );
    }
}
