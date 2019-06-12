/**
 * 
 */
package org.grapheus.cli;

import java.io.IOException;
import java.io.InputStream;

import org.grapheus.cli.model.CLIAccount;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

/**
 * @author black
 *
 */
@Service
public class AccountSpecLoader {

    public CLIAccount loadAccount(InputStream is) throws IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
   
        return mapper.readValue(is, CLIAccount.class);
    }
}
