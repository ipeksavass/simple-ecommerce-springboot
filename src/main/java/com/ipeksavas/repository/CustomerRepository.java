package com.ipeksavas.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.ipeksavas.model.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{

	boolean existsByEmail(String email);//verilen mail vei tabanında kayıtlı mı diye kontrol edilir.
}
