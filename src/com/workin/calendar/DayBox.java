package com.workin.calendar;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

//요일을 표현할 박스
public class DayBox  extends JPanel{
	int width;
	int height;
	String day;
	Color color;
	Color fontC;
	
	public DayBox(String day,Color color, int width, int height,Color fontC) {
		this.width = width;
		this.height = height;
		this.day = day;
		this.color = color;
		this.fontC = fontC;
		setPreferredSize(new Dimension(width,height));
	}
	
	public void paint(Graphics g) {
		//배경색도 여기서 처리
		g.setColor(color); //그래픽 객체의 물감색
		g.fillRect(0, 0, width,height);
		g.setColor(fontC); //그래픽 객체의 물감색 = 검정
		
		g.setFont(new Font("Arial-Black",100,18));
		//g.drawString(day, 10, 30);
		drawCenterString(day, width, height, g);

	}
	
    public void drawCenterString(String s, int w, int h, Graphics g) {
        FontMetrics fm = g.getFontMetrics();
        int x = (w - fm.stringWidth(s)) / 2;
        int y = (fm.getAscent() + (h - (fm.getAscent() + fm.getDescent())) /2);
        g.drawString(s, x, y);
    }
	
	
	
}
