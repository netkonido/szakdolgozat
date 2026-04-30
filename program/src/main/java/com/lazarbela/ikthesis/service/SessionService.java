package com.lazarbela.ikthesis.service;

import com.lazarbela.ikthesis.model.Session;
import com.lazarbela.ikthesis.repository.SessionRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final FileService fileService;

    @Autowired
    public SessionService(SessionRepository sessionRepository, FileService fileService)
    {
        this.sessionRepository = sessionRepository;
        this.fileService = fileService;
    }

    //use this in dataservice
    public Session getSessionById (String sessionId)
    {
        Optional<Session> session = sessionRepository.findById(sessionId);
        if(session.isEmpty())
            throw new IllegalArgumentException("Session id not found");
        updateSessionTimestamp(session.get());
        return session.get();
    }

    public Session newSession()
    {
        return sessionRepository.save(new Session()); // todo: use saveAndFlush instead?
    }

    public void endSession(String sessionId) throws IOException
    {
        Optional<Session> session = sessionRepository.findById(sessionId);

        if (session.isEmpty())
            throw new IllegalArgumentException("Session id not found");

        fileService.deleteSessionFiles(session.get().getSessionId());
        sessionRepository.delete(session.get());
    }

    public Session saveSession(Session session)
    {
        return sessionRepository.save(session);
    }

    public Session updateSessionTimestamp(Session session)
    {
        session.setTimestamp(Instant.now());
        return sessionRepository.save(session);
    }

    @Transactional
    public Set<String> deleteOldSessions(Duration timeout) throws IOException
    {
        Set<String> deletedSessions = new HashSet<String>();
        List<Session> sessions = sessionRepository.findAll();
        for(Session session : sessions)
        {
            Instant latestSessionTimestamp = session.getTimestamp();
            if(Instant.now().minus(timeout).isAfter(latestSessionTimestamp))
            {
                fileService.deleteSessionFiles(session.getSessionId());
                sessionRepository.delete(session);
                deletedSessions.add(session.getSessionId());
            }
        }
        return deletedSessions;
    }

    public Set<String> getSessionIds()
    {
        return sessionRepository.findAll().stream().map(Session::getSessionId).collect(Collectors.toSet());
    }
}
