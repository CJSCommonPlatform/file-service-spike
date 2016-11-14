package uk.gov.justice.services.fileservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

public class FileLookupTest {

    @Test
    public void shouldLookupFileName() {
        final DirectoryPath dp = new DirectoryPath();
        final FileLookup fl = new FileLookup(dp);
        final UUID uuid = UUID.randomUUID();
        final String fileName = fl.getFileName(uuid);
        assertTrue(fileName, fileName.matches(
                        String.join("","/gluster-storage/dir-[0-9]/", uuid.toString())));
    }
    
    @Test
    public void shouldTestConsistentLookupFileName() {
        final DirectoryPath dp = new DirectoryPath();
        final FileLookup fl = new FileLookup(dp);
        final Map<UUID, String> fileNames = new TreeMap<>();
        final List<UUID> l = Stream.generate(UUID::randomUUID).limit(100000)
                        .collect(Collectors.toList());

        l.forEach(x -> {
            fileNames.put(x, fl.getFileName(x));
        });

        for(Map.Entry<UUID, String> me: fileNames.entrySet()){
            assertEquals(me.getValue(), fl.getFileName(me.getKey()));
        }        
    }
}
