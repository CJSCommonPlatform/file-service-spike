package uk.gov.justice.services.fileservice;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

public class FileOperatorWriteFileTest {

    @Rule
    public TemporaryFolder storagePoolLocation = new TemporaryFolder();

    private final DirectoryScanner scanner = new DirectoryScanner();

    private DirectoryPath directoryPath;

    private ByteBuffer oneKB = ByteBuffer.wrap(new byte[1024]);

    private ByteBuffer oneMB = ByteBuffer.wrap(new byte[1024000]);


    @Before
    public void before() throws IOException {
        storagePoolLocation.newFolder("dir-0");
        storagePoolLocation.newFolder("dir-1");
        directoryPath = scanner.scan(storagePoolLocation.getRoot(), "dir-");

        ThreadLocalRandom.current().nextBytes(oneKB.array());
        ThreadLocalRandom.current().nextBytes(oneMB.array());
    }

    @Test
    public void shouldExecuteConcurrentSuccessfulOperations() throws Exception {

        final UUID uuidOne = UUID.randomUUID();

        final UUID uuidTwo = UUID.randomUUID();

        RecordingFileOperationObserver smallreco = new RecordingFileOperationObserver();
        final FileOperation smallfop = new WriteOperation(uuidOne, true);
        smallfop.setFileOperationObserver(smallreco);

        RecordingFileOperationObserver bigreco = new RecordingFileOperationObserver();
        final FileOperation bigfop = new WriteOperation(uuidTwo, true);
        bigfop.setFileOperationObserver(bigreco);

        final List<Future<FileOperation>> lf = new ArrayList<>();

        ExecutorService es = Executors.newFixedThreadPool(3);

        lf.add(es.submit(() -> {
            FileOperator.op().execute(bigfop);
            return bigfop;
        }));

        lf.add(es.submit(() -> {
            FileOperator.op().execute(smallfop);
            return smallfop;
        }));

        // wait for all ops to complete
        for (Future<?> f : lf) {
            if (f != null) {
                f.get();
                assertTrue(f.isDone());
            }
        }
    }


    private final class WriteOperation implements FileOperation {

        private boolean success;

        private Exception exception;

        private UUID uuid;

        private boolean smallFile;

        private FileOperationObserver observer;

        WriteOperation(final UUID uuid, final boolean smallFile) {
            this.uuid = uuid;
            this.smallFile = smallFile;
        }

        @Override
        public boolean isSuccess() {
            return success;
        }

        @Override
        public UUID getUUID() {
            return uuid;
        }

        @Override
        public Optional<Exception> getFailure() {
            return Optional.ofNullable(exception);
        }

        @Override
        public void execute() {
            observer.beginExecution();
            final String fileName = new FileLookup(directoryPath).getFileName(getUUID());
            try {
                if (smallFile) {
                    IOUtils.write(oneKB.array(), new FileOutputStream(new File(fileName)));
                } else {
                    IOUtils.write(oneMB.array(), new FileOutputStream(new File(fileName)));
                }
                success = true;
            } catch (Exception e) {
                exception = e;
            } finally {
                observer.endExecution();
            }
        }

        @Override
        public void setFileOperationObserver(final FileOperationObserver observer) {
            this.observer = observer;
        }
    }

    private class RecordingFileOperationObserver implements FileOperationObserver {

        private long startTime;

        private long endTime;

        @Override
        public void endExecution() {
            endTime = System.currentTimeMillis();
        }

        @Override
        public void beginExecution() {
            startTime = System.currentTimeMillis();
        }

        public long getStartTime() {
            return startTime;
        }

        public long getEndTime() {
            return endTime;
        }
    }
}
