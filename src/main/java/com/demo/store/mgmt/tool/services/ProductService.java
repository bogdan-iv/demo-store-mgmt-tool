package com.demo.store.mgmt.tool.services;

import com.demo.store.mgmt.tool.dto.AddProductRequest;
import com.demo.store.mgmt.tool.exception.ProductNotFoundException;
import com.demo.store.mgmt.tool.exception.ProductValidationException;
import com.demo.store.mgmt.tool.models.Product;
import com.demo.store.mgmt.tool.repositories.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ProductService {
    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product addProduct(AddProductRequest productRequest)
    {
        Product newProduct = new Product();
        newProduct.setName(productRequest.name());
        newProduct.setPrice(productRequest.price());
        return productRepository.save(newProduct);
    }

    public Optional<Product> findProductById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> findProductByName(String name) {
        return productRepository.findByName(name);
    }

    public List<Product> findAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> findProductsByNameContaining(String name) {
        return productRepository.findByNameContaining(name);
    }

    public List<Product> findProductsByPriceRange(BigDecimal minPrice, BigDecimal maxPrice) {
        return productRepository.findByPriceBetween(minPrice, maxPrice);
    }

    public Product changeProductPrice(Long id, BigDecimal newPrice) {
        if (newPrice == null || newPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ProductValidationException("New price must be greater than zero");
        }
        Optional<Product> productOpt = productRepository.findById(id);
        if (productOpt.isEmpty()) {
            throw new ProductNotFoundException( id);
        }
        Product product = productOpt.get();
        product.setPrice(newPrice);
        return productRepository.save(product);
    }

    public void deleteProduct(Long id) {
        productRepository.deleteById(id);
    }

    public long countProducts() {
        return productRepository.count();
    }
}
