package uk.gov.justice.services.fileservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class SmallFileWriteableTest {

    @Rule
    public TemporaryFolder storagePoolLocation = new TemporaryFolder();

    private final DirectoryScanner scanner = new DirectoryScanner();

    private DirectoryPath directoryPath;

    @Before
    public void before() throws IOException {
        storagePoolLocation.newFolder("dir-0");
        storagePoolLocation.newFolder("dir-1");
        directoryPath = scanner.scan(storagePoolLocation.getRoot(), "dir-");
    }

    @Test
    public void shouldWriteASmallFileAccurately() throws IOException {

        final String expected = "testing";

        final UUID uuid = UUID.randomUUID();

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(expected.getBytes());

        final FileOperation fop = new SmallFileWriteable(inputStream, directoryPath, uuid);

        FileOperator.op().execute(fop);

        final UUID fileUUID = fop.getUUID();

        assertEquals(uuid, fileUUID);

        fop.getFailure().ifPresent(e -> e.printStackTrace());

        assertTrue(fop.isSuccess());

        final String fileName = new FileLookup(directoryPath).getFileName(fileUUID);

        final String actual = new String(Files.readAllBytes(Paths.get(fileName)));

        assertEquals(expected, actual);
    }

    @Test
    public void shouldWriteARandomByteArrayToASmallFileAccurately() throws IOException {
        
        final byte expected[] = new byte[102400];
        
        ThreadLocalRandom.current().nextBytes(expected);
        
        final UUID uuid = UUID.randomUUID();

        final ByteArrayInputStream inputStream = new ByteArrayInputStream(expected);
        
        final FileOperation fop = new SmallFileWriteable(inputStream, directoryPath, uuid);

        FileOperator.op().execute(fop);

        final UUID fileUUID = fop.getUUID();

        assertEquals(uuid, fileUUID);

        fop.getFailure().ifPresent(e -> e.printStackTrace());

        assertTrue(fop.isSuccess());

        final String fileName = new FileLookup(directoryPath).getFileName(fileUUID);
        
        final byte actual[] = Files.readAllBytes(Paths.get(fileName));

        assertTrue(Arrays.equals(expected, actual));
    }

}
