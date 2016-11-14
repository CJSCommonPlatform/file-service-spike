package uk.gov.justice.services.fileservice;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Optional;
import java.util.UUID;

import org.apache.commons.io.IOUtils;

/**
 * 
 * This class should be used only for writing big files (tested up to 1GB)
 * 
 */
public class BigFileWriteable implements FileOperation {

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
     * Length of the file
     */
    long fileLength;
    
    /**
     * 
     * @param inputStream to read data from
     * @param directoryPath as a prefix to a file location
     * @param uuid of the file
     */
    public BigFileWriteable(final InputStream inputStream, final DirectoryPath directoryPath,
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
            try (final FileOutputStream fos = new FileOutputStream(new File(fileName), false)) {
                IOUtils.copyLarge(inputStream, fos);
            }
            
            fileLength = new File(fileName).length();
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

    @Override
    public long getFileLength() {
        return fileLength;
    }
}