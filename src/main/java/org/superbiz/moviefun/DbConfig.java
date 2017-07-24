package org.superbiz.moviefun;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.jdbc.DataSourceBuilder;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

/**
 * Created by pivotal on 7/24/17.
 */
@Configuration
public class DbConfig {
    @Bean
    @ConfigurationProperties("moviefun.datasources.movies")
    public DataSource moviesDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DataSource hikariMoviesDataSource(DataSource moviesDataSource) {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(moviesDataSource);
        return hikariDataSource;
    }

    @Bean
    @ConfigurationProperties("moviefun.datasources.albums")
    public DataSource albumsDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Bean
    public DataSource hikariAlbumsDataSource(DataSource albumsDataSource) {
        HikariDataSource hikariDataSource = new HikariDataSource();
        hikariDataSource.setDataSource(albumsDataSource);
        return hikariDataSource;
    }

    @Bean
    public HibernateJpaVendorAdapter getHibernateJpaVendorAdapter() {
        HibernateJpaVendorAdapter adapter = new HibernateJpaVendorAdapter();
        adapter.setGenerateDdl(true);
        adapter.setDatabasePlatform("org.hibernate.dialect.MySQL5Dialect");
        adapter.setDatabase(Database.MYSQL);
        return adapter;
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean moviesEntityManager(DataSource hikariMoviesDataSource, HibernateJpaVendorAdapter adapter) {
        return createEntityManager(hikariMoviesDataSource, adapter, "unit-movies");
    }

    @Bean
    public LocalContainerEntityManagerFactoryBean albumsEntityManager(DataSource hikariAlbumsDataSource, HibernateJpaVendorAdapter adapter) {
        return createEntityManager(hikariAlbumsDataSource, adapter, "unit-albums");
    }

    @Bean
    public PlatformTransactionManager moviesTransactionManager(@Qualifier(value = "moviesEntityManager") LocalContainerEntityManagerFactoryBean moviesEntityManager) {
        return createTransactionManager(moviesEntityManager);
    }

    @Bean
    public PlatformTransactionManager albumsTransactionManager(@Qualifier(value = "albumsEntityManager") LocalContainerEntityManagerFactoryBean albumsEntityManager) {
        return createTransactionManager(albumsEntityManager);
    }

    private LocalContainerEntityManagerFactoryBean createEntityManager(DataSource dataSource, HibernateJpaVendorAdapter adapter, String persistenceUnit) {
        LocalContainerEntityManagerFactoryBean bean = new LocalContainerEntityManagerFactoryBean();
        bean.setDataSource(dataSource);
        bean.setJpaVendorAdapter(adapter);
        bean.setPackagesToScan("org.superbiz.moviefun");
        bean.setPersistenceUnitName(persistenceUnit);
        return bean;
    }

    private PlatformTransactionManager createTransactionManager(LocalContainerEntityManagerFactoryBean entityManager) {
        return new JpaTransactionManager(entityManager.getObject());
    }
}
