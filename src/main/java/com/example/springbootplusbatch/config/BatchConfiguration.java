package com.example.springbootplusbatch.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersIncrementer;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.example.springbootplusbatch.dto.Coffee;
import com.example.springbootplusbatch.listener.JobCompletionNotificationListener;
import com.example.springbootplusbatch.processor.CoffeeItemProcessor;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Value("${file.input}")
	private String fileInput;

	@Bean
	public FlatFileItemReader<Coffee> reader() {
		return new FlatFileItemReaderBuilder<Coffee>().name("coffeeItemReader")
				.resource(new ClassPathResource(fileInput)).delimited()
				.names(new String[] { "brand", "origin", "characteristics" })
				.fieldSetMapper(new BeanWrapperFieldSetMapper<Coffee>() {
					{
						setTargetType(Coffee.class);
					}
				}).build();
	}

	@Bean
	@StepScope
	public CoffeeItemProcessor processor() {
		return new CoffeeItemProcessor();
	}

	@Bean
	public JdbcBatchItemWriter<Coffee> writer(DataSource dataSource) {
		return new JdbcBatchItemWriterBuilder<Coffee>()
				.itemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>())
				.sql("INSERT INTO coffee (brand, origin, characteristics) VALUES (:brand, :origin, :characteristics)")
				.dataSource(dataSource).build();
	}

	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("importUserJob").incrementer(new JobParametersIncrementer() {
			@Override
			public JobParameters getNext(JobParameters jobParameters) {
				return new JobParametersBuilder(jobParameters).addString("job.name", "exportUserJob")
						.addString("run.time", String.valueOf(System.currentTimeMillis())).toJobParameters();
			}
		}).listener(listener).flow(step1).end().build();
	}

	@Bean
	public Step step1(JdbcBatchItemWriter<Coffee> writer) {
		return stepBuilderFactory.get("step1").<Coffee, Coffee>chunk(10).reader(reader()).processor(processor())
				.writer(writer).build();
	}

}