package uk.gov.justice.services.fileservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * 
 * Atomically move the file from the temporary location to the actual one.
 *
 */
public class AtomicMover {

    /**
     * Atomically move the file from temp location
     * 
     * @param directoryPath of the storage pool
     * @param uuid of the file
     * @throws IOException
     */
    public void atomicMove(final DirectoryPath directoryPath, final UUID uuid) throws IOException {

        final FileLookup fl = new FileLookup(directoryPath);

        final Path source = new File(fl.getTemporaryFileName(uuid)).toPath();

        final Path target = new File(fl.getFileName(uuid)).toPath();

        Files.move(source, target, StandardCopyOption.ATOMIC_MOVE);
    }
}
