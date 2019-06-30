package grapheus.persistence;

import com.arangodb.ArangoDBException;

/**
 * Utility class helping to classify error codes
 */
public final class ArangoDBExceptionUtil {
    public static boolean isDocumentNotFound(ArangoDBException adbe) {
        return adbe.getResponseCode() != null && adbe.getResponseCode() == 404;
    }
}
