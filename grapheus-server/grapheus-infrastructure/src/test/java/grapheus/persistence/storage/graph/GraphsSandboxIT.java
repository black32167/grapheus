/**
 * 
 */
package grapheus.persistence.storage.graph;

import static java.util.Collections.singletonList;

import java.util.Collections;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import com.arangodb.entity.EdgeDefinition;
import com.arangodb.model.CollectionsReadOptions;
import com.arangodb.model.HashIndexOptions;

import grapheus.it.TestConstants;
import grapheus.persistence.StorageSupport;
import grapheus.persistence.testutil.DbTestsContextConfig;

/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        DbTestsContextConfig.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
@Ignore
public class GraphsSandboxIT extends StorageSupport {
    @Test
    public void test() {
        int CREATED_GRAPHS_CNT = 1000;
        for(int i = 0; i < CREATED_GRAPHS_CNT; i++) {
            final int graphIdx = i;
            update(db -> {
                //  db.createCollection("C1");
                db.createGraph("G1_"+graphIdx, singletonList(new EdgeDefinition().//
                        collection("E1_"+graphIdx).//
                        from("V1_"+graphIdx).//
                        to("V1_"+graphIdx)));
                db.collection("V1_"+graphIdx).ensureHashIndex(Collections.singleton("field1"), new HashIndexOptions());
              //  db.graph("G1_"+graphIdx).vertexCollection().in
            });
            
        }
        
        int actualGraphsCount = query(db-> db.getGraphs().size());
        Assert.assertEquals("Graphs count", CREATED_GRAPHS_CNT, actualGraphsCount);
        
        long actualVCollectionsCount = query(db->db.getCollections(new CollectionsReadOptions().excludeSystem(true)).//
                stream().filter(c->c.getName().startsWith("V1_")).count());
        Assert.assertEquals("Vertices collections count", CREATED_GRAPHS_CNT, actualVCollectionsCount);
    }
}
