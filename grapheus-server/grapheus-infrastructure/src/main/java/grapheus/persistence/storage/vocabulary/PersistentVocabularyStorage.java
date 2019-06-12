/**
 * 
 */
package grapheus.persistence.storage.vocabulary;

import java.util.Optional;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Repository;

import com.arangodb.ArangoDatabase;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import grapheus.CacheManagers;
import grapheus.persistence.StorageSupport;
import grapheus.persistence.datasource.ArangoCursorDatasource;
import grapheus.persistence.datasource.ClosableDataSource;
import grapheus.persistence.model.vocabulary.VocabularyTerm;

/**
 * @author black
 */
@Repository
@Slf4j
public class PersistentVocabularyStorage extends StorageSupport implements VocabularyStorage {

    @Override
    public void onConnected(ArangoDatabase db) {
        createCollection(db, VocabularyTerm.class);
    }

    @Cacheable(value="getMentionsCount", cacheManager=CacheManagers.MULTIPLE)
    @Override
    public int getMentionsCount(String scope, String term) {
        int count = query().//
                from(VocabularyTerm.class).//
                filter(VocabularyTerm.FIELD_SCOPE, scope).//
                filter(VocabularyTerm.FIELD_TERM, term).//
                first().
                map(t -> t.getMentionsCount()). //
                orElse(0);
        //log.debug("Mentions count for term {}:{}", term, count);
        return count;
    }

    @Override
    public ClosableDataSource<VocabularyTerm> getTerms(String scope) {
        return ArangoCursorDatasource.from(
                query().from(VocabularyTerm.class).//
                filter(VocabularyTerm.FIELD_SCOPE, scope).//
                ret());
    }


    @Cacheable(value="getTotalCount", cacheManager=CacheManagers.MULTIPLE)
    @Override
    public int getTotalCount(String scope) {
        Long totalTerms = query().//
                from(VocabularyTerm.class).//
                filter(VocabularyTerm.FIELD_SCOPE, scope).//
                aggregate("SUM", VocabularyTerm.FIELD_COUNT, Long.class);
        log.debug("Total terms: {}", totalTerms);
        return totalTerms == null ? 0 : totalTerms.intValue();
    }
    

    @Override
    public void addTermMentionsCount(@NonNull String scope, @NonNull String term, @NonNull Integer countInDocument) {
        try {
            
            Optional<VocabularyTerm> maybeTerm = query().from(VocabularyTerm.class).//
                filter(VocabularyTerm.FIELD_SCOPE, scope).//
                filter(VocabularyTerm.FIELD_TERM, term).//
                first();
            if(maybeTerm.isPresent()) {
                VocabularyTerm termItem = maybeTerm.get();
                termItem.setMentionsCount(termItem.getMentionsCount() + countInDocument);
                updateDocument(termItem);
            } else {
                createDocument(VocabularyTerm.builder().//
                        scope(scope).//
                        term(term).//
                        mentionsCount(countInDocument).//
                        build());
            };
           
        } catch(Throwable e) {
            log.debug("Cannot register term:{}", term);
            throw e;
        }
        
    }

}
