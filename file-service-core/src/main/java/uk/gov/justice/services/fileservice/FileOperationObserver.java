package uk.gov.justice.services.fileservice;

/**
 * 
 * Observe operations on the file.
 * 
 * Possible uses include recording execution times
 *
 */
public interface FileOperationObserver {

    /**
     * Observe the start of an execution
     */
    void beginExecution();

    /**
     * Observe the end of an execution regardless of success/failure
     */
    void endExecution();
}
