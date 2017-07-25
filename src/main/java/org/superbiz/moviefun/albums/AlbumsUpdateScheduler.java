package org.superbiz.moviefun.albums;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.sql.DataSource;

@Configuration
@EnableAsync
@EnableScheduling
public class AlbumsUpdateScheduler {

    private static final long SECONDS = 1000;
    private static final long MINUTES = 60 * SECONDS;

    private final AlbumsUpdater albumsUpdater;
    private final JdbcTemplate jdbcTemplate;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public AlbumsUpdateScheduler(AlbumsUpdater albumsUpdater, DataSource dataSource) {
        this.albumsUpdater = albumsUpdater;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Scheduled(initialDelay = 15 * SECONDS, fixedRate = 2 * MINUTES)
    public void run() {
        if (startAlbumSchedulerTask()) {
            try {
                logger.debug("Starting albums update");
                albumsUpdater.update();

                logger.debug("Finished albums update");

            } catch (Throwable e) {
                logger.error("Error while updating albums", e);
            }
        } else {
            logger.debug("Nothing to start");
        }
    }

    private boolean startAlbumSchedulerTask() {
        String query = "UPDATE album_scheduler_task SET started_at=NOW() WHERE (started_at IS NULL OR TIMEDIFF(NOW(), started_at) > TIME('00:02:00'))";
        int rows = jdbcTemplate.update(query);
        return rows == 1;
    }
}
