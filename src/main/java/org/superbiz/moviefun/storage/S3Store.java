package org.superbiz.moviefun.storage;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.S3Object;

import java.io.IOException;
import java.util.Optional;

/**
 * Created by pivotal on 7/25/17.
 */
public class S3Store implements BlobStore {
    private final AmazonS3Client s3Client;
    private final String s3BucketName;

    public S3Store(AmazonS3Client s3Client, String s3BucketName) {
        this.s3Client = s3Client;
        this.s3BucketName = s3BucketName;
    }

    @Override
    public void put(Blob blob) throws IOException {
        if (!s3Client.doesBucketExist(s3BucketName)) {
            s3Client.createBucket(s3BucketName);
        }
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentType(blob.contentType);
        s3Client.putObject(s3BucketName, blob.name, blob.inputStream, metadata);
    }

    @Override
    public Optional<Blob> get(String name) throws IOException {
        if (!s3Client.doesBucketExist(s3BucketName)) {
            s3Client.createBucket(s3BucketName);
        }
        if (!s3Client.doesObjectExist(s3BucketName, name)) {
            return Optional.empty();
        } else {
            S3Object object = s3Client.getObject(s3BucketName, name);
            return Optional.of(new Blob(name, object.getObjectContent(), object.getObjectMetadata().getContentType()));
        }
    }
}
