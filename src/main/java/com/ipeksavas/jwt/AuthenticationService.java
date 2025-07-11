package com.ipeksavas.jwt;

import java.math.BigDecimal;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.ipeksavas.model.Cart;
import com.ipeksavas.model.Customer;
import com.ipeksavas.model.Role;
import com.ipeksavas.repository.CartRepository;
import com.ipeksavas.repository.CustomerRepository;


import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthenticationService {
	
	private final CustomerRepository customerRepository;
	private final CartRepository cartRepository;
	private final PasswordEncoder passwordEncoder;
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	
	public AuthenticationResponse  register(RegisterRequest request) {
		Customer customer = new Customer();
		customer.setName(request.getName());
		customer.setEmail(request.getEmail());
		customer.setUsername(request.getUsername());
		customer.setPassword(passwordEncoder.encode(request.getPassword()));
		customer.setRole(Role.ROLE_USER);
		customerRepository.save(customer);
		
		Cart cart = new Cart();
	    cart.setCustomer(customer);
	    cart.setTotalPrice(BigDecimal.ZERO);
	    cartRepository.save(cart);
		
		String token = jwtService.generateToken(customer);
		return new AuthenticationResponse(token);
	}
	
	public AuthenticationResponse authenticate(AuthenticationRequest request) {
		authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(request.getUsername(),
						request.getPassword())
				);
		Customer customer = customerRepository.findByUsername(request.getUsername())
				.orElseThrow(() -> new IllegalArgumentException("NO CUSTOMER FOUND"));
		
		String token = jwtService.generateToken(customer);
		return new AuthenticationResponse(token);
		
	}
}
