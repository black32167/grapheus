/**
 * 
 */
package org.grapheus.client.model.telemetry;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author black
 *
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RServerTelemetryContainer {
    private String serverId; 
    private List<RTelemetry> telemetryHistory;
}
