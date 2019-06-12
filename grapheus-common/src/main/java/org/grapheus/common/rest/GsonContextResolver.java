/**
 * 
 */
package org.grapheus.common.rest;

import javax.ws.rs.ext.ContextResolver;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author black
 *
 */
public class GsonContextResolver implements ContextResolver<Gson> {

    @Override
    public Gson getContext(Class<?> type) {
        return new GsonBuilder().disableHtmlEscaping().registerTypeHierarchyAdapter(byte[].class,
                new ByteArrayToBase64TypeAdapter()).create();
    }

}
