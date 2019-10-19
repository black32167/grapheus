/**
 * 
 */
package org.grapheus.cli.subcommand.analytics;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.grapheus.cli.security.CLIUserContext;
import org.grapheus.cli.subcommand.GrapheusCLICommand;
import org.grapheus.cli.subcommand.processor.CommandProcessingException;
import org.grapheus.cli.subcommand.processor.CommandProcessor;
import org.grapheus.client.GrapheusClientFactory;
import org.grapheus.client.UserClient;
import org.grapheus.client.api.ComputeAPI;
import org.grapheus.client.api.DataStatisticsAPI;
import org.grapheus.client.api.VertexAPI;
import org.grapheus.client.api.VerticesFilter;
import org.grapheus.client.model.graph.edge.REdge;
import org.grapheus.client.model.graph.vertex.RVertex;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Optional.ofNullable;

/**
 * @author black
 */
@Service
@RequiredArgsConstructor(onConstructor=@__({@Inject}))
public class AnalyticsCommandProcessor implements CommandProcessor {
    private static final String DEFAULT_BRIDGE_PATTERN = "{titleFrom}->{titleTo}";
    private static final String DEFAULT_VERTEX_LIST_PATTERN = "{title}";
    private final GrapheusClientFactory rcFactory;
    private final CLIUserContext userCtx;
   
    @Override
    public void process(GrapheusCLICommand command) throws CommandProcessingException {
        AnalyticsCommand analyticsCommand = (AnalyticsCommand) command;
        UserClient userClient = rcFactory.forUser(userCtx.credentialsSupplier());
        ComputeAPI analyticsAPI = userClient.analytics();
        DataStatisticsAPI dstatAPI = userClient.dataStat();
        String pattern = analyticsCommand.getPattern();
        switch(analyticsCommand.getOperation()) {
        case "bridges":
            formatEdges(analyticsCommand.getGraph(), pattern, analyticsAPI.getBridges()).forEach(System.out::println); break;
        case "bridges-v":
            formatIds(analyticsCommand.getGraph(), pattern, 
                    edges2Vertices(analyticsAPI.getBridges())).forEach(System.out::println); break;
        case "sinks":
            formatIds(analyticsCommand.getGraph(), pattern, analyticsAPI.getSinks()).forEach(System.out::println); break;
        case "outbound":
            formatEdges(analyticsCommand.getGraph(), pattern, analyticsAPI.getOutbound(analyticsCommand.getParameters())).forEach(System.out::println); break;
        case "outbound-v":
            formatIds(analyticsCommand.getGraph(), pattern,
                    edges2Vertices(analyticsAPI.getOutbound(analyticsCommand.getParameters()))).forEach(System.out::println); break;
        case "count":
            System.out.println(dstatAPI.getDataStat().getArtifactsCount()); break;
        case "title":
            VerticesFilter filter = VerticesFilter.builder().title("%"+analyticsCommand.getParameters()+"%").build();
            userClient.vertex().//
                findVertices(analyticsCommand.getGraph(), filter).//
                getArtifacts().//
                forEach(a->System.out.println(format("{id} ('{title}')", a))); break;
        }
    }
    
    private Set<String> edges2Vertices(@NonNull Collection<REdge> outbound) {
        final Set<String> vertices = new HashSet<>();
        outbound.forEach(e -> {
            vertices.add(e.getFrom());
            vertices.add(e.getTo());
        });
        return vertices;
    }

    private List<String> formatEdges(String graph, String pattern, List<REdge> bridges) {
        Set<String> ids = new HashSet<String>();
        bridges.forEach(b->{
            ids.add(b.getFrom());
            ids.add(b.getTo());
        });
        
        final String fPattern = ofNullable(pattern).orElse(DEFAULT_BRIDGE_PATTERN);

        Collection<RVertex> artifacts = vertex().loadArtifacts(graph, ids);
        Map<String, RVertex> id2a = artifacts.stream().collect(Collectors.toMap(RVertex::getId, Function.identity()));
        return bridges.stream().map(b->{
            RVertex fromA = id2a.get(b.getFrom());
            RVertex toA = id2a.get(b.getTo());
            return fPattern.
                replace("{titleFrom}", fromA.getTitle()).
                replace("{idFrom}", fromA.getId()).
                replace("{titleTo}", toA.getTitle()).
                replace("{idTo}", toA.getId());
            }).collect(Collectors.toList());
    }
    private VertexAPI vertex() {
        UserClient userClient = rcFactory.forUser(userCtx.credentialsSupplier());
        return userClient.vertex();
    }
    private List<String> formatIds(String graph, String pattern, Collection<String> ids) {
        final String fPattern = ofNullable(pattern).orElse(DEFAULT_VERTEX_LIST_PATTERN);
      
        Collection<RVertex> artifacts = vertex().loadArtifacts(graph, ids);
        
        return artifacts.stream().//
                map(a->format(fPattern, a)).//
                collect(Collectors.toList());
    }

    private String format(String pattern, RVertex a) {
        return pattern.
                replace("{title}", a.getTitle()).
                replace("{id}", a.getId());
    }
    @Override
    public Class<?> processingClass() {
        return AnalyticsCommand.class;
    }

}
