package uk.gov.justice.services.fileservice;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DirectoryPathTest {

    @Test
    public void shouldGetDefaultFilesystemPrefix() {
        final DirectoryPath dp = new DirectoryPath();
        assertEquals(dp.getStoragePoolLocation(), "/gluster-storage/");
    }

    @Test
    public void shouldSetFilesystemPrefix() {
        final DirectoryPath dp = new DirectoryPath();
        dp.setStoragePoolLocation("/test/");
        assertEquals(dp.getStoragePoolLocation(), "/test/");
    }
    
    @Test
    public void shouldGetDefaultDirectoryPrefix() {
        final DirectoryPath dp = new DirectoryPath();
        assertEquals(dp.getDirectoryNamePrefix(), "dir-");
    }

    @Test
    public void shouldSetDirectoryPrefix() {
        final DirectoryPath dp = new DirectoryPath();
        dp.setDirectoryNamePrefix("filedir-");
        assertEquals(dp.getDirectoryNamePrefix(), "filedir-");
    }

    @Test
    public void shouldGetDefaultNumberFiles() {
        final DirectoryPath dp = new DirectoryPath();
        assertEquals(dp.getNumberDirectories(), 10);
    }


}
