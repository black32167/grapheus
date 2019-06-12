/**
 * 
 */
package grapheus.it.util;

import java.util.UUID;

import grapheus.server.config.DBConfig;

/**
 * @author black
 *
 */
public class TestDbConfig extends DBConfig {
    private String dbName = System.getProperty("db.test.name", "db"+UUID.randomUUID().toString());
    
    @Override
    public String getDbName() {
        return dbName;
    }
}
