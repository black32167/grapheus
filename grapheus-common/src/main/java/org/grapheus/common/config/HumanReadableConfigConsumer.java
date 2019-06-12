/**
 * 
 */
package org.grapheus.common.config;

/**
 * @author black
 *
 */
@FunctionalInterface
public interface HumanReadableConfigConsumer {
	/**
	 * {@link HumanReadableConfigProvider} can use this method to supply information about configuration it manages.
	 */
	HumanReadableConfigConsumer addConfig(String humanReadableTitle, String key, Object value);
}
