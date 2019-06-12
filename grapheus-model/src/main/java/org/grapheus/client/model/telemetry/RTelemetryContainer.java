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
 * Contains collection of 
 * 
 * @author black
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RTelemetryContainer {
    private List<RServerTelemetryContainer> serversTelemetry;
}
