package com.ioreo.Utils;

import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class DataBaseX {
    private static String DB_url ;
    private static Connection connection;
    public DataBaseX() throws Exception
    {

        URI dbUri = null;
        Class.forName("org.postgresql.Driver");
        dbUri = new URI(System.getenv("DATABASE_URL"));
        
        String username = dbUri.getUserInfo().split(":")[0];
        String password = dbUri.getUserInfo().split(":")[1];
        DB_url = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath();
        connection = DriverManager.getConnection(DB_url, username, password);
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE IF NOT EXISTS drafts (messageid INTEGER, content TEXT, imgs TEXT, chatid TEXT, mediagroupid TEXT)");
        statement.close();
        connection.setAutoCommit(false);
        connection.commit();
    }
    public boolean insert(String content, String imgs, String chatid, int messageid, String mediagourpid)
    {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO drafts (messageid, content, imgs, chatid, mediagroupid) VALUES ("+messageid+",'"+content+"','"+imgs+"','"+chatid+"','"+mediagourpid+"')");
            statement.close();
            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public boolean delete(int messageid, String mediagroupid)
    {
        try {
            Statement statement = connection.createStatement();
            statement.execute("DELETE FROM drafts WHERE messageid="+messageid+" OR mediagroupid='"+mediagroupid+"'");
            statement.close();
            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<String> get(int messageid, String mediagroupid)
    {
        List<String> list = new ArrayList<String>();
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM drafts WHERE messageid="+messageid+" OR mediagroupid='"+mediagroupid+"'");
            while(resultSet.next())
            {
                list.add(resultSet.getString("content"));
                list.add(resultSet.getString("imgs"));
                list.add(resultSet.getString("chatid"));
                list.add(resultSet.getString("mediagroupid"));
            }
            statement.close();
            connection.commit();
            return list;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public boolean update(String content, String imgs, String chatid, int messageid, String mediagroupid)
    {
        try {
            Statement statement = connection.createStatement();
            statement.execute("UPDATE drafts SET content='"+content+"', imgs='"+imgs+"', chatid='"+chatid+"' WHERE messageid="+messageid+" OR mediagroupid='"+mediagroupid+"'");
            statement.close();
            connection.commit();
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    public void removeTable()
    {
        try {
            Statement statement = connection.createStatement();
            statement.execute("DROP TABLE drafts");
            statement.close();
            connection.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void close()
    {
        try {
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
