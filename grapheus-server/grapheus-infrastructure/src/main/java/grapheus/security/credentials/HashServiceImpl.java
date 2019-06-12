/**
 * 
 */
package grapheus.security.credentials;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Slf4j
@Service
public class HashServiceImpl implements HashService {
    @Override
    public byte[] hash(byte[] source) {
        if(source == null) {
            return null;
        }
        try {
            return MessageDigest.getInstance("SHA-1").digest(source);
        } catch(NoSuchAlgorithmException e) {
            log.error("", e);
            throw new RuntimeException();
        } 
    }
}
