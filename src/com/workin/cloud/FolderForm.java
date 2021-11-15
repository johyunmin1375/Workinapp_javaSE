package com.workin.cloud;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

public class FolderForm extends JFrame{
	CloudMain cloudMain;
	JPanel p_center;
	JLabel la_title;
	JTextField t_title;
	JButton bt_create;
	JButton bt_cancel;
	
	public FolderForm(CloudMain cloudMain) {
		this.cloudMain = cloudMain;
		//생성
		p_center = new JPanel();
		la_title = new JLabel(" 폴더명");
		t_title = new JTextField();
		bt_create = new JButton("생성");
		bt_cancel = new JButton("취소");
		//스타일 및 레이아웃
		p_center.setBackground(Color.WHITE);
		la_title.setPreferredSize(new Dimension(100,30));
		la_title.setOpaque(true);
		la_title.setBackground(Color.DARK_GRAY);
		la_title.setForeground(Color.WHITE);
		t_title.setPreferredSize(new Dimension(210,30));
		//조립
		p_center.add(la_title);
		p_center.add(t_title);
		p_center.add(bt_create);
		p_center.add(bt_cancel);
		add(p_center);
		
		//이벤트
		bt_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FolderForm.this.setVisible(false);
			}
		});
		
		bt_create.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				cloudMain.createFolder();
				cloudMain.getFolderList();
				FolderForm.this.setVisible(false);
			}
		});
		//보여주기
		setVisible(true);
		setLocation(880,380);
		setSize(340, 110);
	}
}
