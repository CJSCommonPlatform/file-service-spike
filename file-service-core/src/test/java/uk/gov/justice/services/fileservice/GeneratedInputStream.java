package uk.gov.justice.services.fileservice;

import java.io.IOException;
import java.io.InputStream;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 
 * Generate an input stream of a fixed length
 *
 */
public class GeneratedInputStream extends InputStream {

    final Random random = new Random();

    final AtomicLong counter = new AtomicLong();

    final long maximum;

    public GeneratedInputStream(long size) {
        this.maximum = size;
    }

    @Override
    public int read() throws IOException {
        if (maximum <= counter.get()) {
            return -1;
        }
        counter.incrementAndGet();
        return random.nextInt();
    }
}
