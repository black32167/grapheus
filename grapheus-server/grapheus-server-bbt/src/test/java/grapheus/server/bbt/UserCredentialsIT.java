/**
 * 
 */
package grapheus.server.bbt;

import static java.lang.String.format;

import java.nio.charset.StandardCharsets;

import org.junit.Test;
import org.grapheus.client.model.security.RUserCredentials;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Slf4j
public class UserCredentialsIT extends AbstractUserBasedIT {

    @Test
    public void createUserCredentials() {

        withUser("test_user", (userKey, client) -> {

            log.info("Creating user credentials");

            client.put(format("user/%s/credentials", "test_user"),
                    RUserCredentials.builder().userSecret(
                            "1".getBytes(StandardCharsets.ISO_8859_1)).build());

          
        });
    }
}
