package uk.gov.justice.services.fileservice;

import java.util.UUID;

import org.junit.Assert;
import org.junit.Test;

public class FileLookupTest {


    @Test
    public void testGetFileName() {
        final DirectoryPath dp = new DirectoryPath();
        final FileLookup fl = new FileLookup(dp);
        final UUID uuid = UUID.randomUUID();
        final String fileName = fl.getFileName(uuid);
        Assert.assertTrue(fileName, fl.getFileName(uuid).matches("/gluster-storage/dir-[0-9]"));
    }

}
