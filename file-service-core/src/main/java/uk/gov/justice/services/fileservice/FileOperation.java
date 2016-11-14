package uk.gov.justice.services.fileservice;

import java.util.Optional;
import java.util.UUID;

/**
 * 
 * An Interface for operating on a file
 *
 */
public interface FileOperation {

    /**
     * The UUID associated with the file
     * 
     * @return the UUID associated with the file
     */
    UUID getUUID();

    /**
     * Operate on a file
     * 
     * @param observer
     * @see FileOperator for usage
     * @see FileOperationObserver observer
     */
    void execute();


    /**
     * Return true if the operation was executed successfully
     * 
     * @return success
     */
    boolean isSuccess();

    /**
     * If the operation was a failure return the details of the exception.
     * 
     * @return
     */
    Optional<Exception> getFailure();

    /**
     * Set an observer to receive notifications
     * 
     * @see FileOperationObserver
     * @param observer
     */
    void setFileOperationObserver(FileOperationObserver observer);
}
