package uk.gov.justice.services.fileservice;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Assert;
import org.junit.Test;

public class ConsistentHashTest {

    @Test
    public void shouldDistributeUUIDSAcrossHundredBuckets() {

        final Map<Integer, AtomicInteger> buckets = new TreeMap<>();

        final List<UUID> l = Stream.generate(UUID::randomUUID).limit(1000000)
                        .collect(Collectors.toList());

        l.forEach(x -> {
            int bucket = ConsistentHash.getBucket(x, 100);
            if (!buckets.containsKey(bucket)) {
                buckets.put(bucket, new AtomicInteger());
            }
            buckets.get(bucket).incrementAndGet();
        });

        for (Map.Entry<Integer, AtomicInteger> me : buckets.entrySet()) {
            Assert.assertTrue(me.getValue().intValue() > 0);
        }
    }

}
