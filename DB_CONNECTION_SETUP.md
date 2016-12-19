# Database Connection

## PCF service binding

It just works

### With connection pooling

Add the HikariCP dependency to project.

```
<dependency>
    <groupId>com.zaxxer</groupId>
    <artifactId>HikariCP</artifactId>
    <version>2.5.1</version>
</dependency>
```

## Legacy database

Manually configure datasource using `@Value` annotated fields.
   `@Value` gets injected using `application.properties`, `application.yaml`, Environment variables...
   
   ```
@Value("${pal.moviesDb.url}") String moviesDbUrl;
@Value("${pal.moviesDb.username}") String moviesDbUsername;
@Value("${pal.moviesDb.password}") String moviesDbPassword;

@Bean
@Primary
public DataSource moviesDataSource() {
    MysqlDataSource dataSource = new MysqlDataSource();
    dataSource.setURL(moviesDbUrl);
    dataSource.setUser(moviesDbUsername);
    dataSource.setPassword(moviesDbPassword);
    return dataSource;
}
```

Locally, setup in `application.yaml`

```
pal:
  moviesDb:
    url: jdbc:mysql://localhost:3306/movie_fun?useSSL=false
    username: root
    password:
```

on PCF setup environment variables, checking for the env should look like this:

```
$ cf env moviefun

...

PAL_MOVIES_DB_PASSWORD: <password>
PAL_MOVIES_DB_URL: jdbc:mysql://<server>:<port>/<db_name>
PAL_MOVIES_DB_USERNAME: <username>
```

### With connection pooling

Wrap existing DataSource with HikariCP connection pooled DataSource.

```
public static DataSource createConnectionPool(DataSource dataSource) {
    HikariConfig config = new HikariConfig();
    config.setDataSource(dataSource);
    
    // Customize connection pool here

    return new HikariDataSource(config);
}
```

## Multiple database connections

Setup two data sources.

Let's add an Album entity type, and display albums when we visit `/setup`
I want to be able to add the following to the JSP:

```
<h2>Seeded Database with the Following albums</h2>
<table width="500">
    <tr>
        <td><b>Title</b></td>
        <td><b>Artist</b></td>
        <td><b>Year</b></td>
    </tr>

    <c:forEach items="${requestScope.albums}" var="album">
        <tr>
            <td> ${album.title} </td>
            <td> ${album.artist} </td>
            <td> ${album.year} </td>
        </tr>
    </c:forEach>
</table>
```

The albums data should come from a Separate database.

### Steps

* Create database
* Create Album Entity
* Create AlbumsBean class to access the DB.
* Configure separate db connection (adding a new persistence unit).

    * Add qualifier to DataSources
        ```
        @Bean
        @Qualifier("albums")
        public DataSource albumsDataSource() {
            MysqlDataSource dataSource = new MysqlDataSource();
            
            dataSource.setURL(albumsDbUrl);
            dataSource.setUser(albumsDbUsername);
            dataSource.setPassword(albumsDbPassword);
            
            return createConnectionPool(dataSource);
        }
        ```

    * Create LocalContainerEntityManagerFactoryBean for each database using `@Qualifier` to differentiate them.
      Here, the persistence unit name is important, we will use it to inject the entity manager.
        ```
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
        ```

    * Configure JPA Vendor Adapter in code instead of using `application.yaml` (only one should suffice, not one per database)
        ```
        @Bean
        HibernateJpaVendorAdapter jpaVendorAdapter() {
            HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
            jpaVendorAdapter.setDatabase(Database.MYSQL);
            jpaVendorAdapter.setDatabasePlatform("org.hibernate.dialect.MySQL5InnoDBDialect");
            jpaVendorAdapter.setGenerateDdl(true);
            return jpaVendorAdapter;
        }
        ```
        
    * Configure Transaction Manager for each database using `@Qualifier` to differentiate them.
        ```
        @Bean
        @Qualifier("albums")
        PlatformTransactionManager albumsTransactionManager(@Qualifier("albums") LocalContainerEntityManagerFactoryBean factoryBean) {
            return new JpaTransactionManager(factoryBean.getObject());
        }
        ```
        
    * Inject EntityManager using the `@PersistenceContext` annotation (replacing the constructor injection)
        ```
        @PersistenceContext(unitName = "albums")
        private EntityManager entityManager;
        ```
        
    * Replace `@Transactional` annotation with manual transaction handling. 
      The TransactionManager should be injected in the controller.
        ```
        private List<Album> createAlbums() {
            TransactionStatus transaction = albumsTransactionManager.getTransaction(null);
    
            albumsBean.addAlbum(new Album("Massive Attack", "Mezzanine", 1998, 9));
            albumsBean.addAlbum(new Album("Radiohead", "OK Computer", 1997, 8));
            albumsBean.addAlbum(new Album("Radiohead", "Kid A", 2000, 9));
    
            albumsTransactionManager.commit(transaction);
    
            return albumsBean.getAlbums();
        }
        ```

### With connection pooling

Same as previously.
