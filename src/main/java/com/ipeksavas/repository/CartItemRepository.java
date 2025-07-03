package com.ipeksavas.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipeksavas.model.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long>{

	List<CartItem> findByProductId(Long productId);
}
