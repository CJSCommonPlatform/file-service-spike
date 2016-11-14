package uk.gov.justice.services.fileservice;

import static java.lang.String.join;

import java.io.Serializable;

/**
 * 
 * Get the path to the directory
 *
 */
public class DirectoryPath implements Serializable {

    /**
     * Serial Version ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * The Storage Pool Location
     */
    private String storagePoolLocation = "/gluster-storage";

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
    public DirectoryPath setStoragePoolLocation(final String storagePoolLocation) {
        this.storagePoolLocation = storagePoolLocation;
        return this;
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
    public DirectoryPath setDirectoryNamePrefix(final String directoryNamePrefix) {
        this.directoryNamePrefix = directoryNamePrefix;
        return this;
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
    public DirectoryPath setNumberDirectories(final int numberDirectories) {
        this.numberDirectories = numberDirectories;
        return this;
    }

    /**
     * The prefix to a directory
     * 
     * @return the partial prefix to a directory
     */
    public String getDirectoryPath() {
        return join("", getStoragePoolLocation(), "/", getDirectoryNamePrefix());
    }
    

}
