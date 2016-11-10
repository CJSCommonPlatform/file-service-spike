package uk.gov.justice.services.event.buffer.core.repository.streamstatus;


import static java.util.UUID.randomUUID;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

import uk.gov.justice.services.common.util.UtcClock;
import uk.gov.justice.services.fileservice.repository.Metadata;
import uk.gov.justice.services.fileservice.repository.MetadataJdbcRepository;
import uk.gov.justice.services.test.utils.persistence.AbstractJdbcRepositoryIT;

import java.time.ZonedDateTime;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;

public class MetadataJdbcRepositoryIT extends AbstractJdbcRepositoryIT<MetadataJdbcRepository> {
    private static final String LIQUIBASE_FILE_STORE_DB_CHANGELOG_XML = "liquibase/file-store-db.changelog.xml";

    private static final UUID FILE_ID = randomUUID();

    public MetadataJdbcRepositoryIT() {
        super(LIQUIBASE_FILE_STORE_DB_CHANGELOG_XML);
    }

    @Before
    public void initializeDependencies() throws Exception {
        jdbcRepository = new MetadataJdbcRepository();
        registerDataSource();
    }

    @Test
    public void shouldStoreAndRetrieveMetaData() {
        final UUID owningContextUser = randomUUID();
        final ZonedDateTime now = new UtcClock().now();
        final Metadata metadata = metadataOf(FILE_ID, owningContextUser, now, "file-name", "content-type", "content-encoding", 10000, "content-md5", "fileMetaData");
        jdbcRepository.insert(metadata);

        final Metadata metadataFound = jdbcRepository.findByFileId(FILE_ID);

        assertThat(metadataFound, notNullValue());
        assertThat(metadataFound.getFileId(), is(metadata.getFileId()));
        assertThat(metadataFound.getOwningContentUser(), is(metadata.getOwningContentUser()));
        assertThat(metadataFound.getCreatedOn(), is(metadata.getCreatedOn()));
        assertThat(metadataFound.getFileName(), is(metadata.getFileName()));
        assertThat(metadataFound.getContentType(), is(metadata.getContentType()));
        assertThat(metadataFound.getContentEncoding(), is(metadata.getContentEncoding()));
        assertThat(metadataFound.getContentLength(), is(metadata.getContentLength()));
        assertThat(metadataFound.getContentMd5(), is(metadata.getContentMd5()));

        assertThat(metadataFound.getToBeDeletedOn(), is(metadata.getToBeDeletedOn()));
        assertThat(metadataFound.getDeletedOn(), is(metadata.getDeletedOn()));
        assertThat(metadataFound.getCommittedEventId(), is(metadata.getCommittedEventId()));
        assertThat(metadataFound.getCommittedOn(), is(metadata.getCommittedOn()));
        assertThat(metadataFound.getFileMetadata(), is(metadata.getFileMetadata()));
    }

    private Metadata metadataOf(final UUID fileId, final UUID owningContextUser, final ZonedDateTime createdOn,
                                final String fileName, final String contentType, final String contentEncoding,
                                final int contentLength, final String contentMd5, final String fileMetaData) {
        final Metadata metadata = new Metadata(fileId, owningContextUser, createdOn, fileName,
                contentType, contentEncoding, contentLength, contentMd5);
        metadata.setToBeDeletedOn(createdOn.plusHours(2));
        metadata.setDeletedOn(createdOn.plusHours(3));
        metadata.setCommittedEventId(randomUUID());
        metadata.setCommittedOn(createdOn.plusSeconds(5));
        metadata.setFileMetadata(fileMetaData);
        return metadata;
    }
}


