package com.ipeksavas.dto.response;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductResponse {

	private String name;
	
    private BigDecimal price;
	
	private int stock;
}
