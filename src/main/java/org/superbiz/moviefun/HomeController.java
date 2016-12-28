package org.superbiz.moviefun;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final PlatformTransactionManager moviesTransactionManager;
    private final PlatformTransactionManager albumsTransactionManager;
    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;

    public HomeController(
        MoviesBean moviesBean,
        AlbumsBean albumsBean,
        PlatformTransactionManager moviesTransactionManager,
        PlatformTransactionManager albumsTransactionManager,
        MovieFixtures movieFixtures,
        AlbumFixtures albumFixtures
    ) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.moviesTransactionManager = moviesTransactionManager;
        this.albumsTransactionManager = albumsTransactionManager;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;
    }


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        model.put("albums", createAlbums());
        model.put("movies", createMovies());
        return "setup";
    }


    private List<Album> createAlbums() {
        TransactionStatus transaction = albumsTransactionManager.getTransaction(null);

        for (Album album : albumFixtures.load()) {
            albumsBean.addAlbum(album);
        }

        albumsTransactionManager.commit(transaction);

        return albumsBean.getAlbums();
    }

    private List<Movie> createMovies() {
        TransactionStatus transaction = moviesTransactionManager.getTransaction(null);

        for (Movie movie : movieFixtures.load()) {
            moviesBean.addMovie(movie);
        }

        moviesTransactionManager.commit(transaction);

        return moviesBean.getMovies();
    }
}
