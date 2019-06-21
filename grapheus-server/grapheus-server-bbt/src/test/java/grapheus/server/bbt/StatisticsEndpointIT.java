/**
 * 
 */
package grapheus.server.bbt;

import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;
import org.grapheus.client.GrapheusClientFactory;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.grapheus.client.model.statistics.RTermStatistics;

/**
 * @author black
 */
@Slf4j
public class StatisticsEndpointIT extends AbstractUserBasedIT {
    @Test
    public void termsStatisticsRetrievingTest() {
//        withUser((userKey, client) -> {
//            TODO: ^pass GrapheusClientFactory^
//            client.put("artifact/"+getCurrentScope(), RVertex.builder().//
//                    description("Mars base, blocked by MaRs").//
//                    title("Mars base").//
//                    id("MRS-2").//
//                    build());
//            try {
//                Thread.sleep(500);
//            } catch  (Exception e) {}
//
//            RTermStatistics stats = client.get("vocabulary/"+getCurrentScope(), RTermStatistics.class);
//
//            Assert.assertNotNull(stats);
//            Assert.assertEquals(4, stats.getTermCounts().size());
//        });
    }
}
