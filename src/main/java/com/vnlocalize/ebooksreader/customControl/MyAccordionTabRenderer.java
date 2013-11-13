/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vnlocalize.ebooksreader.customControl;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.JLabel;

import com.javaswingcomponents.accordion.JSCAccordion;
import com.javaswingcomponents.accordion.tabrenderer.AccordionTabRenderer;
import com.javaswingcomponents.accordion.tabrenderer.GetTabComponentParameter;
import com.javaswingcomponents.framework.painters.configurationbound.LinearGradientColorPainter;
import javax.swing.ImageIcon;
import javax.swing.JPanel;

public class MyAccordionTabRenderer extends JLabel implements AccordionTabRenderer {

	private JSCAccordion accordion;
	private ImageIcon icon = new ImageIcon(getClass().getResource("/ebooksreader/LastRead_s.png"));
        
	public MyAccordionTabRenderer() {
            setOpaque(false);
	}
	
	@Override
	public JComponent getTabComponent(GetTabComponentParameter parameters) {
		//read the tabText from the parameter
		setText(parameters.tabText);
                setIcon(icon);
                setOpaque(false);
		//set the text color to white
		setForeground(Color.WHITE);
		//use a slightly smaller bold font
		setFont(getFont().deriveFont(Font.BOLD, 14));
		//create a border to help align the label
		setBorder(BorderFactory.createEmptyBorder(0,8,0,0));
		//set the gap between the icon and the text to 8 pixels
		setIconTextGap(8);
		
		//set the appropriate image based on the tabText.
		
		//returns itself, which extends JLabel
		return this;
	}

	private LinearGradientColorPainter painter = new LinearGradientColorPainter();
	
	@Override
	protected void paintComponent(Graphics g) {
		painter.setColorFractions(new float[]{
				0.0f, 0.49f, 0.5f, 0.51f});
		painter.setColors(new Color[]{
				new Color(200,200,200),new Color(100,100,100),
				new Color(200,200,200),new Color(100,100,100)});
		painter.paint((Graphics2D) g, new Rectangle(0, 0, getWidth(), getHeight()));
		
		Color originalColor = g.getColor();
		
		g.setColor(new Color(100,100,100));
		g.drawLine(0, getHeight()-1, getWidth(), getHeight()-1);
		
		g.setColor(originalColor);
		super.paintComponent(g);			
	}
	
	@Override
	public void setAccordion(JSCAccordion accordion) {
		this.accordion = accordion;
	}
}