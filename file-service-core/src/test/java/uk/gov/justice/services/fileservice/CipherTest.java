package uk.gov.justice.services.fileservice;

import static org.junit.Assert.assertTrue;

import java.security.SecureRandom;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.junit.Test;

public class CipherTest {

    //http://www.cse.wustl.edu/~jain/cse567-06/ftp/encryption_perf/
    @Test
    public void shouldTestEncryptionAndDecryption() throws Exception {
        
        final ThreadLocalRandom random = ThreadLocalRandom.current();
        final byte[] actual = new byte[random.nextInt(1024, 240020)];
        random.nextBytes(actual);
        final KeyGenerator keyGenerator = KeyGenerator.getInstance("AES");
        //policy file changes required to boost key length
        keyGenerator.init(128);
        final byte[] key = keyGenerator.generateKey().getEncoded();
        final SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
        final SecureRandom secureRandom = new SecureRandom();
        final byte iv[] = new byte[16];
        secureRandom.nextBytes(iv);
        final IvParameterSpec ivs = new IvParameterSpec(iv);
        //play safe with CBC and random IV
        final Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, ivs);
        final byte[] encrypted = cipher.doFinal(actual);
        cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, ivs);
        final byte[] decrypted = cipher.doFinal(encrypted);
        
        assertTrue(Arrays.equals(actual, decrypted));
    }
}
