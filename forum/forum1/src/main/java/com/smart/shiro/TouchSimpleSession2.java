package com.smart.shiro;

import org.apache.shiro.session.mgt.SimpleSession;
import org.apache.shiro.session.mgt.ValidatingSession;

import java.io.Serializable;
import java.util.Date;

public class TouchSimpleSession2 extends SimpleSession implements ValidatingSession, Serializable {
    private boolean isChanged = true;

    private Date lastTouchTime;

    public TouchSimpleSession2() {
    }

    public TouchSimpleSession2(String host) {
        super(host);
    }

    @Override
    public void setLastAccessTime(Date lastAccessTime) {
        lastTouchTime = getLastAccessTime();
        super.setLastAccessTime(lastAccessTime);
        isChanged = false;
    }

    @Override
    public void touch() {
        lastTouchTime = getLastAccessTime();
        super.touch();
        isChanged = false;
    }

    public boolean isChanged() {
        return isChanged;
    }

    public void setChanged(boolean isChanged) {
        this.isChanged = isChanged;
    }

    public Date getLastTouchTime() {
        return lastTouchTime;
    }

    public void setLastTouchTime(Date lastTouchTime) {
        this.lastTouchTime = lastTouchTime;
    }

}
