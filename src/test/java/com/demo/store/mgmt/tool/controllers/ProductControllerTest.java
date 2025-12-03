package com.demo.store.mgmt.tool.controllers;

import com.demo.store.mgmt.tool.dto.AddProductRequest;
import com.demo.store.mgmt.tool.models.Product;
import com.demo.store.mgmt.tool.repositories.ProductRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import org.springframework.test.web.servlet.client.MockMvcWebTestClient;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import org.springframework.web.util.UriComponentsBuilder;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureWebTestClient
@ActiveProfiles("test") // <-- Add this annotation to select the test resources config
public class ProductControllerTest {

    private WebTestClient webTestClient;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private WebApplicationContext context;

    @BeforeEach
    void setUp() {
        this.webTestClient = MockMvcWebTestClient.bindToApplicationContext(this.context)
                .apply(SecurityMockMvcConfigurers.springSecurity()) // Apply MVC Security Context
                .build();
        // Also clear the test DB here if needed
        productRepository.deleteAll();
    }

    // Test Case 1: Adding a product as an ADMIN user (authorized)
    @Test
    @WithMockUser(roles = "ADMIN") // User has the required "ADMIN" role
    void testAddProduct_AsAdmin_ShouldSucceedWith201() throws Exception {
        AddProductRequest request = new AddProductRequest("Keyboard",BigDecimal.valueOf(75.0));

        webTestClient.post().uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request) // Pass the DTO directly
                .exchange() // Perform the request
                .expectStatus().isCreated() // Assert HTTP status is 201 Created
                .expectHeader().contentType(MediaType.APPLICATION_JSON) // Assert JSON content type
                .expectBody() // Start asserting the response body content
                .jsonPath("$.name").isEqualTo("Keyboard"); // Use jsonPath for assertion
    }


    // Test Case 1: Adding a product as an USER user (not authorized)
    @Test
    @WithMockUser(roles = "USER") // User has the not authorized "USER" role
    void testAddProduct_AsUser_ShouldBeForbidden() throws Exception {
        AddProductRequest request = new AddProductRequest("Keyboard", BigDecimal.valueOf(75.00));

        // The service layer is not mocked, but the security layer blocks the request
        webTestClient.post().uri("/api/v1/products")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(request)
                .exchange()
                .expectStatus().isForbidden(); // Expect 403 Forbidden
    }

    @Test
    @WithMockUser(roles = "USER")
    void testGetAllProducts() throws Exception {

        productRepository.save(new Product(null, "Laptop", BigDecimal.valueOf(1200.00)));
        productRepository.save(new Product(null, "Mouse",  BigDecimal.valueOf(25.00)));

        webTestClient.get().uri("/api/v1/products")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Product.class) // Expect a list of Product objects back
                .hasSize(2)
                .value(products -> {
                    // AssertJ assertions on the resulting list
                    assertThat(products.get(0).getName()).isEqualTo("Laptop");
                });
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testChangePrice() throws Exception {
        // Pre-populate DB with an item
        Product existingProduct = productRepository.save(new Product(null, "Old Product", BigDecimal.valueOf(50.00)));
        Long productId = existingProduct.getId();
        BigDecimal newPrice = BigDecimal.valueOf(99.99);

        String url = UriComponentsBuilder.fromPath("/api/v1/products/{id}/price")
                .queryParam("newPrice", newPrice)
                .buildAndExpand(productId)
                .toUriString();

        webTestClient.put().uri(url)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price").isEqualTo(newPrice);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void testDelete() throws Exception {
        // Pre-populate test DB with an item to delete
        Product itemToDelete = productRepository.save(new Product(null, "Temp Item", BigDecimal.valueOf(10.00)));
        Long productId = itemToDelete.getId();

        webTestClient.delete().uri("/api/v1/products/{id}", productId)
                .exchange()
                .expectStatus().isNoContent() // Expect 204 No Content
                .expectBody().isEmpty(); // Assert body is empty

        // Verify it was actually deleted from the H2 DB
        assertThat(productRepository.findById(productId)).isEmpty();
    }
}
