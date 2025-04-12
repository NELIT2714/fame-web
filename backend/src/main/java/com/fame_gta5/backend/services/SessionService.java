package pl.oliwawyplywa.web.services;

import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import pl.oliwawyplywa.web.dto.tokens.TokenResponse;
import pl.oliwawyplywa.web.exceptions.HTTPException;
import pl.oliwawyplywa.web.repositories.SessionsRepository;
import pl.oliwawyplywa.web.schemas.Session;
import pl.oliwawyplywa.web.schemas.User;
import pl.oliwawyplywa.web.utils.JwtTokensUtil;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class SessionsService {

    private final SessionsRepository sessionsRepository;
    private final JwtTokensUtil jwtTokensUtil;

    public SessionsService(SessionsRepository sessionsRepository, JwtTokensUtil jwtTokensUtil) {
        this.sessionsRepository = sessionsRepository;
        this.jwtTokensUtil = jwtTokensUtil;
    }

    public Session createSession(User user) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("id_user", user.getIdUser());
        TokenResponse jwtToken = jwtTokensUtil.generateToken(claims);

        Session session = new Session(jwtToken.getToken(), user, jwtToken.getExpiresAt());
        return sessionsRepository.save(session);
    }

    public List<Session> getValidSessionsByUser(User user) {
        return sessionsRepository.findValidSessionsByUser(user.getIdUser());
    }

    public Session getSession(int idSession) {
        return getSessionById(idSession);
    }

    public Session getSession(String jwtToken) {
        return getSessionByToken(jwtToken);
    }

    @Transactional
    public void deleteSession(int idSession) {
        sessionsRepository.delete(getSession(idSession));
    }

    @Transactional
    public void deleteSession(String jwtToken) {
        sessionsRepository.delete(getSession(jwtToken));
    }

    public void deleteSession(Session session) {
        sessionsRepository.delete(session);
    }

    private void deleteInvalidSession(Session session) {
        if (!checkValidSession(session)) {
            deleteSession(session);
            throw new HTTPException(HttpStatus.UNAUTHORIZED, "Session is invalid or expired");
        }
    }

    private Session getSessionById(int idSession) {
        Session session = sessionsRepository.getSessionByIdSession(idSession).orElseThrow(() -> new HTTPException(HttpStatus.NOT_FOUND, "Session not found"));
        deleteInvalidSession(session);
        return session;
    }

    private Session getSessionByToken(String jwtToken) {
        Session session = sessionsRepository.getSessionByToken(jwtToken).orElseThrow(() -> new HTTPException(HttpStatus.NOT_FOUND, "Session not found"));
        deleteInvalidSession(session);
        return session;
    }

    private boolean checkValidSession(Session session) {
        return session.getExpiresAt().isAfter(Instant.now());
    }

}
