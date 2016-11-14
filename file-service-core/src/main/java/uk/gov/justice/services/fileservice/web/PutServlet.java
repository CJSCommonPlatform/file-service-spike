package uk.gov.justice.services.fileservice.web;

import static java.lang.String.format;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.UUID;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.input.CountingInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Strings;

import uk.gov.justice.services.fileservice.BigFileWriteable;
import uk.gov.justice.services.fileservice.DirectoryPath;
import uk.gov.justice.services.fileservice.DirectoryScanner;
import uk.gov.justice.services.fileservice.FileOperation;
import uk.gov.justice.services.fileservice.FileOperator;
import uk.gov.justice.services.fileservice.SmallFileWriteable;

@WebServlet(name = "putServlet", urlPatterns = {"/put"})
public class PutServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private final DirectoryScanner scanner = new DirectoryScanner();

    private DirectoryPath directoryPath;

    private transient Logger LOG = LoggerFactory.getLogger(PutServlet.class);

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

    @Override
    protected void doPut(final HttpServletRequest request, final HttpServletResponse response)
                    throws ServletException, IOException {
        final int length = request.getContentLength();
        FileOperation fop;
        CountingInputStream cin = new CountingInputStream(request.getInputStream());
        try {
            if (length > 0 && length < 1024000) {
                fop = new SmallFileWriteable(cin, directoryPath, UUID.randomUUID());
            } else {
                fop = new BigFileWriteable(cin, directoryPath, UUID.randomUUID());
            }

            FileOperator.op().execute(fop);

            // basic integrity check
            if (cin.getByteCount() == fop.getFileLength()) {
                handleResponse(fop, response);
            } else {
                handleErrorResponse(
                                format("Error processing UUID %s input size %s does not match file size %s",
                                                fop.getUUID().toString(), cin.getByteCount(),
                                                fop.getFileLength()),
                                null, response);
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
