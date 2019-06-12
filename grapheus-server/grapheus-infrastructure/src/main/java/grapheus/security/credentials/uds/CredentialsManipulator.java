/**
 * 
 */
package grapheus.security.credentials.uds;

import grapheus.persistence.model.common.creds.DSCredentials;

/**
 * @author black
 */
public interface CredentialsManipulator {
    DSCredentials get();
    void set(DSCredentials dsCreds);
}
