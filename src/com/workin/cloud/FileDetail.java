package com.workin.cloud;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class FileDetail extends JFrame{
	CustomFile customFile;
	
	JPanel p_center;
	JLabel la_file;
	JLabel t_file;
	JLabel la_writer;
	JLabel t_writer;
	JLabel la_regdate;
	JLabel t_regdate;
	JButton bt_file, bt_del; 
	
	public FileDetail(CustomFile customFile) {
		this.customFile = customFile;
		String name = customFile.getFile_name();
		String regdate = customFile.getRegdate();
		String writer = customFile.getWriter();
		
		//생성
		p_center = new JPanel();
		la_file = new JLabel(" 파일명");
		t_file = new JLabel(name);
		la_writer = new JLabel(" 작성자");
		t_writer = new JLabel(writer);
		la_regdate = new JLabel(" 생성일");
		t_regdate = new JLabel(regdate);
		bt_file = new JButton("다운로드");
		bt_del = new JButton("삭제");
		
		//스타일 및 레이아웃
		p_center.setPreferredSize(new Dimension(340,320));
		p_center.setBackground(Color.WHITE);
		la_file.setPreferredSize(new Dimension(100,30));
		la_file.setOpaque(true);
		la_file.setBackground(Color.DARK_GRAY);
		la_file.setForeground(Color.WHITE);
		t_file.setPreferredSize(new Dimension(210,30));
		t_file.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		la_writer.setPreferredSize(new Dimension(100,30));
		la_writer.setOpaque(true);
		la_writer.setBackground(Color.DARK_GRAY);
		la_writer.setForeground(Color.WHITE);
		t_writer.setPreferredSize(new Dimension(210,30));
		t_writer.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		la_regdate.setPreferredSize(new Dimension(100,30));
		la_regdate.setOpaque(true);
		la_regdate.setBackground(Color.DARK_GRAY);
		la_regdate.setForeground(Color.WHITE);
		t_regdate.setPreferredSize(new Dimension(210,30));
		t_regdate.setBorder(BorderFactory.createLineBorder(Color.GRAY));
		
		//조립
		p_center.add(la_file);
		p_center.add(t_file);
		p_center.add(la_writer);
		p_center.add(t_writer);
		p_center.add(la_regdate);
		p_center.add(t_regdate);
		p_center.add(bt_file);
		p_center.add(bt_del);
		add(p_center);
		
		//이벤트
		bt_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FileDetail.this.customFile.downLoadFile();
				FileDetail.this.setVisible(false);
			}
		});
		
		//보여주기
		setVisible(true);
		setLocationRelativeTo(customFile);
		setSize(340, 180);
	}
}
