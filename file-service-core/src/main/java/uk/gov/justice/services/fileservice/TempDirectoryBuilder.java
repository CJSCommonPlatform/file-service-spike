package uk.gov.justice.services.fileservice;

import static java.lang.String.format;

import java.io.File;

/**
 * 
 * Create temporary directories for writing files before the atomic move
 *
 */
public class TempDirectoryBuilder {

    /**
     * Create temp directories if they are absent
     * 
     * @param directoryPath
     */
    public void createTempDirectories(final DirectoryPath directoryPath) {

        final String storagePoolLocation = directoryPath.getStoragePoolLocation();

        final File[] dirs = new File(storagePoolLocation).listFiles();

        final String directoryPrefix = directoryPath.getDirectoryNamePrefix();

        for (final File dir : dirs) {

            if (dir.isDirectory() && dir.getName().startsWith(directoryPrefix)) {

                if (!dir.canWrite()) {
                    throwIllegalArgumentException(
                                    format("Cannot write to directory %s ", dir.getAbsolutePath()));
                }

                final File tempDir = new File(dir, "temp");

                if (!tempDir.exists()) {

                    final boolean created = tempDir.mkdir();
                    if (!created) {
                        throwIllegalArgumentException(format("Cannot create directory %s ",
                                        tempDir.getAbsolutePath()));
                    }
                }
            }
        }

    }


    private void throwIllegalArgumentException(final String message) {
        throw new IllegalArgumentException(message);
    }

}
