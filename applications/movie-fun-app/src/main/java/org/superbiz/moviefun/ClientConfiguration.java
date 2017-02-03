package org.superbiz.moviefun;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestOperations;
import org.superbiz.moviefun.albumsapi.AlbumsClient;
import org.superbiz.moviefun.moviesapi.MoviesClient;
import org.superbiz.moviefun.restsupport.RestTemplate;

@Configuration
public class ClientConfiguration {

    @Value("${albums.url}") String albumsUrl;
    @Value("${movies.url}") String moviesUrl;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

    @Bean
    public AlbumsClient albumsClient(RestOperations restOperations, RestTemplate restTemplate) {
        return new AlbumsClient(albumsUrl, restOperations, restTemplate);
    }

    @Bean
    public MoviesClient moviesClient(RestOperations restOperations) {
        return new MoviesClient(moviesUrl, restOperations);
    }
}
