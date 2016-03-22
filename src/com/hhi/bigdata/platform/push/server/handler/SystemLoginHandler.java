package com.hhi.bigdata.platform.push.server.handler;

/**
 * Created by SongChanHo on 2016. 3. 18..
 */
import asia.stampy.common.StampyLibrary;
import asia.stampy.server.listener.login.NotLoggedInException;
import asia.stampy.server.listener.login.StampyLoginHandler;
import asia.stampy.server.listener.login.TerminateSessionException;

/**
 * The Class SystemLoginHandler.
 */
@StampyLibrary(libraryName = "stampy-examples")
public class SystemLoginHandler implements StampyLoginHandler {

    /**
     * The Constant SEE_THE_SYSTEM_ADMINISTRATOR.
     */
    public static final String SEE_THE_SYSTEM_ADMINISTRATOR = "See the system administrator";

    /**
     * The Constant GOOD_USER.
     */
    public static final String GOOD_USER = "gooduser";

    /**
     * The Constant BAD_USER.
     */
    public static final String BAD_USER = "baduser";

    private int maxFailedLoginAttempts = 3;

    private int failedLoginAttempts = 0;

    /*
     * (non-Javadoc)
     *
     * @see
     * asia.stampy.server.mina.login.StampyLoginHandler#login(java.lang.String,
     * java.lang.String)
     */
    @Override
    public void login(String username, String password) throws NotLoggedInException, TerminateSessionException {
        if (GOOD_USER.equals(username)) return;

        failedLoginAttempts++;

        if (failedLoginAttempts >= getMaxFailedLoginAttempts()) {
            throw new TerminateSessionException(SEE_THE_SYSTEM_ADMINISTRATOR);
        }

        throw new NotLoggedInException("Username " + username + " cannot be logged in");
    }

    /**
     * Gets the max failed login attempts.
     *
     * @return the max failed login attempts
     */
    public int getMaxFailedLoginAttempts() {
        return maxFailedLoginAttempts;
    }

    /**
     * Sets the max failed login attempts.
     *
     * @param maxFailedLoginAttempts the new max failed login attempts
     */
    public void setMaxFailedLoginAttempts(int maxFailedLoginAttempts) {
        this.maxFailedLoginAttempts = maxFailedLoginAttempts;
    }
}