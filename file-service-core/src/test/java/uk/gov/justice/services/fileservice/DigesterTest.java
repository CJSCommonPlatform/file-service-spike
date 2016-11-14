package uk.gov.justice.services.fileservice;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

public class DigesterTest {

    @Rule
    public TemporaryFolder storagePoolLocation = new TemporaryFolder();

    private final DirectoryScanner scanner = new DirectoryScanner();

    @Before
    public void before() throws IOException {
        storagePoolLocation.newFolder("dir-0");
        scanner.scan(storagePoolLocation.getRoot(), "dir-");
    }

    @Test
    public void shouldCompareMessageDigest() throws Exception {

        final UUID uuid = UUID.randomUUID();

        final File rootDir = new File(storagePoolLocation.getRoot(), "dir-0");

        Files.write("test".getBytes(), new File(rootDir, uuid.toString()));

        final MessageDigest expectedMessageDigest = MessageDigest.getInstance("SHA-256");
        expectedMessageDigest.update("test".getBytes());

        final MessageDigest mdActual = new Digester().getMessageDigest(
                        new File(rootDir, uuid.toString()),
                        "SHA-256");
        
        assertTrue(Arrays.equals(expectedMessageDigest.digest(), mdActual.digest()));
    }

}
