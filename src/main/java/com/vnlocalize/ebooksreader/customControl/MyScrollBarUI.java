/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnlocalize.ebooksreader.customControl;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.plaf.basic.BasicScrollBarUI;

/**
 *
 * @author Wise-SW
 */
public class MyScrollBarUI extends BasicScrollBarUI{
    private final Color DEFAULT_TRACK_BG = new Color(160, 160, 160);
    private final Color DEFAULT_THUMB_BG = new Color(80, 80, 80);

    @Override
    protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
        g.setColor(DEFAULT_TRACK_BG);
        g.fillRect(trackBounds.x, trackBounds.y, (trackBounds.x > 0) ? trackBounds.width : 10, (trackBounds.x > 0) ? 10 : trackBounds.height);

        if(trackHighlight == DECREASE_HIGHLIGHT)        {
            paintDecreaseHighlight(g);
        }
        else if(trackHighlight == INCREASE_HIGHLIGHT)           {
            paintIncreaseHighlight(g);
        }
    }
    
    @Override
    protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
        if(thumbBounds.isEmpty() || !scrollbar.isEnabled())     {
            return;
        }

        int w = (thumbBounds.x > 0) ? thumbBounds.width : 10;
        int h = (thumbBounds.x > 0) ? 10 : thumbBounds.height;

        g.translate(thumbBounds.x, thumbBounds.y);
        

        g.setColor(DEFAULT_THUMB_BG);
        g.drawRect(0, 0, w-1, h-1);
        g.setColor(DEFAULT_THUMB_BG);
        g.fillRect(0, 0, w-1, h-1);

        g.setColor(DEFAULT_THUMB_BG);
        g.drawLine(1, 1, 1, h-2);
        g.drawLine(2, 1, w-3, 1);

        g.setColor(DEFAULT_THUMB_BG);
        g.drawLine(2, h-2, w-2, h-2);
        g.drawLine(w-2, 1, w-2, h-3);

        g.translate(-thumbBounds.x, -thumbBounds.y);
    }

    @Override
    protected JButton createDecreaseButton(int orientation) {
        BasicArrowButton btn = new BasicArrowButton(orientation,DEFAULT_TRACK_BG, DEFAULT_TRACK_BG, new Color(0, 0, 0), DEFAULT_TRACK_BG){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(10, 10);
            }
        };
        return btn;
        
    }

    @Override
    protected JButton createIncreaseButton(int orientation) {
        BasicArrowButton btn = new BasicArrowButton(orientation,DEFAULT_TRACK_BG, DEFAULT_TRACK_BG, new Color(0, 0, 0), DEFAULT_TRACK_BG){
            @Override
            public Dimension getPreferredSize() {
                return new Dimension(10, 10);
            }
        };
        return btn;
    }
        
}
