package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import org.springframework.web.client.RestTemplate;
import org.superbiz.moviefun.albumsapi.AlbumsClient;

@Configuration
public class ClientConfiguration {

    @Value("${albums.url}") String albumsUrl;

    @Bean
    public RestOperations restOperations() {
        return new RestTemplate();
    }

    @Bean
    public AlbumsClient albumsClient(RestOperations restOperations) {
        return new AlbumsClient(albumsUrl, restOperations);
    }
}
