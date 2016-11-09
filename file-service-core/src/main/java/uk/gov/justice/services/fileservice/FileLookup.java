package uk.gov.justice.services.fileservice;

import static java.lang.String.join;
import static java.lang.String.valueOf;
import static uk.gov.justice.services.fileservice.ConsistentHash.getBucket;

import java.util.UUID;

/**
 * 
 * Lookup a file name
 *
 */
public class FileLookup {

    /**
     * The directory path
     * 
     * @see DirectoryPath
     */
    private final DirectoryPath directoryPath;

    /**
     * Initialise the file lookup with the directory path
     * 
     * @param directoryPath
     */
    public FileLookup(final DirectoryPath directoryPath) {
        this.directoryPath = directoryPath;
    }

    /**
     * Get the file name used to persist a file.
     * 
     * @param uuid to use
     * @param directoryPath to use
     * @return the file name
     */
    public String getFileName(final UUID uuid) {
        int bucket = getBucket(uuid, directoryPath.getNumberDirectories());
        String dp = directoryPath.getDirectoryPath();
        
        return join("",dp,valueOf(bucket));
    }
}
