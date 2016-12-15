package moviefun;


import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;
import org.superbiz.moviefun.Application;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class, webEnvironment = RANDOM_PORT)
public class MovieFunTest {

    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setup() {
        jdbcTemplate.execute("DELETE FROM movie");
    }

    @Test
    public void smokeTest() {
        String homePage = restTemplate.getForObject("/", String.class);

        assertThat(homePage).contains("Please select one of the following links:");

        String setupPage = restTemplate.getForObject("/setup", String.class);

        assertThat(setupPage).contains("Wedding Crashers");
        assertThat(setupPage).contains("Starsky & Hutch");
        assertThat(setupPage).contains("Shanghai Knights");
        assertThat(setupPage).contains("I-Spy");
        assertThat(setupPage).contains("The Royal Tenenbaums");

        String movieFunPage = restTemplate.getForObject("/moviefun", String.class);

        assertThat(movieFunPage).contains("Wedding Crashers");
        assertThat(movieFunPage).contains("David Dobkin");
    }
}
