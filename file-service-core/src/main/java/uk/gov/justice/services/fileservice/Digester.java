package uk.gov.justice.services.fileservice;

import java.io.File;
import java.io.FileInputStream;
import java.security.DigestInputStream;
import java.security.MessageDigest;

public class Digester {

    /**
     * 
     * @param file
     * @param algo
     * @return
     * @throws Exception
     */
    public MessageDigest getMessageDigest(final File file, String algo)
                    throws Exception {

        final MessageDigest sha256Digest = MessageDigest.getInstance(algo);
        try (final DigestInputStream din = new DigestInputStream(new FileInputStream(file), 
                        sha256Digest);) {
            din.on(true);
            while (din.read() > 0);
            return din.getMessageDigest();
        }
    }
}
