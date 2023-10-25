package com.buschmais.backend.infrastructure;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.junit.jupiter.Container;

/**
 * Base class for integration tests spinning up the Spring Context and the MongoDB testcontainer
 */
@IntegrationTest
public abstract class AbstractIntegrationTest {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.11").withReuse(true);

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.data.mongodb.uri", mongoDBContainer::getUri);
	}

}
