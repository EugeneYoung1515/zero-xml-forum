package com.smart.shiro;

import org.apache.shiro.cache.CacheManagerAware;
import org.apache.shiro.session.ExpiredSessionException;
import org.apache.shiro.session.InvalidSessionException;
import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.DefaultSessionKey;
import org.apache.shiro.session.mgt.SessionKey;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;

import java.util.Collection;

public class TestDefaultWebSessionManager extends DefaultWebSessionManager implements CacheManagerAware{
    @Override
    protected void doValidate(Session session) throws InvalidSessionException {
        //System.out.println(session.getClass());
        super.doValidate(session);
    }

    @Override
    public void validateSessions() {
        int invalidCount = 0;

        System.out.println("validateSessions");
        Collection<Session> activeSessions = getActiveSessions();
        System.out.println(activeSessions);
        if (activeSessions != null && !activeSessions.isEmpty()) {
            for (Session s : activeSessions) {
                try {
                    //simulate a lookup key to satisfy the method signature.
                    //this could probably stand to be cleaned up in future versions:
                    SessionKey key = new DefaultSessionKey(s.getId());
                    validate(s, key);
                } catch (InvalidSessionException e) {
                    e.printStackTrace();
                    invalidCount++;
                }
            }
        }

    }
}
