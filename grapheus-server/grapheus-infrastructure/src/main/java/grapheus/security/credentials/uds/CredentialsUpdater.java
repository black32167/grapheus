package grapheus.security.credentials.uds;

import grapheus.persistence.model.common.creds.DSCredentials;

public interface CredentialsUpdater<T extends DSCredentials> {
    T update(T sourceCreds);
}