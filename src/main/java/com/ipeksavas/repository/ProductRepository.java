package com.ipeksavas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipeksavas.model.Product;

public interface ProductRepository extends JpaRepository<Product, Long>{

}
