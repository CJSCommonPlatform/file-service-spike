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
        final UUID uuid = UUID.randomUUID();
        final String fileName = new FileLookup(new DirectoryPath()).getFileName(uuid);
        assertTrue(fileName, fileName.matches(
                        String.join("","/gluster-storage/dir-[0-9]/", uuid.toString())));
    }
    
    @Test
    public void shouldTestConsistentLookupFileName() {
        final FileLookup fileLookup = new FileLookup(new DirectoryPath());
        final Map<UUID, String> fileNames = new TreeMap<>();
        final List<UUID> randomUUIDs = Stream.generate(UUID::randomUUID).limit(100000)
                        .collect(Collectors.toList());

        randomUUIDs.forEach(uuid -> {
            fileNames.put(uuid, fileLookup.getFileName(uuid));
        });

        for(Map.Entry<UUID, String> uuidEntry: fileNames.entrySet()){
            assertEquals(uuidEntry.getValue(), fileLookup.getFileName(uuidEntry.getKey()));
        }        
    }
}