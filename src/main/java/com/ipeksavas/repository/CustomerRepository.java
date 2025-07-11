package com.ipeksavas.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipeksavas.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

	boolean existsByEmail(String email);
	Optional<Customer> findByUsername(String username);
}
