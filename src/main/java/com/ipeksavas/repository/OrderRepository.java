package com.ipeksavas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipeksavas.model.Order;

public interface OrderRepository extends JpaRepository<Order, Long>{

}
