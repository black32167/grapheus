/**
 * 
 */
package grapheus.rest.resource;

import java.util.Collections;
import java.util.stream.Collectors;

import javax.inject.Inject;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.grapheus.client.model.telemetry.RServerTelemetryContainer;
import org.grapheus.client.model.telemetry.RTelemetry;
import org.grapheus.client.model.telemetry.RTelemetryContainer;

import grapheus.periodic.TelemetryCollector;

/**
 * @author black
 *
 */
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Path(TelemetryResource.PATH)
public class TelemetryResource {
    public final static String PATH = "/telemetry";
    
    @Inject
    private TelemetryCollector telemetryCollector;
    
    @GET
    public RTelemetryContainer getTelemetry() {
        return RTelemetryContainer.builder().
                serversTelemetry(
                    Collections.singletonList(RServerTelemetryContainer.builder().//
                        serverId("Default").
                        telemetryHistory(
                            telemetryCollector.getTelemetryHistory().stream().map(this::toExternalTelemetryItem).collect(Collectors.toList())).//
                        build())).
                build();
    }
    
    private RTelemetry toExternalTelemetryItem(TelemetryCollector.TelemetryItem internalTelemetryItem) {
        return RTelemetry.builder().//
                totalMemory(internalTelemetryItem.getTotalMemory()).//
                freeMemory(internalTelemetryItem.getFreeMemory()).//
                maxMemory(internalTelemetryItem.getMaxMemory()).//
                totalDisk(internalTelemetryItem.getTotalDisk()).//
                freeDisk(internalTelemetryItem.getFreeDisk()).//
                usableDisk(internalTelemetryItem.getUsableDisk()).//
                timestamp(internalTelemetryItem.getTimestamp()).//
                availableDbConnections(internalTelemetryItem.getAvailableDbConnections()).//
                build();
    }

}
