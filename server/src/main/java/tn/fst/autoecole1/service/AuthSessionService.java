
package tn.fst.autoecole1.service;

import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthSessionService {

    private final ConcurrentHashMap<String, Integer> sessions =
            new ConcurrentHashMap<>();

    public String createSession(Integer userId) {
        String token = UUID.randomUUID().toString();
        sessions.put(token, userId);
        return token;
    }

    public Integer getUserId(String token) {
        if (token == null || token.isBlank()) {
            return null;
        }
        return sessions.get(token);
    }
}
