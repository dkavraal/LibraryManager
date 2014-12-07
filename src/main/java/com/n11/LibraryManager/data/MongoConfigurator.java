package com.n11.LibraryManager.data;

import java.net.UnknownHostException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;

@EnableMongoRepositories
@PropertySource("classpath:mongodb.properties")
@Configuration
public class MongoConfigurator {
	@Value("${db.name}")
	private String dbName;

	@Value("${db.host}")
	private String dbHost;

	@Value("${db.port}")
	private int dbPort;

	@Bean
	public DB db() throws UnknownHostException {
		return mongo().getDB(dbName);
	}

	@Bean
	public Mongo mongo() throws UnknownHostException {
		return new MongoClient(dbHost, dbPort);
	}

	public @Bean MongoDbFactory mongoDbFactory() throws Exception {
		//UserCredentials userCredentials = new UserCredentials("joe", "secret");
		return new SimpleMongoDbFactory(new MongoClient(dbHost, dbPort), dbName);//, userCredentials);
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongoDbFactory());
	}

}
