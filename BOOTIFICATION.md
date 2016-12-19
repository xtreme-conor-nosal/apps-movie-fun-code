# Bootification

 * Run app in TomEE
 * Write smoke test / run
 * `cf push`
 * Introduce Spring boot
     * Spring boot dependencies in the build
        * Spring boot plugin
        * starter-web
        * starter-tomcat
        * tomcat-embed-jasper (JSP)
        * starter-data-jpa
        * database driver (embedded into war)
     * Write application class
        * Run it see what happens
        
## Get Simple JSP to work

application.yaml

```
spring:
   mvc.view:
     prefix: /WEB-INF/
     suffix: .jsp
```

bind JSPs to controller actions

```
@Controller
public class HomeController {

    @GetMapping("/")
    public String index() {
         return "index";
    }
}
```

move `index.jsp` file into `webapp/WEB-INF`.


## Simple Database connection setup

Local MySQL on dev machine, MySQL service on PCF.

Create database locally:
```
$ mysql -uroot
> create database movie_fun;
```

Setup `application.yaml`

```
datasource:
    url: jdbc:mysql://localhost:3306/movie_fun?useSSL=false
    username: root
jpa:
    generate-ddl: true
    properties.hibernate.dialect: org.hibernate.dialect.MySQL5Dialect
```


## Convert JSP accessing the database

remove usage of EJB in JSP, move it to the controller.

* Make `MoviesBean` EJB injectable: 
    * replace `@Stateless` with `@Repository`
    * inject `EntityManager` (just works because of spring-data-jpa-starter)
    * no need for `@PersistenceContext`
    * remove `persistence.xml`

implement controller


```
@Controller
public class HomeController {

    ...

    @Transactional
    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        moviesBean.addMovie(new Movie("Wedding Crashers", "David Dobkin", "Comedy", 7, 2005));
        ...
        
        model.put("movies", moviesBean.getMovies());
        return "setup";
    }
}
```

change JSP to use created "movies" variable

```
<c:forEach items="${requestScope.movies}" var="movie">
    <tr>
        <td> ${movie.title} </td>
        <td> ${movie.director} </td>
        <td> ${movie.genre} </td>
    </tr>
</c:forEach>
```

move JSP files into `webapp/WEB-INF`.

## Mount WebServlets

Replace `web.xml` with `ServletRegistrationBean` declarations in a configuration class.

```
@Bean
public ServletRegistrationBean actionServletRegistration(ActionServlet actionServlet) {
    return new ServletRegistrationBean(actionServlet, "/moviefun/*");
}
```

Make `Servlet` injectable:

* Annotate with `@Component`
* Create constructor with `MoviesBean` argument
* Remove `@EJB` annotation from field 


## Deploy to PCF with MySQL service

Create service and binding

```
$ cf create-service p-mysql 100mb-dev movies-mysql
$ cf bind-service moviefun movies-mysql
$ cf push moviefun -p target/moviefun.war
```
