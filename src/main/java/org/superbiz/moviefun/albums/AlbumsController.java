package org.superbiz.moviefun.albums;

import org.apache.tika.Tika;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.superbiz.moviefun.storage.Blob;
import org.superbiz.moviefun.storage.BlobStore;

import java.io.*;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

@Controller
@RequestMapping("/albums")
public class AlbumsController {

    private final AlbumsBean albumsBean;
    private final BlobStore blobStore;

    public AlbumsController(AlbumsBean albumsBean, BlobStore blobStore) {
        this.albumsBean = albumsBean;
        this.blobStore = blobStore;
    }

    @GetMapping
    public String index(Map<String, Object> model) {
        model.put("albums", albumsBean.getAlbums());
        return "albums";
    }

    @GetMapping("/{albumId}")
    public String details(@PathVariable long albumId, Map<String, Object> model) {
        model.put("album", albumsBean.find(albumId));
        return "albumDetails";
    }

    @PostMapping("/{albumId}/cover")
    public String uploadCover(@PathVariable long albumId, @RequestParam("file") MultipartFile uploadedFile) throws IOException {
        saveUploadToFile(uploadedFile, getCoverFile(albumId));

        return format("redirect:/albums/%d", albumId);
    }

    @GetMapping("/{albumId}/cover")
    public HttpEntity<byte[]> getCover(@PathVariable long albumId) throws IOException, URISyntaxException {
        String path = getCoverFile(albumId);
        byte[] imageBytes = getExistingCoverPath(path);
        HttpHeaders headers = createImageHttpHeaders(path, imageBytes);

        return new HttpEntity<>(imageBytes, headers);
    }

    private void saveUploadToFile(@RequestParam("file") MultipartFile uploadedFile, String targetFile) throws IOException {
        blobStore.put(new Blob(targetFile, uploadedFile.getInputStream(), uploadedFile.getContentType()));
    }

    private HttpHeaders createImageHttpHeaders(String coverFilePath, byte[] imageBytes) throws IOException {
        String contentType = new Tika().detect(coverFilePath);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType(contentType));
        headers.setContentLength(imageBytes.length);
        return headers;
    }

    private String getCoverFile(@PathVariable long albumId) {
        return format("covers/%d", albumId);
    }

    private byte[] getExistingCoverPath(String coverFile) throws URISyntaxException, IOException {
        Optional<Blob> blob = blobStore.get(coverFile);
        try (InputStream inputStream = blob.isPresent() ? blob.get().inputStream : getClass().getClassLoader().getResourceAsStream("default-cover.jpg")) {
            byte[] buffer = new byte[1024];
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            int count;
            do {
                count = inputStream.read(buffer);
                if (count > 0) {
                    os.write(buffer, 0, count);
                }
            } while (count > 0);
            return os.toByteArray();
        }
    }
}
