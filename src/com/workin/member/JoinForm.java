package com.workin.member;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;


public class JoinForm extends PageControl{
	MemberMain memberMain;
	
	JLabel la_join;
	JLabel la_name;
	JTextField t_name;
	JLabel la_id;
	JTextField t_id;
	JLabel la_pass;
	JPasswordField t_pass;
	JLabel la_passCheck;
	JPasswordField t_passCheck;
	JButton bt_join;
	
	public JoinForm(MemberMain memberMain) {
		super(memberMain);
		
		//생성
		la_join = new JLabel("회원가입",SwingConstants.CENTER);
		la_name = new JLabel("이름");
		t_name = new JTextField();
		la_id = new JLabel("아이디");
		t_id = new JTextField();
		la_pass = new JLabel("비밀번호");
		t_pass = new JPasswordField();
		la_passCheck = new JLabel("비밀번호 확인");
		t_passCheck = new JPasswordField();
		bt_join = new JButton("회원가입");
		
		//스타일 레이아웃
		la_join.setPreferredSize(new Dimension(380,20));
		la_join.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		la_name.setPreferredSize(new Dimension(380,20));
		t_name.setPreferredSize(new Dimension(380,40));
		la_id.setPreferredSize(new Dimension(380,20));
		t_id.setPreferredSize(new Dimension(380,40));
		la_pass.setPreferredSize(new Dimension(380,20));
		t_pass.setPreferredSize(new Dimension(380,40));
		la_passCheck.setPreferredSize(new Dimension(380,20));
		t_passCheck.setPreferredSize(new Dimension(380,40));
		bt_join.setPreferredSize(new Dimension(380,40));
		bt_join.setBackground(Color.DARK_GRAY);
		bt_join.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		bt_join.setForeground(Color.WHITE);
		bt_join.setBorderPainted(false);
		bt_join.setFocusPainted(false);
		
		//이벤트
		bt_join.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				joinCheck();
			}
		});
		
		//조립
		add(la_join);
		add(la_name);
		add(t_name);
		add(la_id);
		add(t_id);
		add(la_pass);
		add(t_pass);
		add(la_passCheck);
		add(t_passCheck);
		add(bt_join);
		
		//보여주기
		setBackground(Color.WHITE);
	}
	
	//회원등록
	public void regist() {
		String sql = "insert into member(user_name, user_id, user_pass) values(?,?,?)";
		PreparedStatement pstmt=null;
		
		try {
			pstmt=this.getMemberMain().getCon().prepareStatement(sql);

			pstmt.setString(1, t_name.getText());
			pstmt.setString(2, t_id.getText());
			pstmt.setString(3, new String( t_pass.getPassword()));
			
			int result = pstmt.executeUpdate(); 
			if(result==1) {
				JOptionPane.showMessageDialog(this.getMemberMain(), "가입 성공");
			}else {
				JOptionPane.showMessageDialog(this.getMemberMain(), "가입 실패");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			this.getMemberMain().release(pstmt);
		}
	}
	
public void joinCheck() {
		
		if(t_name.getText().equals("")) {
			JOptionPane.showMessageDialog(this,"이름을 입력하세요");
		}else if(t_id.getText().equals("")){
			JOptionPane.showMessageDialog(this,"아이디를 입력하세요");			
		}else if(t_pass.getText().equals("")){
			JOptionPane.showMessageDialog(this,"비밀번호를 입력하세요");			
		}else if(!(t_pass.getText().trim().equals(t_passCheck.getText().trim()))){
			System.out.println(t_pass.getText().trim());
			System.out.println(t_passCheck.getText().trim());
			JOptionPane.showMessageDialog(this,"비밀번호를 확인해주세요");			
		}else {
			regist();
			JoinForm.this.getMemberMain().showHide(0);					 
		}		
	}
}
