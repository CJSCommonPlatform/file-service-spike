package uk.gov.justice.services.fileservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class BigFileWriteableTest {

    @Rule
    public TemporaryFolder storagePoolLocation = new TemporaryFolder();

    private final DirectoryScanner scanner = new DirectoryScanner();

    private DirectoryPath directoryPath;

    @Before
    public void before() throws IOException {
        storagePoolLocation.newFolder("dir-0");
        storagePoolLocation.newFolder("dir-1");
        directoryPath = scanner.scan(storagePoolLocation.getRoot(), "dir-");
        TempDirectoryBuilder tempDirBuilder = new TempDirectoryBuilder();
        tempDirBuilder.createTempDirectories(directoryPath);        
    }

    @Test
    public void shouldWriteARandomStreamToABigFileAccurately() throws IOException {

        final long size = 1000000000;
        
        final UUID uuid = UUID.randomUUID();

        final FileOperation fop = new BigFileWriteable(new GeneratedInputStream(size), 
                        directoryPath, uuid);

        FileOperator.op().execute(fop);

        final UUID fileUUID = fop.getUUID();

        assertEquals(uuid, fileUUID);

        fop.getFailure().ifPresent(e -> e.printStackTrace());

        assertTrue(fop.isSuccess());

        final String fileName = new FileLookup(directoryPath).getTemporaryFileName(fileUUID);
        
        final long actual = new File(fileName).length();

        assertEquals(size, actual);
    }

}