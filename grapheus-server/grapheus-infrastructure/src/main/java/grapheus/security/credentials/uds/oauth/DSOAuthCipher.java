/**
 * 
 */
package grapheus.security.credentials.uds.oauth;

import org.springframework.stereotype.Service;

import grapheus.persistence.model.common.creds.DSOAuthCredentials;
import grapheus.security.credentials.uds.SpecificDSCredentialsCipher;

/**
 * @author black
 *
 */
@Service
//@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DSOAuthCipher implements SpecificDSCredentialsCipher<DSOAuthCredentials> {

   // private final PasswordCipher passwordCipher;
    
    @Override
    public DSOAuthCredentials decode(byte[] key, DSOAuthCredentials creds) {
        return creds;//process(creds, p -> passwordCipher.decodePassword(key, p));
    }

    @Override
    public DSOAuthCredentials encode(byte[] key, DSOAuthCredentials creds) {
        return creds;//process(creds, p -> passwordCipher.encodePassword(key, p));
    }
    /*
    private DSOAuthCredentials process(DSOAuthCredentials creds, Function<byte[], byte[]> processor) {
        return Optional.ofNullable(creds.getClientSecret()).
                map(processor).
                map(p -> creds.withClientSecret(p)).
                orElse(creds);
    }*/
}
