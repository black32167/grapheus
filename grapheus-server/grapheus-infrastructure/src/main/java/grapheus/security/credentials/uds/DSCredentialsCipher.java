/**
 * 
 */
package grapheus.security.credentials.uds;

import javax.inject.Inject;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import grapheus.persistence.model.common.creds.DSBasicCredentials;
import grapheus.persistence.model.common.creds.DSOAuthCredentials;
import grapheus.persistence.model.common.creds.DSCredentials;
import grapheus.security.credentials.uds.basic.DSBasicCipher;
import grapheus.security.credentials.uds.oauth.DSOAuthCipher;

/**
 * @author black
 *
 */
@Service
@RequiredArgsConstructor(onConstructor = @__({@Inject}))
public class DSCredentialsCipher {
    private final DSOAuthCipher oauthEncoderDecoder;
    private final DSBasicCipher basicEncoderDecoder;
    
    public DSCredentials encode(byte[] key, DSCredentials creds) {
        SpecificDSCredentialsCipher<DSCredentials> encoder = getEncoderDecoder(creds);
        
        return encoder.encode(key, creds);
    }

    public DSCredentials decode(byte[] key, DSCredentials creds) {
        SpecificDSCredentialsCipher<DSCredentials> decoder = getEncoderDecoder(creds);
        
        return decoder.decode(key, creds);
      
    }

    private <T extends DSCredentials> SpecificDSCredentialsCipher<T> getEncoderDecoder(T creds) {
        if(creds instanceof DSOAuthCredentials) {
            return (SpecificDSCredentialsCipher<T>) oauthEncoderDecoder;
        } else if (creds instanceof DSBasicCredentials) {
            return (SpecificDSCredentialsCipher<T>) basicEncoderDecoder;
        }
        throw new IllegalArgumentException("Unknown credentials type: " + creds);
    }



}
