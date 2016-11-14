package uk.gov.justice.services.fileservice;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import com.google.common.io.Files;

public class AtomicMoverTest {

    @Rule
    public TemporaryFolder storagePoolLocation = new TemporaryFolder();

    private final DirectoryScanner scanner = new DirectoryScanner();

    private DirectoryPath directoryPath;

    @Before
    public void before() throws IOException {
        storagePoolLocation.newFolder("dir-0");
        directoryPath = scanner.scan(storagePoolLocation.getRoot(), "dir-");
        TempDirectoryBuilder tempDirBuilder = new TempDirectoryBuilder();
        tempDirBuilder.createTempDirectories(directoryPath);        
    }

    @Test
    public void shouldMoveAFileAtomically() throws IOException {
        
        final UUID uuid = UUID.randomUUID();
        
        final File rootDir = new File(storagePoolLocation.getRoot(), "dir-0");
        
        final File dir = new File(rootDir, "temp");
                        
        Files.write("test".getBytes(), new File(dir, uuid.toString()));
                
        final AtomicMover atomicMover = new AtomicMover();
        
        atomicMover.atomicMove(directoryPath, uuid);
        
        assertTrue(new File(rootDir, uuid.toString()).exists());
    }

}
