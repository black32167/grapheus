/**
 * 
 */
package org.grapheus.common.config;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * @author black
 *
 */
public final class GrapheusStaticConfiguration {

	public static String getString(String key, Supplier<String> defaultValueSupplier) {
		return get(key, defaultValueSupplier, Function.identity()); 
	}

	public static int getInteger(String key, Supplier<Integer> defaultValueSupplier) {
		return get(key, defaultValueSupplier, Integer::valueOf); 
	}
	
	private static <T> T get(String key, Supplier<T> defaultValueSupplier, Function<String, T> typeConverter) {
		return Optional.ofNullable(
					System.getProperty(key,      // 1 - read from system property
	                System.getenv(envKey(key)))  // 2 - read from environment variable
				)
				.map(typeConverter::apply)
                .orElseGet(defaultValueSupplier);  // 3 - fallback to default value 
	}
	
	private static String envKey(String key) {
		return key.replace(".", "_");
	}

	private GrapheusStaticConfiguration() {};
}
