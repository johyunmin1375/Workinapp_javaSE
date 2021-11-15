package com.workin.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

public class OnlineBox2 extends JPanel{
	int width;
	int height;
	String name;
	String id;
	JPanel p_pic;
	JPanel p_detail;
	JPanel p_container;
	JPanel p_name;
	JPanel p_id;
	JLabel la_name;
	JLabel la_id;
	HumanImg hu;
	

	public OnlineBox2(String name, String id) {
		p_container = new JPanel();
		p_pic= new JPanel();
		p_detail = new JPanel();
		hu = new HumanImg(Color.WHITE, 60, 60,  new ImageIcon(getIcon("Human.png")).getImage());
		la_name = new JLabel("이름 : "+ name , 10);
		p_name = new JPanel();
		la_id = new JLabel("ID : "+id, 10);
		p_id = new JPanel();
		
		
		p_pic.setPreferredSize(new Dimension(100, 70));
		p_detail.setPreferredSize(new Dimension(150, 70));
		p_container.setBorder(new LineBorder(Color.BLACK, 1));
		la_name.setHorizontalAlignment(JLabel.LEFT);
		la_name.setOpaque(true);
		la_id.setOpaque(true);
		la_name.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		la_id.setHorizontalAlignment(JLabel.LEFT);
		la_id.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		p_name.setPreferredSize(new Dimension(200, 40));
//		p_name.setBackground(Color.YELLOW);
		p_id.setPreferredSize(new Dimension(200, 40));
//		p_id.setBackground(Color.RED);
		
		

		this.setAlignmentX(CENTER_ALIGNMENT);
		p_detail.setLayout(new GridLayout(2, 1, 0, 0));
		
		p_name.setLayout(new BorderLayout());
		p_id.setLayout(new BorderLayout());
		p_name.add(la_name);
		p_id.add(la_id);
		p_detail.add(p_name);
		p_detail.add(p_id);
		p_pic.add(hu);
		p_container.add(p_pic);
		p_container.add(p_detail, BorderLayout.CENTER);
		add(p_container);
		
		setBackground(Color.WHITE);
		setPreferredSize(new Dimension(300,100));
		
	}
	
	public Image getIcon(String filename) {
		URL url = this.getClass().getClassLoader().getResource(filename);
		System.out.println(url);
		ImageIcon icon=new ImageIcon(url);
		return icon.getImage();
	}
	
}
