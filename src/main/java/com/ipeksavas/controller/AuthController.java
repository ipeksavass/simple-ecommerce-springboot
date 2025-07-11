package com.ipeksavas.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ipeksavas.jwt.AuthenticationRequest;
import com.ipeksavas.jwt.AuthenticationResponse;
import com.ipeksavas.jwt.AuthenticationService;
import com.ipeksavas.jwt.RegisterRequest;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

	private final AuthenticationService authenticationService;
	
	@PostMapping("/register")
	public ResponseEntity<AuthenticationResponse> register(@RequestBody RegisterRequest request){
		AuthenticationResponse response = authenticationService.register(request);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/authenticate")
	public ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest request){
		AuthenticationResponse response = authenticationService.authenticate(request);
		return ResponseEntity.ok(response);
	}
}
