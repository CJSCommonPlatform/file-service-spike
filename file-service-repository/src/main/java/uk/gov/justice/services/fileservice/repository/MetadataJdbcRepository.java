package uk.gov.justice.services.fileservice.repository;

import static java.lang.String.format;
import static uk.gov.justice.services.common.converter.ZonedDateTimes.fromSqlTimestamp;
import static uk.gov.justice.services.common.converter.ZonedDateTimes.toSqlTimestamp;

import uk.gov.justice.services.jdbc.persistence.AbstractJdbcRepository;
import uk.gov.justice.services.jdbc.persistence.JdbcRepositoryException;
import uk.gov.justice.services.jdbc.persistence.PreparedStatementWrapper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import javax.naming.NamingException;

/**
 * The type Metadata jdbc repository.
 */
public class MetadataJdbcRepository extends AbstractJdbcRepository<Metadata> {

    static final String PRIMARY_KEY_ID = "file_id";
    static final String COL_OWNING_CONTEXT_ID = "owning_context_user";
    static final String COL_CREATED_ON = "created_on";
    static final String COL_FILE_NAME = "file_name";
    static final String COL_CONTENT_TYPE = "content_type";
    static final String COL_CONTENT_ENCODING = "content_encoding";
    static final String COL_CONTENT_LENGTH = "content_length";
    static final String COL_CONTENT_MD5 = "content_md5";

    static final String COL_TO_BE_DELETED_ON = "to_be_deleted_on";
    static final String COL_DELETED_ON = "deleted_on";
    static final String COL_COMMITTED_EVENT_ID = "committed_event_id";
    static final String COL_COMMITTED_ON = "committed_on";
    static final String COL_FILE_METADATA = "file_metadata";

    static final String SQL_FIND_BY_FILE_ID = "SELECT * FROM metadata WHERE file_id=? ";
    static final String SQL_INSERT_METADATA = "INSERT INTO metadata(file_id,owning_context_user,created_on,file_name," +
            "content_type,content_encoding,content_length,content_md5,to_be_deleted_on," +
            "deleted_on,committed_event_id,committed_on,file_metadata) " +
            "VALUES(?, ?, ?, ?,?,?,?,?,?,?,?,?,?)";

    private static final String READING_METADATA_EXCEPTION = "Exception while reading metadata %s";
    private static final String JNDI_DS_FILE_STORE_PATTERN = "java:/app/%s/DS.filestore";

    /**
     * Insert.
     *
     * @param metadata the metadata
     */
    public void insert(final Metadata metadata) {
        try (final Connection connection = getDataSource().getConnection();
             final PreparedStatement ps = connection.prepareStatement(SQL_INSERT_METADATA)) {
            ps.setObject(1, metadata.getFileId());
            ps.setObject(2, metadata.getOwningContentUser());
            ps.setTimestamp(3, toSqlTimestamp(metadata.getCreatedOn()));
            ps.setString(4, metadata.getFileName());
            ps.setString(5, metadata.getContentType());
            ps.setString(6, metadata.getContentEncoding());
            ps.setInt(7, metadata.getContentLength());
            ps.setString(8, metadata.getContentMd5());
            ps.setTimestamp(9, toSqlTimestamp(metadata.getToBeDeletedOn()));
            ps.setTimestamp(10, toSqlTimestamp(metadata.getDeletedOn()));
            ps.setObject(11, metadata.getCommittedEventId());
            ps.setTimestamp(12, toSqlTimestamp(metadata.getCommittedOn()));
            ps.setString(13, metadata.getFileMetadata());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new JdbcRepositoryException(format("Exception while storing metadata %s",
                    metadata.getFileId()), e);
        }
    }

    /**
     * Find by file id metadata.
     *
     * @param fileId the file id
     * @return the metadata
     */
    public Metadata findByFileId(final UUID fileId) {
        try (final PreparedStatementWrapper ps = preparedStatementWrapperOf(SQL_FIND_BY_FILE_ID)) {
            ps.setObject(1, fileId);
            return entityFrom(ps.executeQuery());
        } catch (SQLException e) {
            throw new JdbcRepositoryException(format(READING_METADATA_EXCEPTION, fileId), e);
        }
    }


    /**
     * Jndi name string.
     *
     * @return the string
     * @throws NamingException the naming exception
     */
    @Override
    protected String jndiName() throws NamingException {
        return format(JNDI_DS_FILE_STORE_PATTERN, warFileName());
    }


    /**
     * Entity from metadata.
     *
     * @param resultSet the result set
     * @return the metadata
     * @throws SQLException the sql exception
     */
    @Override
    protected Metadata entityFrom(final ResultSet resultSet) throws SQLException {
        resultSet.next();
        final Metadata metaData = new Metadata(
                (UUID) resultSet.getObject(PRIMARY_KEY_ID),
                (UUID) resultSet.getObject(COL_OWNING_CONTEXT_ID),
                fromSqlTimestamp(resultSet.getTimestamp(COL_CREATED_ON)),
                resultSet.getString(COL_FILE_NAME),
                resultSet.getString(COL_CONTENT_TYPE),
                resultSet.getString(COL_CONTENT_ENCODING),
                resultSet.getInt(COL_CONTENT_LENGTH),
                resultSet.getString(COL_CONTENT_MD5));
        metaData.setToBeDeletedOn(fromSqlTimestamp(resultSet.getTimestamp(COL_TO_BE_DELETED_ON)));
        metaData.setDeletedOn(fromSqlTimestamp(resultSet.getTimestamp(COL_DELETED_ON)));
        metaData.setCommittedEventId((UUID) resultSet.getObject(COL_COMMITTED_EVENT_ID));
        metaData.setCommittedOn(fromSqlTimestamp(resultSet.getTimestamp(COL_COMMITTED_ON)));
        metaData.setFileMetadata(resultSet.getString(COL_FILE_METADATA));
        return metaData;
    }
}