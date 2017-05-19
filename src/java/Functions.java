/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import sun.net.smtp.*;

/**
 *
 * @author Nitro
 */
public class Functions {

    private static SecureRandom random = new SecureRandom();

    public static boolean verify(String code) {
        Connection con = null;
        PreparedStatement s1 = null;
        boolean res = false;

        try {

            con = DatabaseManager_azure.getConnection();
            String query1 = "SELECT path   "
                    + " FROM file2 "
                    + " WHERE code = ?";
            s1 = con.prepareStatement(query1);
            s1.setString(1, "" + code);

            ResultSet result = s1.executeQuery();
            if (result.next() == false) {

                res = true;
            } else {
                res = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s1 != null) {
                try {
                    s1.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return res;
        }
    }

    public static boolean register(String code, String path) {
        Connection con = null;
        PreparedStatement s1 = null;
        boolean suc = false;
        try {

            con = DatabaseManager_azure.getConnection();
            String query1 = "INSERT INTO file2   "
                    + " (code , path ) "
                    + " VALUES (?, ?)";
            s1 = con.prepareStatement(query1);
            s1.setString(1, "" + code);
            s1.setString(2, "" + path);
            int result = s1.executeUpdate();
            suc = true;

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s1 != null) {
                try {
                    s1.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return suc;
        }
    }

    public static String getPath(String code) {
        Connection con = null;
        PreparedStatement s1 = null;
        String path = null;

        try {

            con = DatabaseManager_azure.getConnection();
            String query1 = "SELECT path   "
                    + " FROM file2 "
                    + " WHERE code = ?";
            s1 = con.prepareStatement(query1);
            s1.setString(1, "" + code);

            ResultSet result = s1.executeQuery();
            if (result.next()) {
                path = result.getString("path");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s1 != null) {
                try {
                    s1.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return path;
        }
    }

    public static String getLastID() {
        Connection con = null;
        PreparedStatement s1 = null;
        PreparedStatement s2 = null;
        Integer id = 1000000000;

        try {

            con = DatabaseManager_azure.getConnection();
            String query1 = "SELECT last   "
                    + " FROM id "
                    + " WHERE name = ?";
            s1 = con.prepareStatement(query1);
            s1.setString(1, "" + "1");

            ResultSet result = s1.executeQuery();
            if (result.next()) {
                id = result.getInt("last");
                id += 1;
            }
            String query2 = "UPDATE id   "
                    + " SET last = ?"
                    + " WHERE name = 1";
            s2 = con.prepareStatement(query2);
            s2.setString(1, "" + id.toString());

            int result2 = s2.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s1 != null) {
                try {
                    s1.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            String ids = id.toString();
            return ids;
        }
    }

    public static String createSessionId(String ip, String path) {

        String id = new BigInteger(130, random).toString(32);
        while (!Functions.verifyID(id)) {
            id = new BigInteger(130, random).toString(32);
        }
        Connection con = null;
        PreparedStatement s1 = null;
        boolean suc = false;
        String realIP[] = ip.split(":");
        try {

            con = DatabaseManager_azure.getConnection();
            String query1 = "INSERT INTO session   "
                    + " (id , path , ip ) "
                    + " VALUES (? , ? , ?)";
            s1 = con.prepareStatement(query1);
            s1.setString(1, "" + id);
            s1.setString(2, "" + path);
            s1.setString(3, "" + realIP[0]);
            int result = s1.executeUpdate();
            if (result == 1) {
                suc = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s1 != null) {
                try {
                    s1.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (suc) {
                return id;
            } else {
                return null;
            }
        }
    }

    public static String verifySession(String ip, String id) {
        Connection con = null;
        PreparedStatement s1 = null;
        String path = null;
        String[] realIP = ip.split(":");
        try {

            con = DatabaseManager_azure.getConnection();
            String query1 = "SELECT path"
                    + " FROM session "
                    + " WHERE id = ? AND ip = ?";
            s1 = con.prepareStatement(query1);
            s1.setString(1, "" + id);
            s1.setString(2, "" + realIP[0]);

            ResultSet result = s1.executeQuery();
            if (result.next()) {
                path = result.getString("path");
                System.out.println(path);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s1 != null) {
                try {
                    s1.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return path;
        }
    }

    public static boolean verifyID(String id) {
        Connection con = null;
        PreparedStatement s1 = null;
        boolean res = false;

        try {

            con = DatabaseManager_azure.getConnection();
            String query1 = "SELECT path   "
                    + " FROM session "
                    + " WHERE id = ?";
            s1 = con.prepareStatement(query1);
            s1.setString(1, "" + id);

            ResultSet result = s1.executeQuery();
            if (result.next() == false) {
                res = true;
            } else {
                res = false;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s1 != null) {
                try {
                    s1.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return res;
        }
    }

    public static void sendEmail(String email, String fileName, String fileSize, String passcode, String path) {
        Logger logger = Logger.getLogger(
                Functions.class.getName());
        final String username = "Email Address";
        final String password = "Email Password";
        final String host = "smtp.gmail.com";
        Properties props = new Properties();
        /* props.setProperty("mail.transport.protocol", "smtps");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.port", "587");
         */
        props.put("mail.smtp.host", "smtp.gmail.com");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class",
                "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.port", "587");
        Session session = Session.getInstance(props,
                new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(username, password);
            }
        });

        try {
            BodyPart messageBodyPart = new MimeBodyPart();
            Message message = new MimeMessage(session);
            MimeMultipart multipart = new MimeMultipart("related");

            try {
                message.setFrom(new InternetAddress("bot@QuickPass.com", " Quick Pass "));
            } catch (UnsupportedEncodingException ex) {
                Logger.getLogger(Functions.class.getName()).log(Level.SEVERE, null, ex);
            }
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(email));
            message.setSubject("Quick Pass File Upload");
            String html = ("<div> <img src=\"http://i.imgur.com/eIxXOEL.jpg\"> </div> <hr/><h1 style=\"color:rgb(189,203,212)\" >Your file : <span style=\"color:rgb(255,201,14)\">" + fileName + " " + (Double.parseDouble(fileSize) / 1024.0) + "</span> mb , Has been uploaded</h1>"
                    + "\n\n <hr/> \n\n <h2 style=\"color:rgb(189,203,212)\">Your Passcode is <span style=\"color:rgb(255,201,14)\" >" + passcode + "</span></h2>");
            messageBodyPart.setContent(html, "text/html");
            multipart.addBodyPart(messageBodyPart);
            /*messageBodyPart = new MimeBodyPart();
            DataSource fds = new FileDataSource(
                     path);
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID", "<image>");
            multipart.addBodyPart(messageBodyPart);*/
            //message.setContent(multipart);
            message.setContent(multipart);
            Transport.send(message);
            System.out.println("Message has been sent to : " + email);
            
            
        } catch (MessagingException e) {
            System.out.println(e);

        }
    }
    
    public static void cleanSession(){
        Connection con = null;
        PreparedStatement s1 = null;
       
        try {

            con = DatabaseManager_azure.getConnection();
            String query1 = "DELETE FROM session   "
                    + " WHERE date < DATEADD(hh,-1, GETDATE())";
            s1 = con.prepareStatement(query1);
            int result = s1.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (s1 != null) {
                try {
                    s1.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            
        }
    }
    
    public static boolean validateEmail(String email){
        Pattern emailPattern = Pattern.compile("^(([^<>()\\[\\]\\\\.,;:\\s@\"]+(\\.[^<>()\\[\\]\\\\.,;:\\s@\"]+)*)|(\".+\"))@((\\[[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}])|(([a-zA-Z\\-0-9]+\\.)+[a-zA-Z]{2,}))$");
        if(!emailPattern.matcher(email).matches()){
            return false;
        }
        else{
            return true;
        }
    }

}
