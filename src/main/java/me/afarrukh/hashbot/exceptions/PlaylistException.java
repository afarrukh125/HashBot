package me.afarrukh.hashbot.exceptions;

/**
 * @author Abdullah
 * Created on 10/09/2019 at 21:38
 */
public class PlaylistException extends Exception {

    public PlaylistException(String message) {
        super(message);
    }

    public PlaylistException() {
        super();
    }

    public PlaylistException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlaylistException(Throwable cause) {
        super(cause);
    }

    protected PlaylistException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
