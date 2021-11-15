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

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.border.BevelBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.DimensionUIResource;

import com.workin.main.AppMain;
import com.workin.model.domain.Member;


public class ChatSelect extends JFrame{
	BevelBorder border;
	JPanel p_container;
	JPanel p_north;
	JLabel la_north;
	
	JPanel p_center;
	JLabel la_online;
	JScrollPane scroll;
	
	OnlineBox onlineBox;
	Color bg = new Color(118, 247, 115);
	JPanel p_list;
	
	ChatServer chatServer;
	AppMain appMain;
	Vector<Member> memberList;
	int total;
	
	public ChatSelect(AppMain appMain) {
		this.appMain = appMain;
		//생성
		p_container = new JPanel();
		p_north = new JPanel();
		la_north = new JLabel("오픈채팅방 입장");
		
		
		p_center = new JPanel();
		la_online= new  JLabel();
		
		p_list = new JPanel();
		scroll = new JScrollPane(p_list);

		//디자인,레이아웃

		la_north.setPreferredSize(new Dimension(100, 35));
		la_north.setHorizontalAlignment(JLabel.CENTER);
		border=new BevelBorder(BevelBorder.RAISED);//3차원적인 테두리 효과를 위한것이고 양각의 옵션을 준다.
		la_north.setBorder(border);//라벨에 적용시킨다.
		
		

		
		p_north.setPreferredSize(new Dimension(300, 40));
		la_online.setFont(new Font("맑은 고딕", Font.BOLD, 22));

		
		p_container.setBackground(bg);
		p_list.setBackground(bg);
		p_north.setBackground(bg);
		p_center.setBackground(bg);
		
		
		p_list.setBorder(new EmptyBorder(0,0,0,0));
		scroll.setBorder(new EmptyBorder(0,0,0,0));
		p_center.setPreferredSize(new Dimension(310, 450));
		p_list.setPreferredSize(new Dimension(280, 400));
		scroll.setPreferredSize(new Dimension(300, 370));
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		scroll.getVerticalScrollBar().setUnitIncrement(20);


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
		

		//이벤트
		la_north.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				
			}
		});
		
		la_north.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				appMain.chatClient.setVisible(true);
			}
		});
		
		//보이기
		setResizable(false);
		setVisible(false);
		setBounds(1270, 320, 330, 500);
		System.out.println("쳇셀렉 생성");
		
	}
	
	
	public void onlinecount(Vector<Member> memberList) {
		la_online.setText("");
		total=memberList.size();
		System.out.println("온라인인원은?"+total);
		la_online.setText("접속중"+total+"명");

		createBox(memberList);
	}
	
//	public void createBox(Vector<Member> memberList) {
//		for(int i=0;i<10 ;i++) {
//			String name = "테스트이름";
//			String id = "테스트아이디";
//			onlineBox = new OnlineBox(name,id);
//			p_list.add(onlineBox); //센터에 부착!!
//		}
//		p_list.setPreferredSize(new DimensionUIResource(280, total));
//	}
	
	public void createBox(Vector<Member> memberList) {
		p_list.removeAll();
		p_list.setPreferredSize(new Dimension(300, 105*memberList.size()));
		
		for(int i=0;i<memberList.size();i++) {
			String name = memberList.get(i).getUser_name();
			String id = memberList.get(i).getUser_id();
			onlineBox = new OnlineBox(name,id);
			p_list.add(onlineBox); //센터에 부착!!
		}
		
		p_list.updateUI();
	}
	
	public Image getIcon(String filename) {
		URL url = this.getClass().getClassLoader().getResource(filename);
		ImageIcon icon=new ImageIcon(url);
		return icon.getImage();
	}

}
