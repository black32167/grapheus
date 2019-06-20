/**
 * 
 */
package org.grapheus.client.model;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * @author black
 *
 */
public class GsonFactory {

    public static Gson createGson() {
        return new GsonBuilder().setPrettyPrinting().create();
    }

}
