/**
 * 
 */
package grapheus.security.credentials.uds;
import grapheus.persistence.model.common.creds.DSCredentials;

/**
 * @author black
 *
 */
public interface SpecificDSCredentialsCipher<T extends DSCredentials> {

    T decode(byte[] key, T creds);
    
    T encode(byte[] key, T creds);
}
