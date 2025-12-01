package com.demo.store.mgmt.tool.controllers;

import com.demo.store.mgmt.tool.dto.AddProductRequest;
import com.demo.store.mgmt.tool.models.Product;
import com.demo.store.mgmt.tool.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureWebMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Optional;
import org.springframework.web.util.UriComponentsBuilder;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureWebMvc
public class ProductControllerTest {
    //@Autowired
    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;


    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
    }

    @Test
    void testCreateProduct() throws Exception {
        AddProductRequest product = new AddProductRequest("Keyboard",BigDecimal.valueOf(75.0));
        Product savedProduct = new Product(3L, "Keyboard", BigDecimal.valueOf(75.00));
        when(productService.addProduct(any(Product.class))).thenReturn(savedProduct);

        mockMvc.perform(post("/api/products/public")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(product)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Keyboard"));
    }

    @Test
    void testGetAllProducts() throws Exception {

        Product product1 = new Product(1L, "Laptop", BigDecimal.valueOf(1200.00));
        Product product2 = new Product(2L, "Mouse",  BigDecimal.valueOf(25.00));

        when(productService.findAllProducts()).thenReturn(Arrays.asList(product1, product2));


        mockMvc.perform(get("/api/products/public"))
                .andExpect(status().isOk());
    }

    @Test
    void testChangePrice() throws Exception {
        Long productId = 1L;

        BigDecimal oldPrice = BigDecimal.valueOf(50.00);
        BigDecimal newPrice = BigDecimal.valueOf(99.99);
        Product existingProduct = new Product(productId, "Old Product", oldPrice);
        Product updatedProduct = new Product(productId, "Old Product", newPrice);

        when(productService.findProductById(productId)).thenReturn(Optional.of(existingProduct));
        when(productService.changeProductPrice(productId, newPrice)).thenReturn(updatedProduct);

        // Construct the URL with a query parameter: /api/products/admin/1/price?newPrice=99.99
        String url = UriComponentsBuilder.fromPath("/api/products/admin/{id}/price")
                .queryParam("newPrice", newPrice)
                .buildAndExpand(productId)
                .toUriString();

        mockMvc.perform(put(url)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk()) // Expecting 200 OK for an update
                .andExpect(jsonPath("$.price").value(newPrice));

    }
}
