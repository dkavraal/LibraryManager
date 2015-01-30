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
	private static String mongoURI;
	private static MongoClientURI mongoClientURI;
	private static boolean isHerokuServer;
	
	public MongoConfigurator() {
		try {
			mongoURI = System.getenv("MONGOLAB_URI");
			isHerokuServer = !(mongoURI == null || mongoURI.equals(""));
			if (!isHerokuServer) {
				mongoURI = "mongodb://localhost:27017/local";
			}
			mongoClientURI = new MongoClientURI(mongoURI);
		} catch (Exception e) {
			logger.fatal("Serious problem connecting to DB. Check MONGOLAB_URI env var.", e);
		}
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
			try {
				MongoClientURI mongoClientURI = new MongoClientURI(mongoURI);
				return new SimpleMongoDbFactory(mongo(), 
												mongoClientURI.getDatabase(), 
												new UserCredentials(mongoClientURI.getUsername(), mongoClientURI.getPassword().toString()));
			} catch (Exception e) {
				logger.fatal("Mongo DB Factory failed", e);
			}
		}
		
		try {
			return new SimpleMongoDbFactory(mongo(), mongoClientURI.getDatabase());
		} catch (Exception e) {
			logger.fatal("Mongo DB Factory failed", e);
		}
		
		// really? db connection prop wasn't read; or some library problem with mongo / spring-mongo 
		return null;
	}

	public @Bean MongoTemplate mongoTemplate() {
		return new MongoTemplate(mongoDbFactory());
	}

}
