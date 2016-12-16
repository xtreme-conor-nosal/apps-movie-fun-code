package org.superbiz.moviefun;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration
public class AlbumsDbConfig {

    @Value("${pal.albumsDb.url}") String albumsDbUrl;
    @Value("${pal.albumsDb.username}") String albumsDbUsername;
    @Value("${pal.albumsDb.password}") String albumsDbPassword;

    @Bean
    @Qualifier("albums")
    public DataSource albumsDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL(albumsDbUrl);
        dataSource.setUser(albumsDbUsername);
        dataSource.setPassword(albumsDbPassword);
        return dataSource;
    }

    @Bean
    @Qualifier("albums")
    LocalContainerEntityManagerFactoryBean albumsEntityManagerFactoryBean(DataSource albumsDataSource, HibernateJpaVendorAdapter jpaVendorAdapter) {
        LocalContainerEntityManagerFactoryBean factoryBean = new LocalContainerEntityManagerFactoryBean();

        factoryBean.setDataSource(albumsDataSource);
        factoryBean.setJpaVendorAdapter(jpaVendorAdapter);
        factoryBean.setPackagesToScan(getClass().getPackage().getName());
        factoryBean.setPersistenceUnitName("albums");

        return factoryBean;
    }

    @Bean
    @Qualifier("albums")
    PlatformTransactionManager albumsTransactionManager(@Qualifier("albums") LocalContainerEntityManagerFactoryBean factoryBean) {
        return new JpaTransactionManager(factoryBean.getObject());
    }
}
