package com.n11.LibraryManager.data;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.authentication.UserCredentials;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.MongoURI;

@EnableMongoRepositories
@PropertySource("classpath:mongodb.properties")
@Configuration
public class MongoConfigurator {
	protected static Logger logger = Logger.getLogger(MongoConfigurator.class.getName());
	private static final String mongoURI = System.getenv("MONGOLAB_URI");
	private static boolean isHeroku;
	
	@Value("${db.name}")
	private String dbName;

	@Value("${db.host}")
	private String dbHost;

	@Value("${db.port}")
	private int dbPort;
	
	public MongoConfigurator() {
		isHeroku = !(System.getenv("MONGOLAB_URI") == null || System.getenv("MONGOLAB_URI").equals(""));
		if (isHeroku) {
			try {
				MongoClientURI uri = new MongoClientURI(mongoURI);
				dbName = uri.getDatabase();
				dbHost = uri.getHosts().get(0);
			} catch (Exception e) {
				logger.error("MongoConfigurator cannot start.", e);
			}
			//port TODO
		}
	}
	
	@Bean
	public DB db() throws Exception {
		return mongo().getDB(dbName);
	}
	
	@SuppressWarnings("deprecation")
	@Bean
	public Mongo mongo() throws Exception {
		if (isHeroku) {
			try {
				return new Mongo(new MongoURI(mongoURI));
			} catch (Exception e) {
				logger.error("MongoConfigurator cannot create a client connection.", e);
			}
			
			try {
				MongoClientURI mongoClientURI = new MongoClientURI(mongoURI);
				return new MongoClient(mongoClientURI);
			} catch (Exception e) {
				logger.error("MongoConfigurator cannot create a client connection.", e);
			}
			
		}

		return new MongoClient(dbHost, dbPort);
	}

	public @Bean MongoDbFactory mongoDbFactory() throws Exception {
		if (isHeroku) {
			MongoClientURI mongoClientURI = new MongoClientURI(mongoURI);
			return new SimpleMongoDbFactory(mongo(), dbName, new UserCredentials(mongoClientURI.getUsername(), mongoClientURI.getPassword().toString()));
		}
		return new SimpleMongoDbFactory(mongo(), dbName);
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception {
		return new MongoTemplate(mongoDbFactory());
	}

}
