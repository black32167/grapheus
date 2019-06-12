/**
 * 
 */
package grapheus.server.config;

import org.grapheus.common.config.HumanReadableConfigConsumer;
import org.grapheus.common.config.HumanReadableConfigProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.Getter;

/**
 * @author black
 *
 */
@Getter
@Service
public class DBConfig implements HumanReadableConfigProvider {
	private final static String DB_HOST_KEY = "${db.host}";
	private static final String DB_PORT_KEY = "${db.port}";
	private static final String DB_NAME_KEY = "${db.name}";
	private static final String DB_PASSWORD_KEY = "${db.password}";
	private static final String DB_USER_KEY = "${db.user}";

	
	@Value(DB_HOST_KEY)
	private String dbHost;

	@Value(DB_PORT_KEY)
	private int dbPort;

	@Value(DB_NAME_KEY)
	private String dbName;

	@Value(DB_PASSWORD_KEY)
	private String dbPassword;

	@Value(DB_USER_KEY)
	private String dbUser;

	@Override
	public void provideConfig(HumanReadableConfigConsumer consumer) {
		consumer.addConfig("Database host", DB_HOST_KEY, dbHost);
		consumer.addConfig("Database port", DB_PORT_KEY, dbPort);
		consumer.addConfig("Database user", DB_USER_KEY, dbUser);
	}
}
