package uk.gov.justice.services.fileservice;

import static java.lang.String.join;

/**
 * 
 * Get the path to the directory
 *
 */
public class DirectoryPath {

    /**
     * The Storage Pool Location
     */
    private String storagePoolLocation = "/gluster-storage/";

    /**
     * The directory name prefix
     */
    private String directoryNamePrefix = "dir-";

    /**
     * The number of directories
     */
    private int numberDirectories = 10;

    /**
     * The location of the glusterfs storage pool
     * 
     * @return location of the glusterfs storage pool
     */
    public String getStoragePoolLocation() {
        return storagePoolLocation;
    }

    /**
     * Set the location of the glusterfs storage pool
     * 
     * @param filesystemPrefix location of the glusterfs storage pool
     */
    public void setStoragePoolLocation(final String storagePoolLocation) {
        this.storagePoolLocation = storagePoolLocation;
    }

    /**
     * The name prefix for a directory
     * 
     * @return the directory name prefix
     */
    public String getDirectoryNamePrefix() {
        return directoryNamePrefix;
    }

    /**
     * Set the name prefix for a directory
     * 
     * @param directoryNamePrefix for a directory
     */
    public void setDirectoryNamePrefix(final String directoryNamePrefix) {
        this.directoryNamePrefix = directoryNamePrefix;
    }

    /**
     * The number of directories to allocate
     * 
     * @return the number of directories
     */
    public int getNumberDirectories() {
        return numberDirectories;
    }

    /**
     * Set the number of directories
     * 
     * @param numberDirectories
     */
    public void setNumberDirectories(final int numberDirectories) {
        this.numberDirectories = numberDirectories;
    }

    /**
     * The prefix to a directory
     * 
     * @return the partial prefix to a directory
     */
    public String getDirectoryPath() {
        return join("", getStoragePoolLocation(), getDirectoryNamePrefix());
    }

}
