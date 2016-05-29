package dao;

/**
 *
 * @author BBEIRIGO
 */
public class FacebookDAOException extends Exception {

    public FacebookDAOException() {
    }

    public FacebookDAOException(String e) {
        super(e);
    }

    public FacebookDAOException(Throwable e) {
        super(e);
    }

    public FacebookDAOException(String e, Throwable c) {
        super(e, c);
    }
}
