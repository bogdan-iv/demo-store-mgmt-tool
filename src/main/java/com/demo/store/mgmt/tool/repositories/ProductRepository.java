package com.demo.store.mgmt.tool.repositories;

import com.demo.store.mgmt.tool.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
