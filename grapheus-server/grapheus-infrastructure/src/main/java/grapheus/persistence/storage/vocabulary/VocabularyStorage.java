/**
 * 
 */
package grapheus.persistence.storage.vocabulary;

import grapheus.persistence.datasource.ClosableDataSource;
import grapheus.persistence.model.vocabulary.VocabularyTerm;

/**
 * @author black
 *
 */
public interface VocabularyStorage {
    int getTotalCount(String scope);
    ClosableDataSource<VocabularyTerm> getTerms(String scope);
    int getMentionsCount(String scope, String term);
    void addTermMentionsCount(String scope, String term, Integer countInDocument);


}
