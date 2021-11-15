package com.workin.member;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import com.workin.main.AppMain;
import com.workin.model.domain.Member;

public class LoginForm extends PageControl{
	MemberMain memberMain;
	
	Boolean f = false;
	JLabel la_icon;
	JLabel la_id;
	JTextField t_id;
	JLabel la_pass;
	JPasswordField t_pass;
	JButton bt_login;
		
	Image img;
	Member member;
	
	public LoginForm(MemberMain memberMain) {
		super(memberMain);
		//생성
		la_icon = new JLabel("로그인",SwingConstants.CENTER);
		la_id = new JLabel("아이디",SwingConstants.LEFT);
		t_id = new JTextField("admin");
		la_pass = new JLabel("비밀번호",SwingConstants.LEFT);
		t_pass = new JPasswordField("1234");
		bt_login = new JButton("로그인");
		
		//스타일 레이아웃
		la_icon.setPreferredSize(new Dimension(120,100));
		la_icon.setFont(new Font("맑은 고딕", Font.BOLD, 24));
		la_id.setPreferredSize(new Dimension(380,20));
		t_id.setPreferredSize(new Dimension(380,50));
		la_pass.setPreferredSize(new Dimension(380,20));
		t_pass.setPreferredSize(new Dimension(380,50));
		bt_login.setPreferredSize(new Dimension(380,50));
		bt_login.setBackground(Color.DARK_GRAY);
		bt_login.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		bt_login.setForeground(Color.WHITE);
		bt_login.setBorderPainted(false);
		bt_login.setFocusPainted(false);
		
		//이벤트
		bt_login.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				loginCheck();
			}
		});
		
		//조립
		add(la_icon);
		add(la_id);
		add(t_id);
		add(la_pass);
		add(t_pass);
		add(bt_login);
		
		//보여주기
		setBackground(Color.WHITE);
		setVisible(true);
	}
	
	public void loginCheck() {
		String sql = "select * from member where user_id=? and user_pass=?";
		
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		Member member=null;
		
		try {
			pstmt=this.getMemberMain().getCon().prepareStatement(sql);
			pstmt.setString(1, t_id.getText());
			pstmt.setString(2, new String(t_pass.getPassword()));
			rs=pstmt.executeQuery();
			
			//회원인지 아닌지?
			if(rs.next()) {
				member = new Member();
				member.setMember_id(rs.getInt("member_id"));
				member.setUser_name(rs.getString("user_name"));
				member.setUser_id(rs.getString("user_id"));
				member.setUser_pass(rs.getString("user_pass"));
				member.setRegdate(rs.getString("regdate"));
				member.setImg(rs.getString("img"));
				JOptionPane.showMessageDialog(this, member.getUser_name()+"님 환영합니다.");
				//this.setVisible(false);
				new AppMain(member, memberMain);
				this.getMemberMain().setVisible(false);
			}else {
				JOptionPane.showMessageDialog(this, "로그인 정보가 올바르지 않습니다.");				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			this.getMemberMain().release(pstmt, rs);
		}
		
	}
	
//	//아이콘 올려놓기
//	@Override
//	public void paintComponent(Graphics g) {
//		URL url = this.getClass().getClassLoader().getResource("profile.png");
//		ImageIcon icon = new ImageIcon(url);
//		img = icon.getImage();
//		g.drawImage(img, 165, 10, 80, 80, la_icon);
//	}
}
