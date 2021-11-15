package com.workin.calendar;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.workin.main.AppMain;
import com.workin.member.MemberMain;


//패널위의 라벨을 클릭했을 때 보여짐
public class DetailForm extends JDialog{
	MemberMain memberMain;
	String[] rbb = {"AM","PM"};
	
	JPanel p_whole; //전체를 담을 패널
	
	JPanel p_north1; //제목 올라올 패널
	JLabel l_title;
	JTextField f_title; //제목
	
	JPanel p_north2; //시간 올라올 패널
	JLabel l_date; //날짜
	JTextField f_year;
	JTextField f_month;
	JTextField f_date;
	JLabel l_time; //시간
	JComboBox<String> com_time;
	JTextField f_time2;
	
	JPanel p_center; //내용올라올 패널
	JTextArea content; //내용
	
	JPanel p_south; //버튼 올라올 패널
	JButton bt_modify; //수정
	JButton bt_del; //삭제
	JButton bt_set; //완료
	
	
	String url = "jdbc:mysql://localhost:3306/workinapp?useSSL=false&characterEncoding=UTF-8";
	String user = "root";
	String password = "1234";
	Connection con;
	CalendarMain calendarmain;
	AppMain appMain;
	String choose=null;
	int year=0;
	int month=0;

	
	public DetailForm(JFrame frame, String day,int year, int month,AppMain appMain,CalendarMain calendarMain) {
		super(frame);
		this.calendarmain = calendarMain;
		this.appMain = appMain;
		conn();
		try{ //룩앤필이용해서 UI깔끔하게 
			UIManager.setLookAndFeel ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");//LookAndFeel Windows 스타일 적용
			SwingUtilities.updateComponentTreeUI(this) ;
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, e);
		}
		
		//생성
		p_whole = new JPanel();
		
		p_north1 = new JPanel();
		l_title = new JLabel("제목");
		f_title = new JTextField();
		
		p_north2 = new JPanel();
		l_date = new JLabel("날짜");
		f_year = new JTextField();
		f_month = new JTextField();
		f_date = new JTextField();
		l_time = new JLabel("시간");
		com_time = new JComboBox<String>(rbb);
		f_time2 = new JTextField();
		
		p_center = new JPanel();
		content = new JTextArea(20,52);
		
		p_south = new JPanel();
		bt_modify = new JButton("수정");
		bt_del = new JButton("삭제");
		bt_set = new JButton("완료");
		
		//스타일
		p_whole.setLayout(new GridLayout(2,1));
		f_title.setPreferredSize(new Dimension(320,30));
		l_title.setPreferredSize(new Dimension(80,30));
		l_title.setOpaque(true);
		l_title.setBackground(Color.DARK_GRAY);
		l_title.setForeground(Color.white);
		l_title.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		content.setBorder(BorderFactory.createLineBorder(Color.black));
		com_time.setPreferredSize(new Dimension(50,30));
		f_time2.setPreferredSize(new Dimension(50,30));
		f_year.setPreferredSize(new Dimension(40,30));
		f_month.setPreferredSize(new Dimension(40,30));
		f_date.setPreferredSize(new Dimension(40,30));
		l_date.setPreferredSize(new Dimension(80,30));
		l_time.setPreferredSize(new Dimension(80,30));
		l_date.setOpaque(true);
		l_time.setOpaque(true);
		l_date.setBackground(Color.DARK_GRAY);
		l_time.setBackground(Color.DARK_GRAY);
		l_date.setForeground(Color.white);
		l_time.setForeground(Color.white);
		l_time.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		l_date.setFont(new Font("맑은 고딕", Font.BOLD, 13));
		//combolist();
//		p_whole.setPreferredSize(new Dimension(10,10));
//		p_north1.setPreferredSize(new Dimension(10,10));
//		p_north2.setPreferredSize(new Dimension(10,10));
		p_north1.setOpaque(true);
		p_north1.setBackground(Color.white);
		p_north2.setOpaque(true);
		p_north2.setBackground(Color.white);
		p_center.setOpaque(true);
		p_center.setBackground(Color.white);
		p_south.setOpaque(true);
		p_south.setBackground(Color.white);
		
		
		
		//조립
		p_north1.add(l_title);
		p_north1.add(f_title);
		p_north2.add(l_date);
		p_north2.add(f_year);
		p_north2.add(f_month);
		p_north2.add(f_date);
		p_north2.add(l_time);
		p_north2.add(com_time);
		p_north2.add(f_time2);		
		p_whole.add(p_north1);
		p_whole.add(p_north2);
		
		p_center.add(content);
		
		p_south.add(bt_modify);
		p_south.add(bt_del);
		p_south.add(bt_set);
		
		add(p_whole,BorderLayout.NORTH);
		add(p_center,BorderLayout.CENTER);
		add(p_south,BorderLayout.SOUTH);
		
		//이벤트
		bt_modify.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Check(appMain);
			}
		});
		
		bt_del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delCal();
				setVisible(false);
			}
		});
		bt_set.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		//보여주기
		setSize(520,540);
		setLocationRelativeTo(frame);
		
		choose = day;
		this.year=year;
		this.month = month;
		
		//System.out.println("선택한 날짜"+choose);
	//	System.out.println("선택한 년도"+this.year);
		//System.out.println("선택한 월"+this.month);
		
	combolist();
	}
	
	// DB연결
	public void conn() {
		try {
			Class.forName("com.mysql.jdbc.Driver");//1) 드라이버 로드
			con = DriverManager.getConnection(url,user,password);
			if(con!=null) {
				this.setTitle("접속 성공");
			}else {
				JOptionPane.showMessageDialog(this, "DB에 접속할 수 없습니다");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	//db에서 정보 가져오기 - 제목, 내용, (AM or PM), 시간
	public void combolist() {
		String title =null;
		String contents =null;
		int year =0;
		int month =0;
		int date =Integer.parseInt(this.choose); 
		String cal_date;
		String cal_time;
		
		PreparedStatement pstmt =null;
		ResultSet rs= null;
		
		String sql = "select * from calendar where year="+this.year+" and month = "+(this.month+1)+" and date="+date;
		//System.out.println(sql);
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				title = rs.getString("cal_title");
				year = rs.getInt("year");
				 month = rs.getInt("month");
				 date = rs.getInt("date");
				 contents = rs.getString("cal_content");	
				 cal_date = rs.getString("cal_date");
				 cal_time = rs.getString("cal_time");
				// System.out.println("제목 : "+title + "년도" + year+"월"+month+"일"+date+"내용"+contents+"언제"+cal_date+"몇시"+cal_time);
				 f_title.setText(title);
				 f_year.setText(Integer.toString(year));
				 f_month.setText(Integer.toString(month));
				 f_date.setText(Integer.toString(date));
				 com_time.setSelectedItem(cal_date);
				 f_time2.setText(cal_time);
				 content.setText(contents);		 
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}	finally {
			release(pstmt,rs);
		}
		
		
	}
	
	//수정
	public void modifyCal(AppMain appMain) {
		String title; //제목
		int year; //년도
		int month; //월
		int date; //일
		int app;
		String ap; //오후, 오전
		int time; //시간
		String contents; //내용
		int dates =Integer.parseInt(this.choose);
		int member_id = appMain.getMember().getMember_id();
		
		title = f_title.getText();
		year = Integer.parseInt(f_year.getText());
		month = Integer.parseInt(f_month.getText());
		date = Integer.parseInt(f_date.getText());
		app = com_time.getSelectedIndex();
		ap = rbb[app];
		time = Integer.parseInt(f_time2.getText());
		contents = content.getText();
		
		String ctitle = null;
		int ctime =0;
		String ccontent =null;
		
		
		String sql ="select * from calendar where year="+year+" and month = "+month+" and date="+date+" and member_id ="+member_id;
		PreparedStatement pstmt=null;
	
		ResultSet rs =null;
		try {
				pstmt = con.prepareStatement(sql);
				rs = pstmt.executeQuery();
		
//				while(rs.next()) {
//						ctitle = rs.getString("cal_title");
//						ctime = rs.getInt("cal_time");
//						ccontent = rs.getString("cal_content");
//					}
//				System.out.println("ctitle"+ctitle+", title"+title);
//				System.out.println("sql : "+sql);
				
			
				//System.out.println("주목!!!!");
				//System.out.println("year"+year +"this.year" +this.year); 
			//	System.out.println("month"+month +"this.m" +this.month); 
				//System.out.println("date"+date +"this.d" +this.choose); 
				
				if(rs.isBeforeFirst()) { //조회된 데이터가 없으면 false 있으면 true - 데이터가 있을 때 실행됨
					if(year == this.year && month == (this.month+1) && date == Integer.parseInt(this.choose)) {
						updateData(appMain);
					}else {
						JOptionPane.showMessageDialog(this, "이미 일정이 등록되어있습니다.");								
					}
				}else{
					updateData(appMain);
				}					
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			release(pstmt, rs);
		}
		calendarmain.removeDate(); //날짜 패널 삭제
		calendarmain.createDate(appMain); //날짜 패널 생성
		calendarmain.removeString();//기존 날짜 지우기
		calendarmain.setDateTitle(); //달력 제목 바꾸기 
		calendarmain.printDate(); //날짜 출력하기	
		calendarmain.registDate(appMain); //일정 추가하기	

		this.setVisible(false);
		
	}
	
	public void updateData(AppMain appMain) {
		PreparedStatement pstmt2=null;
		String title; //제목
		int year; //년도
		int month; //월
		int date; //일
		int app;
		String ap; //오후, 오전
		int time; //시간
		String contents; //내용
		int dates =Integer.parseInt(this.choose);
		int member_id = appMain.getMember().getMember_id();
		
		title = f_title.getText();
		year = Integer.parseInt(f_year.getText());
		month = Integer.parseInt(f_month.getText());
		date = Integer.parseInt(f_date.getText());
		app = com_time.getSelectedIndex();
		ap = rbb[app];
		time = Integer.parseInt(f_time2.getText());
		contents = content.getText();
		
		String sql2 = "update calendar set cal_title = "+"'"+title+"'"+", year = "+year+", month = "+month+", date = "+date+", cal_date = "+"'"+ap+"'"+", cal_time = "+time + ",cal_content= "+"'"+contents+"'";
		sql2 +="  where year="+this.year+" and month = "+(this.month+1)+" and date="+dates;
		try {
			pstmt2 = con.prepareStatement(sql2);
			int update_su = pstmt2.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				pstmt2.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		calendarmain.removeDate(); //날짜 패널 삭제
		calendarmain.createDate(appMain); //날짜 패널 생성
		calendarmain.removeString();//기존 날짜 지우기
		calendarmain.setDateTitle(); //달력 제목 바꾸기 
		calendarmain.printDate(); //날짜 출력하기	
		calendarmain.registDate(appMain); //일정 추가하기	
		JOptionPane.showMessageDialog(this, "수정 완료");
	}
	
	
	
	
	
	//삭제
	public void delCal() {
		int date =Integer.parseInt(this.choose); 
		String sql = "delete from calendar where year="+this.year+" and month = "+(this.month+1)+" and date="+date;
		PreparedStatement pstmt =null;
		try {
			pstmt = con.prepareStatement(sql);
			int result2 = JOptionPane.showConfirmDialog(this, "정말로 삭제하시겠습니까?");
			if(result2==JOptionPane.YES_OPTION) { //예 				
				int result = pstmt.executeUpdate();
				if(result==1) {
					JOptionPane.showMessageDialog(this, "삭제성공");
				}else {
					JOptionPane.showMessageDialog(this, "삭제실패");
				}
			}else {
				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			try {
				pstmt.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		calendarmain.removeDate(); //날짜 패널 삭제
		calendarmain.createDate(appMain); //날짜 패널 생성
		calendarmain.removeString();//기존 날짜 지우기
		calendarmain.setDateTitle(); //달력 제목 바꾸기 
		calendarmain.printDate(); //날짜 출력하기	
		calendarmain.registDate(appMain); //일정 추가하기	
	}
	
	public void Check(AppMain appMain) {
		if(f_title.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "제목을 입력해주세요");
			f_title.requestFocus();
		}else if(f_year.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "년도를 입력해주세요");
			f_year.requestFocus();
		}else if(f_month.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "월을 입력해주세요");
			f_month.requestFocus();
		}else if(f_date.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "일을 입력해주세요");
			f_date.requestFocus();
		}else if(f_time2.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "시간을 입력해주세요");
			f_time2.requestFocus();
		}else if(content.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "내용을 입력해주세요");
			content.requestFocus();
		}else if(Integer.parseInt(f_month.getText())>12 || Integer.parseInt(f_month.getText())<1) {
			JOptionPane.showMessageDialog(this, "1월 ~ 12월 중 입력해주세요");
			f_month.setText("");
			f_month.requestFocus();
		}else if(Integer.parseInt(f_date.getText())>31 || Integer.parseInt(f_date.getText())<1) {
			JOptionPane.showMessageDialog(this, "1일 ~ 31일 중 입력해주세요");
			f_date.setText("");
			f_date.requestFocus();
		}else if(Integer.parseInt(f_time2.getText())<0||Integer.parseInt(f_time2.getText())>12) {
			JOptionPane.showMessageDialog(this, "0시 ~ 12시 중 입력해주세요");
			f_time2.setText("");
			f_time2.requestFocus();
		}else {
			modifyCal(appMain);
		}
	}
	
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
	
}
