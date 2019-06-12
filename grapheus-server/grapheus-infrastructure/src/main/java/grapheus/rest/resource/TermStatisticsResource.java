/**
 * 
 */
package grapheus.rest.resource;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.grapheus.client.model.statistics.RTermData;
import org.grapheus.client.model.statistics.RTermStatistics;

import grapheus.persistence.datasource.ClosableDataSource;
import grapheus.persistence.model.vocabulary.VocabularyTerm;
import grapheus.persistence.storage.vocabulary.VocabularyStorage;

/**
 * @author scircel
 */
@Path(TermStatisticsResource.PATH)
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class TermStatisticsResource {
    final static String PATH = "/vocabulary";
    
    @Inject
    private VocabularyStorage termsStatisticsStorage;

    @GET
    @Path("{scope}")
    public RTermStatistics listTerms(@QueryParam("term") String term, @PathParam("scope") String scope) {

        List<RTermData> terms = new ArrayList<>();
        try(ClosableDataSource<VocabularyTerm> persistentTerms = termsStatisticsStorage.getTerms(scope)) {
            persistentTerms.forEach(pt -> terms.add(persistentTermToExternal(pt)));
        }

        return RTermStatistics.builder().
                totalCount(termsStatisticsStorage.getTotalCount(scope)).
                termCounts(terms).
                build();
    }

    private RTermData persistentTermToExternal(VocabularyTerm pt) {
        return RTermData.builder().
                term(pt.getTerm()).
                count(pt.getMentionsCount()).
                build();
    }

}
