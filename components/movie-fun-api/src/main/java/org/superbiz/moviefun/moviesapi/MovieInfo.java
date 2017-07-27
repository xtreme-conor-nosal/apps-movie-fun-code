package org.superbiz.moviefun.moviesapi;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.io.Serializable;

@Entity
public class MovieInfo implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long id;

    private String director;
    private String title;
    private int year;
    private String genre;
    private int rating;

    public MovieInfo() {
    }

    public MovieInfo(String title, String director, String genre, int rating, int year) {
        this.director = director;
        this.title = title;
        this.year = year;
        this.genre = genre;
        this.rating = rating;
    }

    public MovieInfo(String director, String title, int year) {
        this.director = director;
        this.title = title;
        this.year = year;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getDirector() {
        return director;
    }

    public void setDirector(String director) {
        this.director = director;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        MovieInfo movieInfo = (MovieInfo) o;

        if (id != movieInfo.id) return false;
        if (year != movieInfo.year) return false;
        if (rating != movieInfo.rating) return false;
        if (director != null ? !director.equals(movieInfo.director) : movieInfo.director != null) return false;
        if (title != null ? !title.equals(movieInfo.title) : movieInfo.title != null) return false;
        return genre != null ? genre.equals(movieInfo.genre) : movieInfo.genre == null;
    }

    @Override
    public int hashCode() {
        int result = (int) (id ^ (id >>> 32));
        result = 31 * result + (director != null ? director.hashCode() : 0);
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + year;
        result = 31 * result + (genre != null ? genre.hashCode() : 0);
        result = 31 * result + rating;
        return result;
    }
}

