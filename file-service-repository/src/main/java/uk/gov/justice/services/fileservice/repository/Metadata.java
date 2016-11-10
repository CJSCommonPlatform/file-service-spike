package uk.gov.justice.services.fileservice.repository;

import uk.gov.justice.services.common.converter.ZonedDateTimes;

import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.UUID;

public class Metadata {

    private final UUID fileId;

    private final UUID owningContentUser;

    private final ZonedDateTime createdOn;

    private final String fileName;

    private final String contentType;

    private final String contentEncoding;

    private final int contentLength;

    private final String contentMd5;

    private ZonedDateTime toBeDeletedOn;

    private ZonedDateTime deletedOn;

    private UUID committedEventId;

    private ZonedDateTime committedOn;

    private String fileMetadata;

    public Metadata(final UUID fileId, final UUID owningContentUser,
                    final ZonedDateTime createdOn, final String fileName, final String contentType,
                    final String contentEncoding, final int contentLength, final String contentMd5) {
        this.fileId = fileId;
        this.owningContentUser = owningContentUser;
        this.createdOn = createdOn;
        this.fileName = fileName;
        this.contentType = contentType;
        this.contentEncoding = contentEncoding;
        this.contentLength = contentLength;
        this.contentMd5 = contentMd5;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final Metadata metadata = (Metadata) o;
        return Objects.equals(fileId, metadata.fileId) &&
                Objects.equals(owningContentUser, metadata.owningContentUser) &&
                Objects.equals(createdOn, metadata.createdOn) &&
                Objects.equals(fileName, metadata.fileName) &&
                Objects.equals(contentType, metadata.contentType) &&
                Objects.equals(contentEncoding, metadata.contentEncoding) &&
                Objects.equals(contentLength, metadata.contentLength) &&
                Objects.equals(contentMd5, metadata.contentMd5);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fileId, owningContentUser, createdOn, fileName,
                contentType, contentEncoding, contentLength, contentMd5);
    }

    @Override
    public String toString() {
        return String.format("Metadata [fileId=%s, owningContentUser=%s," +
                        "createdOn=%s, fileName=%s, " +
                        "contentType=%s, contentEncoding=%s, contentLength=%s, " +
                        "contentMd5=%s,toBeDeletedOn=%s,deletedOn=%s,committedEventId=%s,committedOn=%s,fileMetadata=%s]",
                fileId, owningContentUser, ZonedDateTimes.toString(createdOn), fileName,
                contentType, contentEncoding, contentLength, contentMd5, toBeDeletedOn, deletedOn, committedEventId, committedOn, fileMetadata);
    }

    public UUID getFileId() {
        return fileId;
    }

    public UUID getOwningContentUser() {
        return owningContentUser;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public String getFileName() {
        return fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public String getContentEncoding() {
        return contentEncoding;
    }

    public int getContentLength() {
        return contentLength;
    }

    public String getContentMd5() {
        return contentMd5;
    }

    public ZonedDateTime getToBeDeletedOn() {
        return toBeDeletedOn;
    }

    public ZonedDateTime getDeletedOn() {
        return deletedOn;
    }

    public UUID getCommittedEventId() {
        return committedEventId;
    }

    public ZonedDateTime getCommittedOn() {
        return committedOn;
    }

    public String getFileMetadata() {
        return fileMetadata;
    }

    public void setToBeDeletedOn(ZonedDateTime toBeDeletedOn) {
        this.toBeDeletedOn = toBeDeletedOn;
    }

    public void setDeletedOn(ZonedDateTime deletedOn) {
        this.deletedOn = deletedOn;
    }

    public void setCommittedEventId(UUID committedEventId) {
        this.committedEventId = committedEventId;
    }

    public void setCommittedOn(ZonedDateTime committedOn) {
        this.committedOn = committedOn;
    }

    public void setFileMetadata(String fileMetadata) {
        this.fileMetadata = fileMetadata;
    }
}
