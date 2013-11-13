/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnlocalize.ebooksreader.customControl;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MultipleGradientPaint;
import java.awt.Paint;
import java.awt.RadialGradientPaint;
import javax.swing.Icon;

/**
 *
 * @author Wise-SW
 */
public class SpecialThumbIcon implements Icon {  
  
  private static final float[] fractions = {0.1f, 0.35f};  
  private static final Color[] colors = {  
    new Color(74,74,74),  
    new Color(74,74,74)  
  };  
  private static final Paint thumbGradient = new RadialGradientPaint(5, 3, 15,  
          fractions, colors, MultipleGradientPaint.CycleMethod.NO_CYCLE);  
  
  @Override  
  public void paintIcon(Component c, Graphics g, int x, int y) {  
    Graphics2D g2 = (Graphics2D) g;  
    g2.setPaint(thumbGradient);  
    g.fillOval(x + 3, y + 3, 14, 14);  
  }  
  
  @Override  
  public int getIconWidth() {  
    return 20;  
  }  
  
  @Override  
  public int getIconHeight() {  
    return 20;  
  }  
}
