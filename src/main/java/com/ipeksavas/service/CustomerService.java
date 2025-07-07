package com.ipeksavas.service;

import org.springframework.stereotype.Service;

import com.ipeksavas.dto.request.CustomerRequest;
import com.ipeksavas.model.Cart;
import com.ipeksavas.model.Customer;
import com.ipeksavas.repository.CustomerRepository;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor  //it's safer than @Autowired 
public class CustomerService {
	private final CustomerRepository customerRepository;
	
	@Transactional//If there is an error in any operation, it will undo everything it has done and the database will remain clean.
	public void addCustomer(CustomerRequest request) {
		if(customerRepository.existsByEmail(request.getEmail())) {
			throw new IllegalArgumentException("This email address is registered in the system.");
		}
		Customer customer = new Customer();
		customer.setName(request.getName());
		customer.setEmail(request.getEmail());
		
		Cart cart = new Cart();
		cart.setCustomer(customer);
		customer.setCart(cart);//I have a two-way relationship
		
		customerRepository.save(customer);
	}
}
