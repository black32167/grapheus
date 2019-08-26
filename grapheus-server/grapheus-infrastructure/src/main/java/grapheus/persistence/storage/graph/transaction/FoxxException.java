package grapheus.persistence.storage.graph.transaction;

import grapheus.persistence.exception.StorageException;

public class FoxxException extends StorageException {
    public FoxxException(String message) {
        super(message);
    }

    public FoxxException(String message, Throwable cause) {
        super(message, cause);
    }
}
