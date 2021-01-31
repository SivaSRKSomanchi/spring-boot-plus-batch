package com.example.springbootplusbatch.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coffee {

	private String brand;
	private String origin;
	private String characteristics;

}
