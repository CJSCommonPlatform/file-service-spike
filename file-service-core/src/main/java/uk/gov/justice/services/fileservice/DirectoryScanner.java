package uk.gov.justice.services.fileservice;

import static java.lang.String.format;

import java.io.File;
import java.util.Set;
import java.util.TreeSet;

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
        
        checkDirectoriesAreFollowingPattern(dirs, directoryPrefix);

        return new DirectoryPath().setStoragePoolLocation(storagePoolLocation.getAbsolutePath())
                        .setDirectoryNamePrefix(directoryPrefix).setNumberDirectories(dirs.length);
    }

    private void checkDirectoriesAreFollowingPattern(final File[] dirs, final String directoryPrefix) {
        final Set<Integer> directorySuffixes = new TreeSet<>();
        for (File dir : dirs) {

            final String suffix = dir.getName().replaceAll(directoryPrefix, "");

            if (!suffix.matches("[0-9]+")) {
                throwIllegalArgumentException(
                                format("Invalid directory name %s, should end with numeric value ",
                                                dir.getName()));
            }

            directorySuffixes.add(Integer.parseInt(suffix));
        }

        for (int i = 0; i < dirs.length; i++) {
            if (!directorySuffixes.contains(i)) {
                throwIllegalArgumentException(format("The directory %s is not present ",
                                String.join("", directoryPrefix, String.valueOf(i))));
            }
        }
    }

    private void throwIllegalArgumentException(final String message) {
        throw new IllegalArgumentException(message);
    }

}
