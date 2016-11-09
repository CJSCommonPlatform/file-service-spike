package uk.gov.justice.services.fileservice;

import static com.google.common.hash.Hashing.consistentHash;
import static com.google.common.hash.Hashing.md5;

import java.util.UUID;

import com.google.common.base.Charsets;
import com.google.common.hash.Hashing;

/**
 * Use the Guava implementation of Jump Consistent Hash Function based on the paper <br/>
 * <br/>
 * https://arxiv.org/ftp/arxiv/papers/1406/1406.2294.pdf <br/>
 * <blockquote> The function satisfies the two properties:
 * <ol>
 * <li>about the same number of keys map to each bucket</li>
 * <li>the mapping from key to bucket is perturbed as little as possible when the number of buckets
 * is changed.</li>
 * </ol>
 * </blockquote> <br/>
 * <br/>
 * It is a fast minimal memory consistent hash algorithm.
 *
 */
public class ConsistentHash {

    /**
     * Private constructor to prevent instantiation
     */
    private ConsistentHash() {}

    /**
     * Get the bucket
     * 
     * @param uuid to allocate
     * @param noBuckets to allocate to
     * @return bucket number in the range 0 to noBuckets
     */
    public static final int getBucket(final UUID uuid, final int noBuckets) {
        return consistentHash(md5().hashString(uuid.toString(), Charsets.UTF_8),
                        noBuckets);
    }

}
