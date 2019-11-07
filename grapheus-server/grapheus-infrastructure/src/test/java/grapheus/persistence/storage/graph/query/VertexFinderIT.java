/**
 * 
 */
package grapheus.persistence.storage.graph.query;

import grapheus.it.TestConstants;
import grapheus.it.util.GraphTestSupport;
import grapheus.persistence.exception.GraphExistsException;
import grapheus.persistence.model.graph.PersistentVertex;
import grapheus.persistence.storage.graph.impl.DefaultVertexStorage;
import grapheus.persistence.storage.graph.query.VertexFinder.SearchResult;
import grapheus.persistence.storage.graph.query.impl.DefaultVertexFinder;
import grapheus.persistence.testutil.DbTestsContextConfig;
import grapheus.service.uds.ArtifactsFilter;
import grapheus.view.SemanticFeature;
import grapheus.view.extract.features.SemanticFeatureType;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author black
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(loader = AnnotationConfigContextLoader.class, classes={
        DbTestsContextConfig.class, DefaultVertexStorage.class, DefaultVertexFinder.class
})
@TestPropertySource(TestConstants.DB_PROPERTIES)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class VertexFinderIT extends GraphTestSupport {

    private static final String LOCAL_ID_1 = "id1";
    private static final String LOCAL_ID_2 = "id2";
    private static final String LOCAL_ID_3 = "id3";
    private static final String LOCAL_ID_4 = "id4";
    private static final String DS_ID_1 = "dsId1";
    private static final String DS_ID_2 = "dsId2";
    private static final String GRAPH_NAME = "graph1";
    
    @Inject
    private VertexFinder finder; // Service under test
    
    @Test
    public void testFindByHint() throws GraphExistsException {

        PersistentVertex artifact2 = partifact(DS_ID_1, LOCAL_ID_2);
        artifact2.getSemanticFeatures().put( //
                SemanticFeatureType.LOCAL_ID_REFERENCE, //
                SemanticFeature.builder(). //
                    feature(SemanticFeatureType.LOCAL_ID_REFERENCE).value(DS_ID_1+":"+LOCAL_ID_1). //
                    build());
        
        graph(GRAPH_NAME).
            vertex(partifact(DS_ID_1, LOCAL_ID_1)).
            vertex(artifact2).
            vertex(partifact(DS_ID_1, LOCAL_ID_3)).
            vertex(partifact(DS_ID_2, LOCAL_ID_4)).
            build();

        List<PersistentVertex> artifacts = new ArrayList<>();
        finder.findVerticesByFeature(
                GRAPH_NAME,
                SemanticFeatureType.LOCAL_ID_REFERENCE,
                Collections.singleton(DS_ID_1+":"+LOCAL_ID_1),
                Collections.singleton("excl"),
                artifacts::add);
        List<String> retrievedIds = artifacts.stream().map(a -> a.getId()).collect(Collectors.toList());
        
        assertEquals(2, retrievedIds.size());
        assertTrue(retrievedIds.containsAll(Arrays.asList(DS_ID_1 + ":" + LOCAL_ID_1, DS_ID_1 + ":" + LOCAL_ID_2)));
    }

    @Test
    public void testCount() throws GraphExistsException {

        graph(GRAPH_NAME).
            vertex(partifact(DS_ID_1, LOCAL_ID_1)).
            vertex(partifact(DS_ID_1, LOCAL_ID_3)).
            vertex(partifact(DS_ID_1, LOCAL_ID_4)).
            build();

        SearchResult sresult =  finder.findVerticesByCriteria(
                GRAPH_NAME,
                ArtifactsFilter.builder().limit(1).build(),
                Collections.emptyList());
        
        assertEquals(1, sresult.getVertices().size());
        assertEquals(3, sresult.getTotalCount());
      
    }

    private PersistentVertex partifact(String dsId, String artifactLocalId) {
       
        PersistentVertex v = PersistentVertex.builder().//
                sourceId(dsId).//
                id(artifactLocalId).//
                id(dsId + ":" + artifactLocalId).//
                build();
        v.getSemanticFeatures().put(//
                SemanticFeatureType.LOCAL_ID_REFERENCE,//
                SemanticFeature.builder().//
                    feature(SemanticFeatureType.LOCAL_ID_REFERENCE).//
                    value(dsId+":"+artifactLocalId).build());
        return v;
    }


}
