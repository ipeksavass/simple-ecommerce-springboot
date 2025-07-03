package com.ipeksavas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipeksavas.model.Cart;

public interface CartRepository extends JpaRepository<Cart, Long>{

}
