/**
 * 
 */
package grapheus.persistence.storage.graph.query;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.grapheus.client.model.graph.SortDirection;
import org.grapheus.client.model.graph.VerticesSortCriteria;
import org.grapheus.client.model.graph.VerticesSortCriteriaType;

/**
 * Deserialization test.
 * 
 * @author black    
 */
public class VerticesSortCriteriaTest {
    private final static String SAMPLE_SERIALIZED_CRITERIA1_ASC = VerticesSortCriteriaType.OUT_EDGES_COUNT.getAlias() + "_" + SortDirection.ASC.name();
    private final static String SAMPLE_SERIALIZED_CRITERIA1_DESC = VerticesSortCriteriaType.OUT_EDGES_COUNT.getAlias() + "_" + SortDirection.DESC.name();
    private final static String SAMPLE_SERIALIZED_CRITERIA2_ASC = VerticesSortCriteriaType.VERTEX_TITLE.getAlias() + "_" + SortDirection.ASC.name();
        
    @Test
    public void testDeserializationSinglCriteriaAsc() {
        List<VerticesSortCriteria> parsedCriteria = VerticesSortCriteria.deserializeSortingCriteria(SAMPLE_SERIALIZED_CRITERIA1_ASC);
        
        Assert.assertEquals(1, parsedCriteria.size());
        
        VerticesSortCriteria criteria = parsedCriteria.get(0);
        
        Assert.assertEquals(VerticesSortCriteriaType.OUT_EDGES_COUNT, criteria.getSortingType());
        Assert.assertEquals(SortDirection.ASC, criteria.getSortDirection());
    }

    @Test
    public void testDeserializationSinglCriteriaDesc() {
        List<VerticesSortCriteria> parsedCriteria = VerticesSortCriteria.deserializeSortingCriteria(SAMPLE_SERIALIZED_CRITERIA1_DESC);
        
        Assert.assertEquals(1, parsedCriteria.size());
        
        VerticesSortCriteria criteria = parsedCriteria.get(0);
        
        Assert.assertEquals(VerticesSortCriteriaType.OUT_EDGES_COUNT, criteria.getSortingType());
        Assert.assertEquals(SortDirection.DESC, criteria.getSortDirection());
    }
    

    @Test
    public void testDeserializationMultipleCriteria() {
        List<VerticesSortCriteria> parsedCriteria = VerticesSortCriteria.deserializeSortingCriteria(SAMPLE_SERIALIZED_CRITERIA1_DESC + ", " + SAMPLE_SERIALIZED_CRITERIA2_ASC);
        
        Assert.assertEquals(2, parsedCriteria.size());
        
        VerticesSortCriteria criteria1 = parsedCriteria.get(0);
        Assert.assertEquals(VerticesSortCriteriaType.OUT_EDGES_COUNT, criteria1.getSortingType());
        Assert.assertEquals(SortDirection.DESC, criteria1.getSortDirection());

        VerticesSortCriteria criteria2 = parsedCriteria.get(1);
        Assert.assertEquals(VerticesSortCriteriaType.VERTEX_TITLE, criteria2.getSortingType());
        Assert.assertEquals(SortDirection.ASC, criteria2.getSortDirection());
    }

}
