/**
 * 
 */
package grapheus.security.credentials.uds;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

/**
 * @author black
 *
 */
@Service
@Slf4j
public class PasswordCipher {

    public byte[] encodePassword(byte[] key, byte[] password) {
       
        
        if(key == null) {
            return password;
        }
        Cipher cipher = getCipher(Cipher.ENCRYPT_MODE, key);
        
        try {
            byte[] encoded = cipher.doFinal(password);
            log.trace("Encoding password={} key={} encoded={}", Arrays.toString(password), Arrays.toString(key), Arrays.toString(encoded));
            return encoded;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new RuntimeException("Cannot cypher password", e);
        }
    }
    
    public byte[] decodePassword(byte[] key, byte[] encoded) {
        if(key == null) {
            return encoded;
        }
        Cipher cipher = getCipher(Cipher.DECRYPT_MODE, key);
        
        try {
            byte[] decoded = cipher.doFinal(encoded);
            log.trace("Decoding encoded={} key={} decoded={}", Arrays.toString(encoded), Arrays.toString(key), Arrays.toString(decoded));
            return decoded;
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            log.trace("Failing encoding encoded={} key={} decoded={}", Arrays.toString(encoded), Arrays.toString(key));
            throw new RuntimeException("Cannot cypher password", e);
        }
    }
    
    private Cipher getCipher(int mode, byte originalKey[]) {
        String algorithm = "DES";
        
        byte[] key = digest8(originalKey);
        
        SecretKey skey = new SecretKeySpec(key, algorithm);
        try {
            Cipher c = Cipher.getInstance(algorithm);
            c.init(mode, skey);
            return c;
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new RuntimeException("Cannot find digest for algorithm " + algorithm +":" + e.getMessage(), e);
        }
    }
    
    private byte[] digest8(byte[] input) {
        long h = 1125899906842597L;
        int len = input.length;

        for (int i = 0; i < len; i++) {
          h = 31*h + input[i];
        }
        
        byte[] res = new byte[Long.BYTES];
        for(int i = 0; i < res.length; i++) {
            res[i] = (byte) h;
            h >>= 8;
        }
        return res;
    }
}
