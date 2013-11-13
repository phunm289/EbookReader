/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnlocalize.ebooksreader.customControl;

import javax.swing.Icon;
import javax.swing.JLabel;

/**
 *
 * @author Wise-SW
 */
public class MyLabel extends JLabel{
    private int id;
    
    public MyLabel() {
    }

    public MyLabel(Icon image) {
        super(image);
    }

    public MyLabel(String text) {
        super(text);
    }

    public MyLabel(Icon image, int horizontalAlignment) {
        super(image, horizontalAlignment);
    }

    public MyLabel(String text, int horizontalAlignment) {
        super(text, horizontalAlignment);
    }

    public MyLabel(String text, Icon icon, int horizontalAlignment) {
        super(text, icon, horizontalAlignment);
    }
    
    public int getId(){
        return id;
    }
    public void setId(int id){
        this.id = id;
    }
}
