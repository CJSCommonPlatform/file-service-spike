package uk.gov.justice.services.fileservice;

import java.io.File;

import com.google.common.base.Strings;

/**
 * 
 * Scan the file system at bootstrap and build the directory path
 *
 * @see DirectoryPath
 */
public class DirectoryScanner {


    /**
     * Scan the file system looking for directories
     * 
     * @param storagePoolLocation
     * @param directoryPrefix
     * @return
     */
    public DirectoryPath scan(final File storagePoolLocation, final String directoryPrefix) {

        if (storagePoolLocation == null || !storagePoolLocation.exists()) {
            throwIllegalArgumentException("Invalid Storage Pool Location, does not exist");
        }

        if (Strings.isNullOrEmpty(directoryPrefix)) {
            throwIllegalArgumentException("Directory Prefix is empty");
        }

        final File[] dirs = storagePoolLocation.listFiles((dir, name) -> {
            return dir.isDirectory() && name.startsWith(directoryPrefix);
        });

        if (dirs == null || dirs.length == 0) {
            throwIllegalArgumentException(
                            "Invalid Storage Pool Location, directories do not exist");
        }

        return new DirectoryPath().setStoragePoolLocation(storagePoolLocation.getAbsolutePath())
                        .setDirectoryNamePrefix(directoryPrefix).setNumberDirectories(dirs.length);
    }

    private void throwIllegalArgumentException(final String message) {
        throw new IllegalArgumentException(message);
    }

}
