package com.workin.chat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;

import javax.swing.JPanel;

public class ImageBox extends JPanel {
	int width;
	int height;
	Color color; 
	Image img;
	
	public ImageBox(Color color, int width, int height, Image img) {
		this.img=img;
		this.color=color;
		this.width=width;
		this.height=height;
		setPreferredSize(new Dimension(width, height));
	}

	public void paint(Graphics g) {
		g.setColor(color);
		g.fillRect(0, 0, width, height);
		g.drawImage(img, 0, 0, 30, 20, this);
	}
}