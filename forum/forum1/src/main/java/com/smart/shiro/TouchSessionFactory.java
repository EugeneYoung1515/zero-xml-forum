package com.smart.shiro;

import org.apache.shiro.session.Session;
import org.apache.shiro.session.mgt.SessionContext;
import org.apache.shiro.session.mgt.SessionFactory;
import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.SimpleSessionFactory;

public class TouchSessionFactory extends SimpleSessionFactory implements SessionFactory {
    public Session createSession(SessionContext initData) {
        if (initData != null) {
            String host = initData.getHost();
            if (host != null) {
                return new TouchSimpleSession(host);
                //return new TouchSimpleSession2(host);
            }
        }
        return new SimpleSession();
    }
}
