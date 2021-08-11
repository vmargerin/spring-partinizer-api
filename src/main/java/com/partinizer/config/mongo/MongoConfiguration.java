package com.partinizer.config.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MongoCustomConversions;

import com.mongodb.MongoClientSettings.Builder;
import com.mongodb.ServerAddress;

@Configuration
public class MongoConfiguration extends AbstractMongoClientConfiguration {

	@Autowired
	Environment environment;

	@Override
	protected String getDatabaseName() {
		return this.environment.getProperty("spring.data.mongodb.database");
	}

	@Override
	protected void configureClientSettings(Builder builder) {

		// builder.credential(MongoCredential.createCredential("name", "db",
		// "pwd".toCharArray()))
		builder.applyToClusterSettings(settings -> {
			settings.hosts(Arrays.asList(new ServerAddress(this.environment.getProperty("spring.data.mongodb.host"),
					Integer.valueOf(this.environment.getProperty("spring.data.mongodb.port")))));
		});
	}

	@Bean
	@Override
	public MongoCustomConversions customConversions() {
		final List<Converter<?, ?>> converterList = new ArrayList<Converter<?, ?>>();
		converterList.add(new OffsetDateTimeReadConverter());
		converterList.add(new OffsetDateTimeWriteConverter());
		return new MongoCustomConversions(converterList);
	}

}
