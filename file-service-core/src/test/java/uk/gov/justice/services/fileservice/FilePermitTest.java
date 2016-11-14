package uk.gov.justice.services.fileservice;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.util.UUID;
import java.util.concurrent.locks.Lock;

import org.junit.Before;
import org.junit.Test;

public class FilePermitTest {

    FilePermit filePermit;

    @Before
    public void before() {
        filePermit = new FilePermit(UUID.randomUUID());
    }

    @Test
    public void shouldAcquireFilePermit() throws InterruptedException {
        final Lock lock = mock(Lock.class);
        filePermit.setLock(lock);
        filePermit.acquire();
        verify(lock).lock();
    }

    @Test
    public void shouldReleaseFilePermitWhenUsingTryWith() throws Exception {
        final Lock lock = mock(Lock.class);
        try (final FilePermit fp = new FilePermit(UUID.randomUUID())) {
            fp.setLock(lock);
        }
        verify(lock).unlock();
    }

    @Test
    public void shouldReleaseFilePermitWhenUsingTryWithAndExceptionIsThrown() throws Exception {
        final Lock lock = mock(Lock.class);
        try (final FilePermit fp = new FilePermit(UUID.randomUUID())) {
            fp.setLock(lock);
            throw new RuntimeException("Testing Permit closing");
        } catch (RuntimeException e) {
            verify(lock).unlock();
        }
    }
}
