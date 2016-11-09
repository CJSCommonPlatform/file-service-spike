package uk.gov.justice.services.fileservice;

import java.util.UUID;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 
 * Explicit Permit for restricting access on files <br>
 * <br>
 * 
 * Example usage <br>
 * <code>
 * 
 * try (final FilePermit fp = filePermit.acquire()) {
 *     
 *     //do something
 *     
 * }
 * </code>
 * 
 *
 */
public class FilePermit implements AutoCloseable {


    /**
     * A reentrant mutually exclusive lock
     */
    private Lock lock = new ReentrantLock();

    /**
     * The UUID associated with the file permit
     */
    private final UUID uuid;

    /**
     * 
     */
    private volatile boolean locked;

    /**
     * UUID associated with the file
     * 
     * @param uuid
     */
    public FilePermit(final UUID uuid) {
        this.uuid = uuid;
    }

    /**
     * Acquire a permit
     * 
     * @return FilePermit
     * @throws InterruptedException
     */
    public FilePermit acquire() {
        lock.lock();
        locked = true;
        return this;
    }

    /**
     * Release a permit
     */
    @Override
    public void close() {
        lock.unlock();
        locked = false;
    }

    /**
     * Return the lock
     * 
     * @return lock
     */
    public Lock getLock() {
        return lock;
    }

    /**
     * Set the lock
     * 
     * @param lock to set
     */
    public void setLock(final Lock lock) {
        this.lock = lock;
    }

    /**
     * Check the lock status
     * 
     * @return
     */
    public boolean isLocked() {
        return locked;
    }

    /**
     * 
     * @return the UUID
     */
    public UUID getUuid() {
        return uuid;
    }

}
