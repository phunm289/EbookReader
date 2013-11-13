/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnlocalize.ebooksreader.db;

import com.vnlocalize.ebooksreader.eBooks;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Wise-SW
 */
public class Database {

    private Connection conn = null;

    public Database() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void createDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:eBooksReader.db");
            Statement stmt = conn.createStatement();
            String query = "Create Table eBooks("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,"
                    + "name varchar(100) NOT NULL,"
                    + "path varchar(500) NOT NULL,"
                    + "author varchar(50) NOT NULL,"
                    + "addedDate varchar(50) NOT NULL,"
                    + "lastRead varchar(20),"
                    + "markPage varchar(500),"
                    + "readTime int NOT NULL);";
            stmt.executeUpdate(query);
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    try {
                        conn.close();
                    } catch (SQLException ex) {
                        Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public ResultSet getAllBook() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:eBooksReader.db");
        String query = "Select * FROM eBooks";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs;
    }

    public ResultSet getBook(int bid) throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:eBooksReader.db");
        String query = "Select * FROM eBooks WHERE id = " + bid;
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs;
    }

    public ResultSet getRecentAdd() throws SQLException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = new GregorianCalendar();
        Date today = new Date();
        cal.setTime(today);
        cal.add(Calendar.DATE, -1);
        Date dayBefore = cal.getTime();
        conn = DriverManager.getConnection("jdbc:sqlite:eBooksReader.db");
        String query = "Select * FROM eBooks WHERE addedDate > '" + dateFormat.format(dayBefore) + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs;

    }

    public ResultSet getRecentRead() throws SQLException {
        DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        Calendar cal = new GregorianCalendar();
        Date today = new Date();
        cal.setTime(today);
        cal.add(Calendar.DATE, -1);
        Date dayBefore = cal.getTime();
        conn = DriverManager.getConnection("jdbc:sqlite:eBooksReader.db");
        String query = "Select * FROM eBooks WHERE lastRead > '" + dateFormat.format(dayBefore) + "'";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs;

    }

    public ResultSet getUnreadBook() throws SQLException {
        conn = DriverManager.getConnection("jdbc:sqlite:eBooksReader.db");
        String query = "Select * FROM eBooks WHERE readTime = 0";
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        return rs;
    }

    public void insertBook(eBooks book) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:eBooksReader.db");
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM eBooks WHERE path = '" + book.getBookPath() + "'";
            if (!stmt.executeQuery(query).next()) {
                query = "Insert Into eBooks(name,path,author,addedDate,readTime) "
                        + "Values('" + book.getBookName() + "','" + book.getBookPath() + "','" + book.getAuthor() + "','" + book.getAddedDate() + "','" + book.getReadTime() + "');";
                stmt.executeUpdate(query);
            }
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {
                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void removeBook(int bid) {
        try {
            if (conn != null && !conn.isClosed()) {
                conn.close();
            }
            conn = DriverManager.getConnection("jdbc:sqlite:eBooksReader.db");
            Statement stmt = conn.createStatement();
            String query = "SELECT * FROM eBooks WHERE id = '" + bid + "'";
            if (stmt.executeQuery(query).next()) {
                query = "DELETE From eBooks WHERE id = '" + bid + "'";
                stmt.executeUpdate(query);
            }
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {

                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void modifiBook(int bid, String lastRead) {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite:eBooksReader.db");
            String query = "Select * FROM eBooks WHERE id = " + bid;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                query = "UPDATE eBooks "
                        + "SET lastRead = '" + lastRead + "', readTime = readTime + 1 WHERE id = " + rs.getInt("id");
                stmt.executeUpdate(query);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {

                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public void setMarkPage(String currentPage, int bid) {
        try {
            try {
                if (conn != null && !conn.isClosed()) {

                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
            conn = DriverManager.getConnection("jdbc:sqlite:eBooksReader.db");
            String query = "Select * FROM eBooks WHERE id = " + bid;
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            while (rs.next()) {
                query = "UPDATE eBooks "
                        + "SET markPage = '"+currentPage+"' WHERE id = " + rs.getInt("id");
                stmt.executeUpdate(query);
            }
            rs.close();
            stmt.close();
        } catch (SQLException ex) {
            Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            try {
                if (conn != null && !conn.isClosed()) {

                    conn.close();
                }
            } catch (SQLException ex) {
                Logger.getLogger(Database.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
