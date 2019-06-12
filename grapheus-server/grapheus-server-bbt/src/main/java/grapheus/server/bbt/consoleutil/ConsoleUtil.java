/**
 * 
 */
package grapheus.server.bbt.consoleutil;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * @author black
 *
 */

public final class ConsoleUtil {

    public static String read(String prompt) {
        return read(prompt, String.class);
    }
    public static <T> T read(String prompt, Class<T> clazz) {
        String sProp = System.getProperty(prompt);
        if(sProp == null) {
            System.out.println(String.format("Enter value for key %s", prompt));
            try(BufferedReader br = new BufferedReader(new InputStreamReader(System.in))) {
                sProp =  br.readLine();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        try {
            return clazz.getConstructor(String.class).newInstance(sProp);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private ConsoleUtil(){}
}
