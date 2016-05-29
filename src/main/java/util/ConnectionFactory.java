package util;

import dao.FacebookDAOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 *
 * @author BBEIRIGO
 */
public class ConnectionFactory {

    public static Connection getConnection(String server, String port, String db, String user, String password) throws FacebookDAOException {
            String url = "jdbc:mysql://"+server+":"+port+"/"+db;
        try {
            //Ajuda a identificar a falta do driver JDBC
            Class.forName("com.mysql.jdbc.Driver");
            return DriverManager.getConnection(url, user, password);
        } catch (Exception e) {
            throw new FacebookDAOException("Impossível conectar-se a url: "+url+"?user="+user+"&password="+password,e);
        }
    }

    public static void closeConnection(Connection conn, Statement strnt, ResultSet rs) throws FacebookDAOException {
        close(conn, strnt, rs);
    }

    public static void closeConnection(Connection conn, Statement strnt) throws FacebookDAOException {
        close(conn, strnt, null);
    }

    public static void closeConnection(Connection conn) throws FacebookDAOException {
        close(conn, null, null);
    }

    private static void close(Connection conn, Statement strnt, ResultSet rs) throws FacebookDAOException {
        try {
            if (rs != null) {
                rs.close();
            }
            if (strnt != null) {
                strnt.close();
            }
            if (conn != null) {
                conn.close();
            }
        } catch (Exception e) {
            throw new FacebookDAOException("Impossível terminar conexão.", e);
        }

    }

}
