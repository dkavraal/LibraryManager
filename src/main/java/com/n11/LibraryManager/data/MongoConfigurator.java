package com.n11.LibraryManager.data;

import org.apache.log4j.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
@Configuration
public class MongoConfigurator {
	protected static Logger logger = Logger.getLogger(MongoConfigurator.class.getName());
	private static String mongoURI = System.getenv("MONGOLAB_URI");
	private static MongoClientURI mongoClientURI = new MongoClientURI(mongoURI);
	private static boolean isHerokuServer;
	
	public MongoConfigurator() {
		isHerokuServer = !(mongoURI == null || mongoURI.equals(""));
		/*
		try {
			dbName = mongoClientURI.getDatabase();
			dbServerAddress = new ServerAddress(mongoClientURI.getHosts().get(0));
		} catch (Exception e) {
			logger.error("DB Connection information corrupt. Check MONGOLAB_URI env var.", e);
		}*/
	}
	
	@Bean
	public DB db() {
		return mongo().getDB(mongoClientURI.getDatabase());
	}
	
	@SuppressWarnings("deprecation")
	@Bean
	public Mongo mongo() {
		if (isHerokuServer) {
			try {
				return new MongoClient(mongoClientURI);
			} catch (Exception e) {
				logger.error("MongoConfigurator cannot create a client connection.", e);
			}

			try {
				MongoURI uri = new MongoURI(mongoURI);
				return new Mongo(uri);
			} catch (Exception e) {
				logger.error("MongoConfigurator cannot create a client connection.", e);
			}
			
		}

		try {
			return new MongoClient(mongoClientURI);
		} catch (Exception e) {
			logger.fatal("MongoConfigurator cannot create a client connection.", e);
			return null;
		}
	}

	public @Bean MongoDbFactory mongoDbFactory() {
		if (isHerokuServer) {
			MongoClientURI mongoClientURI = new MongoClientURI(mongoURI);
			return new SimpleMongoDbFactory(mongo(), 
											mongoClientURI.getDatabase(), 
											new UserCredentials(mongoClientURI.getUsername(), mongoClientURI.getPassword().toString()));
		}
		
		try {
			return 
					
					new SimpleMongoDbFactory(mongo(), mongoClientURI.getDatabase());
		} catch (Exception e) {
			logger.fatal("Mongo DB Factory failed", e);
		}
		
		return null;
	}

	public @Bean MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoDbFactory());
	}

}
