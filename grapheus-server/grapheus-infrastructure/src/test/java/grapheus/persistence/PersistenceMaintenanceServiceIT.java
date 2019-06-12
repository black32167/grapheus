/**
 * 
 */
package grapheus.persistence;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import javax.inject.Inject;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.ComponentScan.Filter;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.FilterType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.arangodb.ArangoDBException;

import lombok.AllArgsConstructor;
import grapheus.persistence.conpool.DBConnectionPool;

/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader=AnnotationConfigContextLoader.class)
@TestPropertySource
public class PersistenceMaintenanceServiceIT {
    @AllArgsConstructor
    private static class TestUser {
        String name;
    }

    @Configuration
    @ComponentScan(basePackages = "grapheus", includeFilters=@Filter(type=FilterType.ASSIGNABLE_TYPE, value={
            DBConnectionPool.class
    }))
    static class Context {
        
    }
    
    @Inject
    private DBConnectionPool arangoDriverProvider;

    @Test
    public void dbTestDocumentSave() throws ArangoDBException {
        TestUser storedDocument = new TestUser("name1");
        TestUser restoredDocument = arangoDriverProvider.query(db -> {

            String docId = db.collection("user").insertDocument(storedDocument).getId();
            return db.getDocument(docId, TestUser.class);
        }); 
        
        assertEquals(restoredDocument.name, storedDocument.name);
        assertNotEquals(restoredDocument, storedDocument);
    }
    
    @Test
    public void dbTestDocumentUpdate() throws ArangoDBException {
        TestUser storedDocument = new TestUser("name1");
        arangoDriverProvider.update(db -> {
            String docKey = db.collection("user").insertDocument(storedDocument).getKey();
            TestUser restoredDocument = db.collection("user").getDocument(docKey, TestUser.class);
            restoredDocument.name = "name2";
    
            db.collection("user").updateDocument(docKey, restoredDocument);
            
            TestUser updatedDocument = db.collection("user").getDocument(docKey, TestUser.class);
            
            assertEquals(updatedDocument.name, "name2");
        });

    }
}
