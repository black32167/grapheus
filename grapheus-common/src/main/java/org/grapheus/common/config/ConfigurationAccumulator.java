/**
 * 
 */
package org.grapheus.common.config;

/**
 * NOTE: this implementation is not thread-safe.
 * 
 * @author black
 */
public class ConfigurationAccumulator implements HumanReadableConfigConsumer {
	private final StringBuilder buffer = new StringBuilder();
	{
		buffer.append(String.format("\t%-30s %-30s %s\n", "Title", "Key", "Value"));
		buffer.append(String.format("\t%-30s %-30s %s\n", "-----", "------", "-------"));
	}

	@Override
	public HumanReadableConfigConsumer addConfig(String humanReadableTitle, String key, Object value) {
		String bareKey = key.replace("${", "").replace("}", "");
		buffer.append(String.format("\t%-30s %-30s %s\n", humanReadableTitle, bareKey, value));
		return this;
	}

	@Override
	public String toString() {
		return buffer.toString();
	}
	
	

}
