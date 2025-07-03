package com.ipeksavas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipeksavas.dto.request.CustomerRequest;
import com.ipeksavas.service.CustomerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/customer")
@RequiredArgsConstructor
public class CustomerController {
	
	private final CustomerService customerService;
	
	@PostMapping(path = "/save")//save islemi oldugu icin post!!!
	public ResponseEntity<String> addCustomer(@RequestBody CustomerRequest request){
		customerService.addCustomer(request);
		return ResponseEntity.ok("Müşteri kayıt edilmiştir.");
	}
}
