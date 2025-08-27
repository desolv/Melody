package gg.desolve.melody.manager;

import com.mongodb.ConnectionString;
import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.config.Configurator;
import org.bson.UuidRepresentation;

import static gg.desolve.melody.Melody.instance;

@Getter
public class MongoManager {

    private MongoClient mongoClient;
    private MongoDatabase mongoDatabase;

    public MongoManager(String url, String database) {
        try {
            long start = System.currentTimeMillis();

            quietMongoLogs();
            MongoClientSettings mongoSettings = MongoClientSettings.builder()
                    .applyConnectionString(new ConnectionString(url))
                    .uuidRepresentation(UuidRepresentation.STANDARD)
                    .build();

            mongoClient = MongoClients.create(mongoSettings);
            mongoDatabase = mongoClient.getDatabase(database);

            instance.getLogger().info("Connected to MongoDB in " + (System.currentTimeMillis() - start) + "ms.");
        } catch (Exception e) {
            instance.getLogger().info("There was a problem connecting to MongoDB.");
            e.printStackTrace();
        }
    }

    public void quietMongoLogs() {
        Configurator.setLevel("org.mongodb.driver", Level.WARN);
        Configurator.setLevel("org.mongodb.driver.cluster", Level.WARN);
        Configurator.setLevel("org.mongodb.driver.connection", Level.WARN);
        Configurator.setLevel("org.mongodb.driver.client", Level.WARN);
    }
}
