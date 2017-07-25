package org.superbiz.moviefun.storage;

import java.io.InputStream;

/**
 * Created by pivotal on 7/25/17.
 */
public class Blob {
    public final String name;
    public final InputStream inputStream;
    public final String contentType;

    public Blob(String name, InputStream inputStream, String contentType) {
        this.name = name;
        this.inputStream = inputStream;
        this.contentType = contentType;
    }
}
