package org.superbiz.moviefun.storage;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by pivotal on 7/25/17.
 */
public interface BlobStore {
    void put(Blob blob) throws IOException;

    Optional<Blob> get(String name) throws IOException;
}
