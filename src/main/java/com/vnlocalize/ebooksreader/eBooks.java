/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnlocalize.ebooksreader;

/**
 *
 * @author Wise-SW
 */
public class eBooks {
    private String bookName,bookPath,author,lastRead,addedDate;
    private int readTime,bid;

    public eBooks() {
    }

    public int getBid() {
        return bid;
    }

    public void setBid(int bid) {
        this.bid = bid;
    }

    public String getBookName() {
        return bookName;
    }

    public void setBookName(String bookName) {
        this.bookName = bookName;
    }

    public String getBookPath() {
        return bookPath;
    }

    public void setBookPath(String bookPath) {
        this.bookPath = bookPath;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public eBooks(String bookName, String bookPath, String author, String addDate) {
        this.bookName = bookName;
        this.bookPath = bookPath;
        this.author = author;
        this.addedDate = addDate;
    }

    public int getReadTime() {
        return readTime;
    }

    public String getLastRead() {
        return lastRead;
    }

    public void setLastRead(String lastRead) {
        this.lastRead = lastRead;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addDate) {
        this.addedDate = addDate;
    }

    public void setReadTime(int readTime) {
        this.readTime = readTime;
    }
    
    
    
}
