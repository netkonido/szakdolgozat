package com.lazarbela.ikthesis;

import com.lazarbela.ikthesis.service.DataService;
import com.lazarbela.ikthesis.service.FileService;
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
    private final FileService fileService;

    private static final Duration sessionTimeout = Duration.ofMinutes(10);

    @Autowired
    public SessionCleanupTask(SessionService sessionService, FileService fileService) {
        this.sessionService = sessionService;
        this.fileService = fileService;
    }

    @Scheduled(fixedRate = 60_000) //every minute
    public void cleanupSessions() {
        log.info("Running session cleanup at {}.", dateFormat.format(new Date()));
        try
        {
            Set<String> deletedSessions = sessionService.deleteOldSessions(sessionTimeout);
            if(!deletedSessions.isEmpty()) {
                log.info("Removed {} sessions. Ids:", (long) deletedSessions.size());
                for(String id : deletedSessions)
                {
                    log.info(id);
                }
            }
            else {
                log.info("No sessions to remove");
            }

            log.info(fileService.cleanFileStorage(sessionService.getSessionIds()));

        } catch (Exception e) {
            log.error("Session cleanup at {} encountered an error: {}", dateFormat.format(new Date()), e.getMessage());
        }
    }
}
