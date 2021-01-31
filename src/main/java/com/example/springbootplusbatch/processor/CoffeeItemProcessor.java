package com.example.springbootplusbatch.processor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;

import com.example.springbootplusbatch.dto.Coffee;

public class CoffeeItemProcessor implements ItemProcessor<Coffee, Coffee> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CoffeeItemProcessor.class);

	@Value("#{jobParameters['run.time']}")
	String runTime;

	@Override
	public Coffee process(final Coffee coffee) throws Exception {
		LOGGER.info("run.time => {}", runTime);
		String brand = coffee.getBrand().toUpperCase();
		String origin = coffee.getOrigin().toUpperCase();
		String chracteristics = coffee.getCharacteristics().toUpperCase();

		Coffee transformedCoffee = new Coffee(brand, origin, chracteristics);
		LOGGER.info("Converting ( {} ) into ( {} )", coffee, transformedCoffee);

		return transformedCoffee;
	}

}