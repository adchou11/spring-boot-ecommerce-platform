package com.adchou.productservice;

import java.math.BigDecimal;
import org.junit.jupiter.api.Assertions;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.adchou.productservice.dto.ProductRequest;
import com.adchou.productservice.repository.ProductRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.testcontainers.junit.jupiter.Container;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/*
 * @SpringBootTest: This annotation tells Spring Boot to start an application context for integration tests. The application context includes all the beans and configurations
 * that your application would have in a running state.

@Testcontainers: This is a JUnit 5 extension that activates the Testcontainers support for the annotated test class,
* managing the lifecycle of containers that are used in the tests.

@AutoConfigureMockMvc: This annotation is used to auto-configure MockMvc, which is a powerful tool
* for testing web applications without starting an HTTP server.

MongoDBContainer: This is a Testcontainers class that represents a MongoDB Docker container.
* The test class declares a static MongoDBContainer which is annotated with @Container, indicating that Testcontainers should manage this container.
* The specified image version is "mongo:4.4.2".

@Autowired MockMvc: The MockMvc instance is injected into the test, allowing you to send HTTP requests and assert the results for your tests.

@Autowired ObjectMapper: This is Jackson's JSON object mapper, and it's used here to convert ProductRequest objects to JSON strings.

@Autowired ProductRepository: This is your Spring Data MongoDB repository, which interacts with the MongoDB database.

@DynamicPropertySource: This annotation is used to dynamically override configuration properties for tests. In this case, it sets the MongoDB URI to the one provided by the Testcontainers MongoDB instance, ensuring that the application connects to the test MongoDB container.

shouldCreateProduct Test Method: This is the actual test case. It creates a ProductRequest object, converts it to a JSON string, 
and sends a POST request to /api/product with that JSON as the request body. It expects the HTTP status to be "Created" (201). 
After the request, it asserts that the product repository contains exactly one entry, 
which means the product was successfully created and stored in the MongoDB database.


The test method shouldCreateProduct is an end-to-end test simulating a client sending data to a REST endpoint to create a new product. It validates that the application's web layer is working correctly (by using MockMvc) and that the application is correctly integrated with MongoDB (by using a real MongoDB instance provided by Testcontainers).
 */
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ProductServiceApplicationTests {

	@Container 
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:4.4.2");
	
	@Autowired
	private MockMvc mockMvc;
	
	@Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private ProductRepository productRepository;

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dynamicPropertyRegistry) {
		dynamicPropertyRegistry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);

	}

	@Test
    void shouldCreateProduct() throws Exception {
        ProductRequest productRequest = getProductRequest();
        String productRequestString = objectMapper.writeValueAsString(productRequest);
        mockMvc.perform(MockMvcRequestBuilders.post("/api/product")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(productRequestString))
                .andExpect(status().isCreated());
        Assertions.assertEquals(1, productRepository.findAll().size());
    }
    
	private ProductRequest getProductRequest() {
        return ProductRequest.builder()
                .name("iPhone 13")
                .description("iPhone 13")
                .price(BigDecimal.valueOf(1200))
                .build();
    }

}
