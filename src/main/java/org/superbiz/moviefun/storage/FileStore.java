package org.superbiz.moviefun.storage;

import org.apache.tika.Tika;

import java.io.*;
import java.util.Optional;

/**
 * Created by pivotal on 7/25/17.
 */
public class FileStore implements BlobStore {
    @Override
    public void put(Blob blob) throws IOException {
        File targetFile = new File(blob.name);
        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        byte[] buffer = new byte[1024];
        try (FileOutputStream outputStream = new FileOutputStream(targetFile); InputStream is = blob.inputStream) {
            int count;
            do {
                count = is.read(buffer);
                if (count > 0) {
                    outputStream.write(buffer, 0, count);
                }
            } while (count > 0);
        }
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        File targetFile = new File(name);
        if (targetFile.exists()) {
            String contentType = new Tika().detect(name);
            return Optional.of(new Blob(name, new FileInputStream(targetFile), contentType));
        }
        return Optional.empty();
    }
}
