/**
 * 
 */
package grapheus.persistence;

import static java.lang.String.format;
import static grapheus.persistence.ArangoDBErrors.COLLECTION_NOT_FOUND;
import static grapheus.persistence.ArangoDBErrors.DB_NOT_FOUND;
import static grapheus.persistence.ArangoDBErrors.DUPLICATE_NAME;
import static grapheus.persistence.ArangoDBErrors.ERROR_ARANGO_CONFLICT;
import static grapheus.persistence.ArangoDBErrors.UNIQUE_CONSTRAINT_VIOLATED;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.inject.Inject;

import com.arangodb.ArangoCollection;
import com.arangodb.ArangoCursor;
import com.arangodb.ArangoDBException;
import com.arangodb.ArangoDatabase;
import com.arangodb.model.AqlQueryOptions;
import com.arangodb.model.CollectionCreateOptions;
import com.arangodb.model.DocumentImportOptions;
import com.arangodb.model.DocumentImportOptions.OnDuplicate;
import com.arangodb.model.DocumentReplaceOptions;
import com.arangodb.model.DocumentUpdateOptions;
import com.arangodb.model.HashIndexOptions;
import com.arangodb.model.SkiplistIndexOptions;

import grapheus.persistence.exception.DocumentNotFoundException;
import lombok.extern.slf4j.Slf4j;
import grapheus.event.OnAfterDbConnectionListener;
import grapheus.persistence.conpool.DBConnectionPool;
import grapheus.persistence.conpool.DBConnectionPool.DBConnectionConsumer;
import grapheus.persistence.conpool.DBConnectionPool.DBConnectionUpdateConsumer;
import grapheus.persistence.exception.CollectionNotFoundException;
import grapheus.persistence.exception.DatabaseNotFoundException;
import grapheus.persistence.exception.DocumentExistsException;
import grapheus.persistence.exception.DocumentsConflictException;
import grapheus.persistence.exception.StorageException;
import grapheus.persistence.model.ModelMeta;
import grapheus.persistence.query.AQLBuilderFactory;

/**
 * @author black
 */
@Slf4j
public class StorageSupport implements OnAfterDbConnectionListener {

    @Inject
    private DBConnectionPool arangoDriverProvider;

    protected void createCollection(ArangoDatabase db, Class<?> entityClass) {
        String collectionName = ModelMeta.getCollectionName(entityClass);
        createCollection(db, entityClass, collectionName);
    }
    
    protected void createCollection(ArangoDatabase db, Class<?> entityClass, String collectionName) {
        try {
            // Create collection
            db.createCollection(collectionName, new CollectionCreateOptions().isVolatile(true));
            
            // Add indexes if needed
            createIndexes(db, entityClass, collectionName);
            
            log.info("Collection '{}' created", collectionName);   
        } catch (ArangoDBException e) {
            if(e.getErrorNum() == DUPLICATE_NAME) {
                log.info("Collection '{}' seems exist", collectionName);
            } else {
                log.error("", e);
                throw throwStorageException("", e);
            }
        }
    }

    protected void createIndexes(ArangoDatabase db, Class<?> entityClass, String collectionName) {
        ModelMeta.getIndexes(entityClass).stream().forEach((i) -> {
            ArangoCollection collection = db.collection(collectionName);
            List<String> fields = Arrays.asList(i.fields());
            switch (i.type()) {
            case HASH:collection.ensureHashIndex(
                    fields,
                    new HashIndexOptions().unique(i.unique()));
            break;
            case SKIP:collection.ensureSkiplistIndex(
                    fields, new SkiplistIndexOptions().unique(i.unique()));
            }
        });
    }
    
    protected <T> T query(DBConnectionConsumer<T> connectionConsumer) {
        return arangoDriverProvider.query(connectionConsumer);
    }
    protected <T> ArangoCursor<T> q(String aql, Map<String, Object> parameters, Class<T> targetClass) {
        return q(aql, parameters, null, targetClass);
    }
    protected <T> ArangoCursor<T> q(String aql, Map<String, Object> parameters, AqlQueryOptions opts, Class<T> targetClass) {
        return arangoDriverProvider.query(db->db.query(aql, parameters, opts, targetClass));
    }
    
    protected void update(DBConnectionUpdateConsumer connectionConsumer) {
        arangoDriverProvider.update(connectionConsumer);
    }
    
    
    protected AQLBuilderFactory query() {
        return new AQLBuilderFactory(arangoDriverProvider);
    }
    
    protected <T> Optional<T> findDocumentByField(String collectionName, String fieldName, String value, Class<T> entityClass) {
        String query = String.format("FOR t IN %s FILTER t.%s == @value RETURN t", collectionName, fieldName);
        List<T> list = executeAqlQuery(query, Collections.singletonMap("value", value), entityClass);
        if(list.size() == 0) {
            return Optional.empty();
        }
        if(list.size() > 1) {
            throw new RuntimeException(
                    String.format("Found more then one document '%s' by clause %s=%s", collectionName, fieldName, value));
        }
        return Optional.of(list.get(0));
    }

    protected <T> List<T> executeAqlQuery(String aql, Map<String, Object> parameters, Class<T> entityClass) {
        try {
            return query(db -> db.query(aql, parameters, null, entityClass).asListRemaining());
        } catch (ArangoDBException e) {
            throw throwStorageException("", e);
        }
    }


    protected <T> void updateDocument(String collectionName, String documentKey, Class<T> entityClass,
            Consumer<T> consumer) {
        updateDocument(collectionName, documentKey, entityClass, (T e) -> {
            consumer.accept(e);
            return e;
        });
    }

    protected boolean exists(String collectionName, String documentKey) {
        try {
            return query(db -> db.collection(collectionName).documentExists(documentKey));
        } catch (ArangoDBException e) {
            throw throwStorageException(format("Cannot check existence of item '%s' from collection '%s'", documentKey, collectionName), e); 
        }
    }
    
    protected <T> void updateDocument(String collectionName, String documentKey, Class<T> entityClass,
            DocumentUpdateConsumer<T> consumer) {
        try {
            T de = query(db -> db.collection(collectionName).getDocument(documentKey, entityClass));
            T updated = consumer.update(de);
            update(db -> db.collection(collectionName).updateDocument(documentKey, updated));
        } catch (ArangoDBException e) {
            throw throwStorageException(format("Cannot update item '%s' from collection '%s'", documentKey, collectionName), e); 
        }
    }
    
    protected <T> void updateDocument(String collectionName, String documentKey, T document) {
        try {
            update(db -> db.collection(collectionName).replaceDocument(documentKey, document, new DocumentReplaceOptions().ignoreRevs(false)));
        } catch (ArangoDBException e) {
            throw throwStorageException(format("Cannot update item '%s' from collection '%s'", documentKey, collectionName), e); 
        }
    }

    protected <T> void updateDocuments(String collectionName, Collection<T> documents) {
        try {
            update(db -> db.collection(collectionName)
                    .importDocuments(documents, new DocumentImportOptions()
                            .onDuplicate(OnDuplicate.update).waitForSync(false)));
        } catch (ArangoDBException e) {
            throw throwStorageException(format("Cannot bulk update items in collection '%s'", collectionName), e); 
        }
    }

    protected <T> void partiallyUpdateDocument(String collectionName, T document) {
        String entityId = ModelMeta.getDocumentId(document);
        try {
            update(db -> db.collection(collectionName).updateDocument(entityId, document, new DocumentUpdateOptions().serializeNull(false)));
        } catch (ArangoDBException e) {
            throw throwStorageException(format("Cannot partially update item '%s' from collection '%s'", entityId, collectionName), e); 
        }
    }
    

    protected <T> void updateDocument(Object documentEntity) {
        updateDocument(
                ModelMeta.getCollectionName(documentEntity.getClass()),
                documentEntity);
    }
    
    protected <T> void updateDocument(String collectionName, Object documentEntity) {
        String entityId = ModelMeta.getDocumentId(documentEntity);
        updateDocument(
                collectionName,
                entityId,
                documentEntity);
    }
    
    protected boolean documentExists(String collectionName, String userKey) {
        return query(db -> db.collection(collectionName).documentExists(userKey));
    }
    
    protected <T> T getDocument(String collectionName, String documentKey, Class<T> entityClass) {
        try {
            T de = query(db -> db.collection(collectionName).getDocument(documentKey, entityClass));
            return de;
        } catch (ArangoDBException e) {
            throw throwStorageException(format("Cannot get item '%s' for collection '%s'", documentKey, collectionName), e); 
        }
    }
    
    protected <T> Optional<T> findDocument(Class<T> entityClass, String documentKey) {
        String collectionName = ModelMeta.getCollectionName(entityClass);
        
        return findDocument(collectionName, documentKey, entityClass);
    }
    
    protected <T> Optional<T> findDocument(String collectionName, String documentKey, Class<T> entityClass) {
        return exists(collectionName, documentKey) ?
                Optional.of(getDocument(collectionName, documentKey, entityClass)) :
                    Optional.empty();
    }
    
    

    protected String createDocument(String collectionName, Object document) {
        try {
            return  query(db -> db.collection(collectionName).insertDocument(document).getKey());
        } catch (ArangoDBException e) {
            throw throwStorageException(format("Cannot create item '%s' for collection '%s'", document, collectionName), e);
            
        }
    }

    protected String createDocument(Object documentEntity) {
        return createDocument(
                ModelMeta.getCollectionName(documentEntity.getClass()),
                documentEntity);
    }
    
    protected <T> List<T> listAll(String collectionName, Class<T> entityClass, int start, int limit) {
        try {
            Map<String,Object> params = new HashMap<>();
         
            params.put("start", start);
            params.put("limit", limit);
             
            return  query(db -> db.query(format("FOR t IN %s LIMIT @start, @limit RETURN t", collectionName),
                    params,
                    null,
                    entityClass).asListRemaining());
        } catch (ArangoDBException e) {
            throw new StorageException(format("Cannot list collection '%s'", collectionName), e);
        }
    }

    protected void deleteDocument(String collectionName, String documentKey) {
        try {
            query(db -> db.collection(collectionName).deleteDocument(documentKey));
        } catch (ArangoDBException e) {
            throw new StorageException(
                    format("Cannot delete item '%s' from collection '%s'", documentKey, collectionName), e);
        }
    }
    
    protected void deleteDocument(Class<?> entityClass, String documentKey) {
        deleteDocument(ModelMeta.getCollectionName(entityClass), documentKey);
    }
    
    protected void deleteDocument(Object documentEntity) {
        String entityId = ModelMeta.getDocumentId(documentEntity);
        deleteDocument(ModelMeta.getCollectionName(documentEntity.getClass()), entityId);
    }

    protected void removeDocumentsByFilter(String collectionName, Map<String, Object> example) {
        StringBuilder query = new StringBuilder("FOR c in " + collectionName + " FILTER ");
        String filter = example.entrySet().stream().//
                map((e) -> "c."+ e.getKey() + " == @" + e.getKey()).//
                collect(Collectors.joining(" && "));
        query.append(filter);
        query.append(" REMOVE c IN " + collectionName);

        try {
            query(db -> db.query(query.toString(), example, null, Void.TYPE));
        } catch (ArangoDBException e) {
            throw new StorageException(
                    format("Cannot remove item from collection '%s' by filter '%s'", collectionName, example), e);
        }
    }

    @Override
    public void onConnected(ArangoDatabase db) {
        
    }


    protected StorageException throwStorageException(String msg, ArangoDBException dbe) {
        if(dbe.getResponseCode() == 404) {
            return new DocumentNotFoundException(msg, dbe);
        } else if(dbe.getErrorMessage() != null/*Bug circumvention*/) {
            switch(dbe.getErrorNum()) {
            case COLLECTION_NOT_FOUND: return new CollectionNotFoundException(msg);
            case DB_NOT_FOUND:return new DatabaseNotFoundException(msg, dbe);
            case UNIQUE_CONSTRAINT_VIOLATED :return new DocumentExistsException(msg, dbe);
            case ERROR_ARANGO_CONFLICT:return new DocumentsConflictException(msg, dbe);
            }
        }
        return new StorageException(msg, dbe);
    }
}
