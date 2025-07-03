package com.ipeksavas.dto.request;

import java.math.BigDecimal;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ProductRequest {

	private String name;
	private BigDecimal price;
	private int stock;
}
