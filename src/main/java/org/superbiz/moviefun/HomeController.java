package org.superbiz.moviefun;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;
    private final PlatformTransactionManager moviesTransactionManager;
    private final PlatformTransactionManager albumsTransactionManager;

    public HomeController(
        MoviesBean moviesBean,
        AlbumsBean albumsBean,
        PlatformTransactionManager moviesTransactionManager,
        PlatformTransactionManager albumsTransactionManager
    ) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.moviesTransactionManager = moviesTransactionManager;
        this.albumsTransactionManager = albumsTransactionManager;
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

        albumsBean.addAlbum(new Album("Massive Attack", "Mezzanine", 1998, 9));
        albumsBean.addAlbum(new Album("Radiohead", "OK Computer", 1997, 8));
        albumsBean.addAlbum(new Album("Radiohead", "Kid A", 2000, 9));

        albumsTransactionManager.commit(transaction);

        return albumsBean.getAlbums();
    }

    private List<Movie> createMovies() {
        TransactionStatus transaction = moviesTransactionManager.getTransaction(null);

        moviesBean.addMovie(new Movie("Wedding Crashers", "David Dobkin", "Comedy", 7, 2005));
        moviesBean.addMovie(new Movie("Starsky & Hutch", "Todd Phillips", "Action", 6, 2004));
        moviesBean.addMovie(new Movie("Shanghai Knights", "David Dobkin", "Action", 6, 2003));
        moviesBean.addMovie(new Movie("I-Spy", "Betty Thomas", "Adventure", 5, 2002));
        moviesBean.addMovie(new Movie("The Royal Tenenbaums", "Wes Anderson", "Comedy", 8, 2001));
        moviesBean.addMovie(new Movie("Zoolander", "Ben Stiller", "Comedy", 6, 2001));
        moviesBean.addMovie(new Movie("Shanghai Noon", "Tom Dey", "Comedy", 7, 2000));

        moviesTransactionManager.commit(transaction);

        return moviesBean.getMovies();
    }
}
