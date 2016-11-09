package uk.gov.justice.services.fileservice;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.IntStream;

import org.junit.Before;
import org.junit.Test;

public class FileOperatorTest {

    /**
     * Shared resource without any concurrent locks
     */
    final List<Integer> sharedResource = new ArrayList<>();

    /**
     * A queue of integers to add to the shared resource 
     */
    final Queue<Integer> generator = new LinkedBlockingQueue<>();

    @Before
    public void before() {
        IntStream.range(0, 10000).forEach(e -> generator.add(e));
    }


    @Test
    public void shouldExecuteASuccessfulFileOperation() {

        final FileOperation fop = mock(FileOperation.class);

        when(fop.getUUID()).thenReturn(UUID.randomUUID());

        when(fop.isSuccess()).thenReturn(Boolean.TRUE);

        FileOperator.op().execute(fop);

        assertTrue(fop.isSuccess());
    }

    @Test
    public void shouldExecuteAFailedFileOperation() {

        final FileOperation fop = mock(FileOperation.class);

        when(fop.getUUID()).thenReturn(UUID.randomUUID());

        when(fop.isSuccess()).thenReturn(Boolean.FALSE);

        when(fop.getFailure()).thenReturn(Optional.of(new Exception()));

        FileOperator.op().execute(fop);

        assertFalse(fop.isSuccess());

        assertNotNull(fop.getFailure());
    }

    @Test
    public void shouldExecuteConcurrentSuccessfulOperationsWithoutExceptions() {

        final UUID uuid = UUID.randomUUID();

        final FileOperation fop = mock(FileOperation.class);

        when(fop.getUUID()).thenReturn(uuid);

        when(fop.isSuccess()).thenReturn(Boolean.TRUE);

        Executors.newFixedThreadPool(3).submit(new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < 10000; i++) {
                    FileOperator.op().execute(fop);
                    assertTrue(fop.isSuccess());
                }
            }
        });
    }


    @Test
    public void shouldExecuteConcurrentSuccessfulOperationsAddingItemsToSharedResource()
                    throws Exception {

        final UUID uuid = UUID.randomUUID();

        final WriteOperation wop = new WriteOperation(uuid);

        List<Future<?>> lf = new ArrayList<>();

        ExecutorService es = Executors.newFixedThreadPool(4);

        lf.add(es.submit(getRunnableWithRange(0, 2500, wop)));
        lf.add(es.submit(getRunnableWithRange(2500, 5000, wop)));
        lf.add(es.submit(getRunnableWithRange(5000, 7500, wop)));
        lf.add(es.submit(getRunnableWithRange(7500, 10000, wop)));

        // wait for all ops to complete
        for (Future<?> f : lf) {
            f.get();
            assertTrue(f.isDone());
        }

        //check that the resource contains all the information
        for (int i = 0; i < 10000; i++) {
            assertTrue(String.valueOf(i), sharedResource.contains(i));
        }

        assertEquals(String.valueOf(sharedResource.size()), 10000, sharedResource.size());
    }

    
    @Test
    public void shouldExecuteConcurrentSuccessfulOperationsAddingItemsSlowlyToSharedResource()
                    throws Exception {

        final UUID uuid = UUID.randomUUID();

        final DelayedWriteOperation wop = new DelayedWriteOperation(uuid);

        List<Future<?>> lf = new ArrayList<>();

        ExecutorService es = Executors.newFixedThreadPool(4);

        lf.add(es.submit(getRunnableWithRange(0, 150, wop)));
        lf.add(es.submit(getRunnableWithRange(150, 300, wop)));
        
        // wait for all ops to complete
        for (Future<?> f : lf) {
            if (f != null) {
                f.get();
            }
        }

        //check that the resource contains all the information
        for (int i = 0; i < 300; i++) {
            assertTrue(String.valueOf(i), sharedResource.contains(i));
        }

        assertEquals(String.valueOf(sharedResource.size()), 300, sharedResource.size());
    }

    private Runnable getRunnableWithRange(final int start, final int end, final FileOperation fop) {
        return new Runnable() {
            @Override
            public void run() {
                for (int i = start; i < end; i++) {
                    FileOperator.op().execute(fop);
                    assertTrue(fop.isSuccess());
                }
            }
        };
    }
    
    
    private final class WriteOperation implements FileOperation {

        private boolean success;

        private Exception exception;

        private UUID uuid;

        WriteOperation(final UUID uuid) {
            this.uuid = uuid;
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
            sharedResource.add(generator.remove());
            success = true;
        }

        @Override
        public void setFileOperationObserver(FileOperationObserver observer) {
        }
    }
    
    private final class DelayedWriteOperation implements FileOperation {

        private boolean success;

        private Exception exception;

        private UUID uuid;

        DelayedWriteOperation(final UUID uuid) {
            this.uuid = uuid;
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
            
            sharedResource.add(generator.remove());
            
            try {
                Thread.sleep(ThreadLocalRandom.current().nextLong(3, 10));
            } catch (InterruptedException e) {
                
            }
            
            success = true;
        }

        @Override
        public void setFileOperationObserver(FileOperationObserver observer) {
        }
    }
}
