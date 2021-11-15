package com.workin.chat;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.URL;
import java.util.Vector;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;

import com.workin.main.AppMain;
import com.workin.model.domain.Member;

public class BoxCheck extends JFrame{
	BevelBorder border;
	JPanel p_container;
	JPanel p_north;
	JLabel la_north;
	
	
	JPanel p_center;
	JLabel la_online;
	JScrollPane scroll;
	HumanImg humanImg;

	
	OnlineBox2 onlineBox2;
	JPanel p_list;
	
	ChatServer chatServer;
	
	AppMain appMain;
	Vector<Member> memberList;
	int total;
	public BoxCheck() {
		//생성
		p_container = new JPanel();
		p_north = new JPanel();
		la_north = new JLabel("오픈채팅방");
		
		
		p_center = new JPanel();
		la_online= new  JLabel();
		
		p_list = new JPanel();
		scroll = new JScrollPane(p_list);

		//디자인,레이아웃

		la_north.setPreferredSize(new Dimension(80, 35));
		la_north.setHorizontalAlignment(JLabel.CENTER);
		border=new BevelBorder(BevelBorder.RAISED);//3차원적인 테두리 효과를 위한것이고 양각의 옵션을 준다.
		la_north.setBorder(border);//라벨에 적용시킨다.
		
		

		
		p_north.setPreferredSize(new Dimension(300, 40));
		la_online.setFont(new Font("맑은 고딕", Font.BOLD, 22));

		
		p_container.setBackground(Color.WHITE);
		p_list.setBackground(Color.WHITE);
		p_north.setBackground(Color.WHITE);
		p_center.setBackground(Color.WHITE);
		
		
		p_list.setBorder(new EmptyBorder(0,0,0,0));
		scroll.setBorder(new EmptyBorder(0,0,0,0));
		p_center.setPreferredSize(new Dimension(310, 450));
		p_list.setPreferredSize(new Dimension(280, 5000));
		scroll.setPreferredSize(new Dimension(300, 370));
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(20);

		la_north.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				onlineBox2  = new OnlineBox2("테스트", "Test");
				p_list.add(onlineBox2);
				update(getGraphics());
			}
		});

		//조립
		p_container.setLayout(new FlowLayout());
		p_north.add(la_north);
		p_container.add(p_north);
		
		
		
		p_list.setLayout(new FlowLayout());
		p_center.add(la_online);
		p_center.add(scroll);
		p_container.add(p_center);
		
		add(p_container);
		
		//보이기
		setResizable(false);
		setVisible(true);
		setBounds(1270, 320, 330, 500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	};

	public Image getIcon(String filename) {
		URL url = this.getClass().getClassLoader().getResource(filename);
		ImageIcon icon=new ImageIcon(url);
		return icon.getImage();
	}
	
	public static void main(String[] args) {
		new BoxCheck();
	}
}
