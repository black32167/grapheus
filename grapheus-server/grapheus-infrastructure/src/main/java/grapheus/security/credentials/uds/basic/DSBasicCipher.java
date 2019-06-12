/**
 * 
 */
package grapheus.security.credentials.uds.basic;

import java.util.Optional;
import java.util.function.Function;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import grapheus.persistence.model.common.creds.DSBasicCredentials;
import grapheus.security.credentials.uds.PasswordCipher;
import grapheus.security.credentials.uds.SpecificDSCredentialsCipher;

/**
 * @author black
 *
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DSBasicCipher implements SpecificDSCredentialsCipher<DSBasicCredentials> {
    private final PasswordCipher passwordCipher;
    
    public DSBasicCredentials encode(byte[] key, DSBasicCredentials creds) {
        return process(creds, p -> passwordCipher.encodePassword(key, p));
    }

    @Override
    public DSBasicCredentials decode(byte[] key, DSBasicCredentials creds) {
        return process(creds, p -> passwordCipher.decodePassword(key, p));
    }
    
    private DSBasicCredentials process(DSBasicCredentials creds, Function<byte[], byte[]> processor) {
        return Optional.ofNullable(creds).
                map(DSBasicCredentials::getUserPassword).
                map(processor).
                map(p -> new DSBasicCredentials(
                        creds.getUserName(),
                        p)).
                orElse(creds);
    }
}
