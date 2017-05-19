
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.sql.*;

/**
 *
 * @author Nitro
 */
public class DatabaseManager_azure {

    protected static final String USER_NAME = "Your Username";
    protected static final String PASSWORD = "Your Password";
    protected static final String host = "quickpass.database.windows.net";
    protected static final String dbName = "quickpass";
    protected static final String url = String.format("jdbc:sqlserver://%s.database.windows.net:1433;database=%s;user=%s;password=%s;encrypt=true;hostNameInCertificate=*.database.windows.net;loginTimeout=30;", host, dbName, USER_NAME, PASSWORD);
    protected static final String url2 ="jdbc:sqlserver://quickpass.database.windows.net:1433;database=QuickPass;user=nitroman@quickpass;password=Dark@Man123;encrypt=true;trustServerCertificate=false;hostNameInCertificate=*.database.windows.net;loginTimeout=30;";
    public static Connection getConnection() {
        Connection con = null;
        try {
            Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
        } catch (Exception ex) {
            System.out.println("error" + ex);
        }
        try {
            con = DriverManager.getConnection(url2);
            return con;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }
}
