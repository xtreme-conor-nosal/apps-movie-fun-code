package org.superbiz.moviefun;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albumsapi.AlbumFixtures;
import org.superbiz.moviefun.albumsapi.AlbumInfo;
import org.superbiz.moviefun.albumsapi.AlbumsClient;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesRepository;

import java.util.Map;

@Controller
public class HomeController {

    private final MoviesRepository moviesRepository;
    private final AlbumsClient albumsClient;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;

    public HomeController(MoviesRepository moviesRepository, AlbumsClient albumsClient, MovieFixtures movieFixtures, AlbumFixtures albumFixtures) {
        this.moviesRepository = moviesRepository;
        this.albumsClient = albumsClient;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        for (Movie movie : movieFixtures.load()) {
            moviesRepository.addMovie(movie);
        }

        for (AlbumInfo album : albumFixtures.load()) {
            albumsClient.addAlbum(album);
        }

        model.put("movies", moviesRepository.getMovies());
        model.put("albums", albumsClient.getAlbums());

        return "setup";
    }
}
