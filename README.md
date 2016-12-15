# Movie Fun!

## Build and deploy to PCF

```
$ mvn install
$ cf push moviefun \
    -p target/moviefun.war \
    -b https://github.com/cloudfoundry-community/tomee-buildpack.git\#v3.10
```

## Re-platforming steps

 * Write a spring application smoke test
 * Wrap the application in a Spring Boot application
 * Build Jar instead of war
 * Enable JSP rendering
 * Create controller for standalone JSP files (move index.jsp and setup.jsp to WEB-INF)
 * Make MoviesBean injectable
 * Transform setup.jsp not to need movies bean (update controller)
 * Mount servlet (create simple example without database access)
 * Deploy with Java Buildpack

 * Cleanup:
    * Get rid of persistence.xml
    * Get rid of web.xml

## 
