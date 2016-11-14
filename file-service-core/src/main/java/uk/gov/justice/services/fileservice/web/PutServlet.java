package uk.gov.justice.services.fileservice.web;

import static java.lang.String.format;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.security.DigestInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import uk.gov.justice.services.fileservice.AtomicMover;
import uk.gov.justice.services.fileservice.BigFileWriteable;
import uk.gov.justice.services.fileservice.Digester;
import uk.gov.justice.services.fileservice.DirectoryPath;
import uk.gov.justice.services.fileservice.DirectoryScanner;
import uk.gov.justice.services.fileservice.FileLookup;
import uk.gov.justice.services.fileservice.FileOperation;
import uk.gov.justice.services.fileservice.FileOperator;
import uk.gov.justice.services.fileservice.SmallFileWriteable;
import uk.gov.justice.services.fileservice.TempDirectoryBuilder;

@WebServlet(name = "putServlet", urlPatterns = {"/put"})
public class PutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final DirectoryScanner scanner = new DirectoryScanner();

    private DirectoryPath directoryPath;

    private static final Logger LOG = LoggerFactory.getLogger(PutServlet.class);

    @Override
    public void init() {
        String storagePool = System.getProperty("storagePool");
        if (Strings.isNullOrEmpty(storagePool)) {
            storagePool = new DirectoryPath().getStoragePoolLocation();
        }
        String directoryPrefix = System.getProperty("directoryPrefix");
        if (Strings.isNullOrEmpty(directoryPrefix)) {
            directoryPrefix = new DirectoryPath().getDirectoryNamePrefix();
        }

        directoryPath = scanner.scan(new File(storagePool), directoryPrefix);

        new TempDirectoryBuilder().createTempDirectories(directoryPath);

        try {
            MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOG.error("SHA-256 not available ?", e);
            throw new IllegalStateException("SHA-256 not available ?");
        }
    }

    @Override
    protected void doGet(final HttpServletRequest request, final HttpServletResponse response)
                    throws ServletException, IOException {
        final OutputStream os = response.getOutputStream();
        try {
            os.write("v-1.0".getBytes());
        } finally {
            os.close();
        }
    }

    private boolean compareMessageDigest(final File file, final MessageDigest toCompare) throws Exception {
        MessageDigest md= new Digester().getMessageDigest(file, "SHA-256");
        return Arrays.equals(toCompare.digest(), md.digest());
    }

    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response)
                    throws ServletException, IOException {
        
        final int length = request.getContentLength();
        
        FileOperation fop;
        
        MessageDigest sha256;
        
        try {
            sha256 = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            LOG.error("SHA-256 not available ?", e);
            throw new IllegalStateException("SHA-256 not available ?");
        }
        
        final DigestInputStream din =
                        new DigestInputStream((InputStream) request.getInputStream(), sha256);
        try {

            final UUID uuid = UUID.randomUUID();

            if (length > 0 && length < 1024000) {
                fop = new SmallFileWriteable(din, directoryPath, uuid);
            } else {
                fop = new BigFileWriteable(din, directoryPath, uuid);
            }

            FileOperator.op().execute(fop);

            new AtomicMover().atomicMove(directoryPath, uuid);

            final File file = new File(new FileLookup(directoryPath).getFileName(uuid));

            if (!file.exists()) {

                LOG.error(format("Atomic move failed but did not throw an exception for UUID %s",
                                uuid.toString()));

                handleErrorResponse("Unknown error while processing request", null, response);

                return;
            }

            if (compareMessageDigest(file, sha256)) {
                
                handleResponse(fop, response);
                
            } else {

                LOG.error(format("Message digest check failed for UUID %s",
                                uuid.toString()));

                handleErrorResponse("Unknown error while processing request.", null, response);
            }

        } catch (Exception e) {
            
            LOG.error(format("Error processing request", e));
        
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private void handleResponse(final FileOperation fop, final HttpServletResponse response)
                    throws IOException {
        if (fop.isSuccess()) {
            final OutputStream os = response.getOutputStream();
            try {
                os.write(fop.getUUID().toString().getBytes(Charset.forName("UTF-8")));
            } finally {
                os.close();
            }
            response.setStatus(SC_OK);
        } else {
            final String msg = format("Error processing %s", fop.getUUID().toString());
            handleErrorResponse(msg, fop.getFailure().isPresent() ? fop.getFailure().get() : null,
                            response);
        }
    }

    private void handleErrorResponse(String errorMessage, Exception exception,
                    final HttpServletResponse response) throws IOException {
        LOG.error(errorMessage, exception);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }
}
