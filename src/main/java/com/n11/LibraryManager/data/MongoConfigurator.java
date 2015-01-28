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
import com.mongodb.MongoClientURI;

@EnableMongoRepositories
@PropertySource("classpath:mongodb.properties")
@Configuration
public class MongoConfigurator {
	private static final String mongoURI = System.getenv("MONGOLAB_URI");
	private static final boolean isHeroku = !System.getenv("MONGOLAB_URI").equals("");
	
	@Value("${db.name}")
	private String dbName;

	@Value("${db.host}")
	private String dbHost;

	@Value("${db.port}")
	private int dbPort;
	
	public MongoConfigurator() {
		MongoClientURI uri = new MongoClientURI(mongoURI);
		dbName = uri.getDatabase();
		dbHost = uri.getHosts().get(0);
		//port TODO
	}
	
	@Bean
	public DB db() throws UnknownHostException {
		return mongo().getDB(dbName);
	}
	
	@Bean
	public Mongo mongo() throws UnknownHostException {
		if (isHeroku) {
			return new MongoClient(new MongoClientURI(mongoURI));
		} else {
			return new MongoClient(dbHost, dbPort);
		}
	}

	public @Bean MongoDbFactory mongoDbFactory() throws Exception {
		return new SimpleMongoDbFactory(mongo(), db().getName());
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongoDbFactory());
	}

}
