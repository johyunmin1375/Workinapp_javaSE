package com.workin.member;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import com.workin.main.AppMain;
import com.workin.main.CustomButton;

public class MemberMain extends JFrame implements ActionListener{
	AppMain appMain;
	
	
	JPanel p_back;
	JPanel p_form;
	JLabel la_title;
	JPanel p_center;
	//JButton bt_join;
	
	String[] menu_title= {"로그인","회원가입"};
	CustomButton[] bt_menu=new CustomButton[menu_title.length]; //배열생성
	//페이지 선언
	PageControl[] pages = new PageControl[2];
	
	//db
	private Connection con;
	String url = "jdbc:mysql://localhost:3306/workinapp?characterEncoding=UTF-8";
	String user = "root";
	String password = "1234";
	
	public MemberMain() {
		connect();
		//생성
		p_back = new JPanel();
		p_form = new JPanel();
		la_title = new JLabel("Work IN");
		p_center = new JPanel();
		//bt_join = new JButton("회원가입");

		
		//스타일 및 레이아웃
		p_back.setBackground(new Color(34, 45, 50));
		p_form.setPreferredSize(new Dimension(420,720));
		p_form.setBackground(new Color(34, 45, 50));
		la_title.setPreferredSize(new Dimension(420,80));
		la_title.setForeground(Color.WHITE);
		la_title.setFont(new Font("맑은 고딕", Font.BOLD, 28));
		la_title.setHorizontalAlignment(SwingConstants.CENTER);
		//페이지 생성
		p_center.setPreferredSize(new Dimension(420,420));
		p_center.setBackground(Color.WHITE);
		
		pages[0] = new LoginForm(this);
		pages[1] = new JoinForm(this);
		
		for(int i=0; i<menu_title.length;i++) {
			bt_menu[i] = new CustomButton(menu_title[i]);
			bt_menu[i].setId(i);
			bt_menu[i].setForeground(Color.WHITE);
			bt_menu[i].setFont(new Font("맑은 고딕", Font.BOLD, 16));
			bt_menu[i].setBorderPainted(false);
			bt_menu[i].setFocusPainted(false);		
			bt_menu[i].setBackground(new Color(34, 45, 50));
		}
		
		
		
		//조립
		p_form.add(la_title);
		p_form.add(p_center);
		for(JButton bt : bt_menu) { // improved for loop : 주로 집합데이터 형식을 대상으로 한 loop
			p_form.add(bt);
		}
		//p_form.add(bt_menu[1]);
		p_back.add(p_form);
		for(PageControl p : pages) {
			p_center.add(p);
		}
		add(p_back);
		
		
		//이벤트
		for(int i=0;i<bt_menu.length;i++) {
			bt_menu[i].addActionListener(this);
		}		
		
		//보여주기
		setBounds(650, 100, 520, 640);
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	public void actionPerformed(ActionEvent e) {
		//어떤 버튼이 눌렸는지? - 이벤트가 연결된 컴포넌트를 가리켜 이벤트 소스 
		Object obj = e.getSource();
		//obj는 오브젝트 자료형이기 때문에, 버튼을 가리킬수는 있지만, 버튼 보다는 보편적인 기능만을 가지고 
		//있기에, 즉 가진게 별로 없기에 버튼의 특징을 이용하기 위해서는 버튼 형으로 변환해서 사용하자!!
		CustomButton bt=(CustomButton)obj; //down casting
		showHide(bt.getId());
	}
	
	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver"); //드라이버 로드 
			con = DriverManager.getConnection(url, user, password);
			if(con !=null) {
				this.setTitle("접속 성공");
			}else {
				this.setTitle("접속 실패");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		} 
		
	}
	
	public void disConnect() {
		if(con!=null) {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	//쿼리문이 DML
	public void release(PreparedStatement pstmt) {
		if(pstmt !=null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}

	//쿼리문이 select인 경우
	public void release(PreparedStatement pstmt, ResultSet rs) {
		if(rs !=null) {
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		
		if(pstmt !=null) {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	public Connection getCon() {
		return con;
	}
	
	public void showHide(int n) { //보여주고 싶은 페이지의 번호를 넘기면 된다..
		//내가 누른 버튼에 해당하는 페이지만 setVisible() 을 true로 놓고 나머지는 false로 놓자!!
		for(int i=0;i<pages.length;i++) {
			if(n==i) {
				pages[i].setVisible(true); //현재 선택한 버튼과 같은 인덱스를 갖는 페이지라면..
			}else {
				pages[i].setVisible(false);
			}
		}
	}
	
	public static void main(String[] args) {
		new MemberMain();
	}
}
