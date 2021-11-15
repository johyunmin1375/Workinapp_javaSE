package com.workin.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import com.workin.main.AppMain;

//날짜 1개를 표현할 박스
public class DateBox  extends JPanel{
	int width;
	int height;
	String day;
	Color color;
	Color c_date;
	JLabel l_sch;
	String d;
	ScheduleForm sch;
	DetailForm dt;
	CalendarMain calendarmain;
	int year;
	int month;
	String title;
	String cal_date;
	String cal_time;
	int cate;
	//AppMain appMain;

	Color c1 = new Color(136,133,164);
	Color c2 = new Color(128,128,0);
	Color c3 = new Color(255,127,0);
	Color c4 = new Color(97,219,240);
	Color cc;
	
	
	public DateBox(String day,Color color, int width, int height,Color c_date,int year, int month,String title,String cal_date,String cal_time,int cate,Color cc,CalendarMain calendarmain) {
		this.width = width;
		this.height = height;
		this.day = day;
		this.color = color;
		this.c_date = c_date;
		this.year = year;
		this.month = month;
		this.title = title;
		this.cal_date = cal_date;
		this.cal_time = cal_time;
		this.cate = cate;
		this.cc = cc;
		this.calendarmain = calendarmain;
		
		//System.out.println("카테고리 값"+cate);
		setPreferredSize(new Dimension(width,height));	
	}
	
	public void paint(Graphics g) {
		//배경색도 여기서 처리
		g.setColor(color); //그래픽 객체의 물감색
		g.fillRect(0, 0, width,height);
		
		//날짜
		g.setColor(c_date); //그래픽 객체의 물감색 = 검정
		g.setFont(new Font("Arial-Black",100,18));
		g.drawString(day, 10, 30);
		g.setColor(cc);
		g.fillRect(5, 45, 120, 25);
		
		//제목, 시간
		if(cate==1) {
			g.setColor(Color.white);
			g.setFont(new Font("맑은 고딕",Font.PLAIN,15));
			g.drawString(title, 26, 63);
			g.drawString(cal_date, 66, 63);
			g.drawString(cal_time, 91, 63);
		}else if(cate==2) {
//			g.setColor(c2);
//			g.fillRect(5, 45, 120, 25);
			g.setColor(Color.white);
			g.setFont(new Font("맑은 고딕",Font.PLAIN,15));
			g.drawString(title, 22, 63);
			g.drawString(cal_date, 62, 63);
			g.drawString(cal_time, 87, 63);
		}else if(cate==3) {
//			g.setColor(c3);
//			g.fillRect(5, 45, 120, 25);
			g.setColor(Color.white);
			g.setFont(new Font("맑은 고딕",Font.PLAIN,15));
			g.drawString(title, 13, 63);
		}else if(cate==4) {
			//g.setColor(c4);
			//g.fillRect(5, 45, 120, 25);
			g.setColor(Color.white);
			g.setFont(new Font("맑은 고딕",Font.PLAIN,15));
			g.drawString(title,13, 63);
		}else if(cate == 0) {
			
		}
	}
	
	
	public void click(AppMain appMain) {
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				JPanel p = (JPanel)e.getSource();
				p.setBackground(Color.RED);
				
				d=day;
				sch = new ScheduleForm(appMain, "신규 일정 등록",appMain,d,year,month, p,calendarmain);
				dt = new DetailForm(appMain,d,year,month,appMain,calendarmain);
				//System.out.println("day의 값"+day);
				//System.out.println("date에서 회원값"+appMain.getMember().getUser_name());
				
				if(cal_date=="") {
					sch.setVisible(true);
					
					
				}else {
				dt.setVisible(true);					
					
				}			
			}
		});	

	}
}
