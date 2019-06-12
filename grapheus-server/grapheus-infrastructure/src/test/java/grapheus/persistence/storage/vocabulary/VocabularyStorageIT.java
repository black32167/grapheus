/**
 * 
 */
package grapheus.persistence.storage.vocabulary;

import java.util.List;

import javax.inject.Inject;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import grapheus.it.TestConstants;
import grapheus.persistence.StorageSupport;
import grapheus.persistence.model.vocabulary.VocabularyTerm;
import grapheus.persistence.testutil.DbTestsContextConfig;


/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        DbTestsContextConfig.class, PersistentVocabularyStorage.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
public class VocabularyStorageIT extends StorageSupport {
    private final static String TERM1 = "word1";
    private final static String TERM2 = "word2";
    private static final String DEFAULT_SCOPE = "";
    
    @Inject
    private PersistentVocabularyStorage vocabularyStorage;

    @Test
    public void testAddTerm() {
        vocabularyStorage.addTermMentionsCount(DEFAULT_SCOPE, TERM1, 3);
        vocabularyStorage.addTermMentionsCount(DEFAULT_SCOPE, TERM2, 1);
        vocabularyStorage.addTermMentionsCount(DEFAULT_SCOPE, TERM1, 2);
        
        int mentionsCount1 = vocabularyStorage.getMentionsCount(DEFAULT_SCOPE, TERM1);
        Assert.assertEquals(5,  mentionsCount1);
        int mentionsCount2 = vocabularyStorage.getMentionsCount(DEFAULT_SCOPE, TERM2);
        Assert.assertEquals(1,  mentionsCount2);
        int totalCount = vocabularyStorage.getTotalCount(DEFAULT_SCOPE);
        Assert.assertEquals(6,  totalCount);
    }

    @Test
    public void testStatisticsRetrieval() {
        vocabularyStorage.addTermMentionsCount(DEFAULT_SCOPE, TERM1, 3);
        vocabularyStorage.addTermMentionsCount(DEFAULT_SCOPE, TERM1, 3);
        vocabularyStorage.addTermMentionsCount(DEFAULT_SCOPE, TERM2, 1);

        List<VocabularyTerm> termsList = query().from(VocabularyTerm.class).list();

        Assert.assertEquals(2, termsList.size());
     
    }

}
