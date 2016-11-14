package uk.gov.justice.services.fileservice;

import static java.lang.String.join;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class DirectoryScannerTest {

    @Rule
    public TemporaryFolder storagePoolLocation = new TemporaryFolder();

    private final DirectoryScanner scanner = new DirectoryScanner();

    @Test(expected=IllegalArgumentException.class)
    public void shouldFailAsDirZeroIsAbsent() throws IOException {

        storagePoolLocation.newFolder("dir-1");
        storagePoolLocation.newFolder("dir-2");

        final DirectoryPath directoryPath = scanner.scan(storagePoolLocation.getRoot(), "dir-");

        assertEquals(join("", storagePoolLocation.getRoot().getAbsolutePath(), "/", "dir-"),
                        directoryPath.getDirectoryPath());
        
        assertEquals("dir-", directoryPath.getDirectoryNamePrefix());
        
        assertEquals(2, directoryPath.getNumberDirectories());
    }

    @Test(expected=IllegalArgumentException.class)
    public void shouldFailAsDirOneIsAbsent() throws IOException {

        storagePoolLocation.newFolder("dir-0");
        storagePoolLocation.newFolder("dir-2");

        final DirectoryPath directoryPath = scanner.scan(storagePoolLocation.getRoot(), "dir-");

        assertEquals(join("", storagePoolLocation.getRoot().getAbsolutePath(), "/", "dir-"),
                        directoryPath.getDirectoryPath());
        
        assertEquals("dir-", directoryPath.getDirectoryNamePrefix());
        
        assertEquals(2, directoryPath.getNumberDirectories());
    }

    
    @Test
    public void shouldScanTheStoragePoolAndReturnDirectoryPath() throws IOException {

        storagePoolLocation.newFolder("dir-0");
        storagePoolLocation.newFolder("dir-1");

        final DirectoryPath directoryPath = scanner.scan(storagePoolLocation.getRoot(), "dir-");

        assertEquals(join("", storagePoolLocation.getRoot().getAbsolutePath(), "/", "dir-"),
                        directoryPath.getDirectoryPath());
        
        assertEquals("dir-", directoryPath.getDirectoryNamePrefix());
        
        assertEquals(2, directoryPath.getNumberDirectories());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldScanTheStoragePoolAndThrowIllegalArgumentExceptionWhenDirectoriesAreAbsent()
                    throws IOException {

        final DirectoryPath directoryPath = scanner.scan(storagePoolLocation.getRoot(), "dir-");

        assertEquals(join("", storagePoolLocation.getRoot().getAbsolutePath(), "/", "dir-"),
                        directoryPath.getDirectoryPath());
        
        assertEquals("dir-", directoryPath.getDirectoryNamePrefix());
        
        assertEquals(2, directoryPath.getNumberDirectories());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldScanTheStoragePoolAndThrowIllegalArgumentExceptionWhenInvalidStoragePoolLocation()
                    throws IOException {
        scanner.scan(new File("@"), "dir-");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldScanTheStoragePoolAndThrowIllegalArgumentExceptionWhenNullStoragePoolLocation()
                    throws IOException {
        scanner.scan(null, "dir-");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldScanTheStoragePoolAndThrowIllegalArgumentExceptionWhenDirectoryPrefixIsEmpty()
                    throws IOException {
        scanner.scan(null, "");
    }
}
