/**
 * 
 */
package org.grapheus.client.model.telemetry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Container for the telemetry data from the server
 * 
 * @author black
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RTelemetry {
    private long totalMemory;
    private long freeMemory;
    private long maxMemory;
    private long totalDisk;
    private long freeDisk;
    private long usableDisk;
    private long timestamp;
    private int availableDbConnections;

}
