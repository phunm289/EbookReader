/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnlocalize.ebooksreader;

/**
 *
 * @author Wise-SW
 */
public class Item {
    private String href,id;

    public Item(String href, String id) {
        this.href = href;
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
    
}
