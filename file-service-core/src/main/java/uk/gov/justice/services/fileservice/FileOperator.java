package uk.gov.justice.services.fileservice;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 
 * In memory lock for a file.
 * 
 * Prior to writing to a file, an in memory lock is obtained and File operation is executed.
 * 
 * This prevents concurrent access and associated data corruption issues.
 * 
 * @see FileOperation
 *
 */
public class FileOperator {


    private static final int MAX_ENTRIES = 10000;

    /**
     * Private constructor to prevent instantiation
     */
    private FileOperator() {}

    /**
     * 
     * Singleton Holder pattern
     *
     */
    private static class Holder {
        private static final FileOperator OP = new FileOperator();
    }
    /**
     * 
     * Return an instance of the FileOperator
     *
     */
    public static FileOperator op(){
        return Holder.OP;
    }
    /**
     * All the files currently being operated on
     */
    private final Map<String, FilePermit> filesInUse =
                    new LinkedHashMap<String, FilePermit>(MAX_ENTRIES + 1, .75F, false) {

                        private static final long serialVersionUID = 1L;

                        @Override
                        protected boolean removeEldestEntry(Map.Entry<String, FilePermit> eldest) {
                            return size() > MAX_ENTRIES;
                        }
                    };
    /**
     * ReadWriteLock
     */
    private final ReadWriteLock readWriteLock = new ReentrantReadWriteLock(true);
    /**
     * Write Lock on filesInUse
     */
    private final Lock writeLock = readWriteLock.writeLock();
    /**
     * Read Lock on filesInUse
     */
    private final Lock readLock = readWriteLock.readLock();


    /**
     * Execute a file operation after acquiring a lock
     * 
     * @throws Exception
     * 
     */
    public final void execute(final FileOperation fileOperation) {

        final String key = fileOperation.getUUID().toString();

        writeLock.lock();
        try {
            filesInUse.putIfAbsent(key, new FilePermit(fileOperation.getUUID()));
        } finally {
            writeLock.unlock();
        }

        readLock.lock();
        try (final FilePermit fp = filesInUse.get(key).acquire()) {
            fileOperation.execute();
        } finally {
            readLock.unlock();
        }
    }
}
