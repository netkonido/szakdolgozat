package com.lazarbela.ikthesis;

import com.lazarbela.ikthesis.service.DataService;
import com.lazarbela.ikthesis.service.SessionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.text.SimpleDateFormat;
import java.time.Duration;
import java.util.Date;
import java.util.Set;

@Component
public class SessionCleanupTask {
    private static final Logger log = LoggerFactory.getLogger(SessionCleanupTask.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");

    private final SessionService sessionService;

    private static final Duration sessionTimeout = Duration.ofMinutes(10);

    @Autowired
    public SessionCleanupTask(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Scheduled(fixedRate = 60_000) //every minute
    public void cleanupSessions() {
        log.info("Started cleanup of sessions at {}", dateFormat.format(new Date()));
        try
        {
            Set<String> deletedSessions = sessionService.deleteOldSessions(sessionTimeout);
            log.info("Cleaned up {} sessions.", (long) deletedSessions.size());
            if(!deletedSessions.isEmpty())
                log.info("Session ids: {}", deletedSessions);
        } catch (Exception e) {
            log.error("Encountered an error while cleaning up sessions: {}", e.getMessage());
        }
        finally{
            log.info("Finished cleanup of sessions at {}", dateFormat.format(new Date()));
        }
    }
}
