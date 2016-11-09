package uk.gov.justice.services.fileservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

/**
 * 
 * This class should be used only for writing small files (less that 1MB)
 * 
 * The Content-Length header can be used to decide, if it is small file or not.
 */
public class SmallFileWriteable implements FileOperation {

    /**
     * The input stream to read data from
     */
    private final InputStream inputStream;

    /**
     * Store the status of the OP
     */
    private volatile boolean success;

    /**
     * Reference to an exception if it occurs
     */
    private Optional<Exception> exception = Optional.empty();

    /**
     * Observer is notified of the start and stop of execution
     */
    private FileOperationObserver observer;

    /**
     * UUID for the file
     */
    final UUID uuid;

    /**
     * The directory path
     * @see DirectoryPath
     */
    final DirectoryPath directoryPath;

    /**
     * 
     * @param inputStream to read data from
     * @param directoryPath as a prefix to a file location
     * @param uuid of the file
     */
    public SmallFileWriteable(final InputStream inputStream, final DirectoryPath directoryPath,
                    final UUID uuid) {
        this.inputStream = inputStream;
        this.uuid = uuid;
        this.directoryPath = directoryPath;
    }

    @Override
    public UUID getUUID() {
        return uuid;
    }

    @Override
    public void execute() {
        Optional.ofNullable(observer).ifPresent(s -> s.beginExecution());
        try {
            final String fileName = new FileLookup(directoryPath).getFileName(getUUID());
            final ByteBuffer buf = ByteBuffer.wrap(IOUtils.toByteArray(inputStream));
            try (final FileOutputStream fos = new FileOutputStream(new File(fileName), false);
                            final FileChannel channel = fos.getChannel()) {
                channel.write(buf);
            }
            success = true;
        } catch (Exception e) {
            exception = Optional.of(e);
        } finally {
            Optional.ofNullable(observer).ifPresent(s -> s.endExecution());
        }
    }

    @Override
    public boolean isSuccess() {
        return success;
    }

    @Override
    public Optional<Exception> getFailure() {
        return exception;
    }

    @Override
    public void setFileOperationObserver(final FileOperationObserver observer) {
        this.observer = observer;
    }
}
