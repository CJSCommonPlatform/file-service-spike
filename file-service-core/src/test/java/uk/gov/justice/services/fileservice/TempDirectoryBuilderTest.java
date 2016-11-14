package uk.gov.justice.services.fileservice;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class TempDirectoryBuilderTest {

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
    public void shouldCreateTempDirectoriesAsTheyAreAbsent() {
        
        final TempDirectoryBuilder tempDirBuilder = new TempDirectoryBuilder();
        
        tempDirBuilder.createTempDirectories(directoryPath);
        
        final File dirs[] = storagePoolLocation.getRoot().listFiles();
        
        for(final File file: dirs){
            File tempDir = new File(file, "temp");
            
            assertTrue(tempDir.exists());
            
            assertTrue(tempDir.isDirectory());
            
        }
    }
    
    @Test
    public void shouldNotCreateTempDirectoriesIfTheyArePresentAndNotThrowException() {
        
        final File dirs[] = storagePoolLocation.getRoot().listFiles();
        
        for(final File file: dirs){
            File tempDir = new File(file, "temp");
            tempDir.mkdir();
        }
        
        final TempDirectoryBuilder tempDirBuilder = new TempDirectoryBuilder();
        
        tempDirBuilder.createTempDirectories(directoryPath);
        
        for(final File file: dirs){
            File tempDir = new File(file, "temp");
            
            assertTrue(tempDir.exists());
            
            assertTrue(tempDir.isDirectory());
            
        }
    }

}
