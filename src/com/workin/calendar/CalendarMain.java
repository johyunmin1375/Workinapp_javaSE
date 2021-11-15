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
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;


import com.workin.main.AppMain;
import com.workin.main.Page;
import com.workin.member.MemberMain;

public class CalendarMain extends Page{
	MemberMain memberMain;
	JPanel pp;
	JPanel p_hnorth;
	JButton bt_today;
	JLabel la_today;
	JPanel p_north;
	JButton bt_prev_y;
	JButton bt_prev;
	JLabel la_title;
	JButton bt_next;
	JButton bt_next_y;
	
	JPanel p_center; //날짜 박스 처리할 영역
	String[] dayArray= {"SUN","MON","TUE","WED","THU","FRI","SAT"};
	//ScheduleForm form;
	
	//원하신 시점에 날짜 박스를 제어하기 위해서, 각 날짜 박스객체들을 배열에 담아놓자!!
	DateBox[] boxArray=new DateBox[dayArray.length*6];
	Calendar currentDate; //다음달, 이전달로 이동할 때 사용
	Calendar today = Calendar.getInstance();
	

	int yy_t = today.get(Calendar.YEAR); //현재 연도 
	int mm_t = today.get(Calendar.MONTH); //현재 월 
	int dd_t = today.get(Calendar.DATE); //현재 일
	
	int calDates[] = new int[boxArray.length];
	int category; //카테고리
	String cate;
	
	String url="jdbc:mysql://localhost:3306/workinapp?useSSL=false&characterEncoding=UTF-8";
	String user="root";
	String password="1234";
	Connection  con;
	
	
	public CalendarMain(AppMain appMain) {
		
		super(appMain);
		
		conn();
		//룩앤필이용해서 UI깔끔하게 
		try{
			UIManager.setLookAndFeel ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");//LookAndFeel Windows 스타일 적용
			SwingUtilities.updateComponentTreeUI(this) ;
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, e);
		}
		
		pp = new JPanel();
		p_hnorth= new JPanel();
		bt_today = new JButton("Today");
		la_today = new JLabel();
		p_north = new JPanel();
		bt_prev = new JButton("<");
		bt_prev_y = new JButton("<<");		
		la_title = new JLabel("연도 월 올 예정", SwingConstants.CENTER);
		bt_next = new JButton(">");
		bt_next_y = new JButton(">>");
		p_center = new JPanel();
//		form = new ScheduleForm(appMain, "신규 일정 등록",appMain);
		
		//스타일
		la_today.setFont(new Font("Arial-Black",Font.PLAIN,15));
		la_title.setFont(new Font("Arial-Black", Font.BOLD, 22));
		la_title.setPreferredSize(new Dimension(400, 30));
		pp.setLayout(new GridLayout(2,1));
	
		
		setLayout(new BorderLayout());
		
		//조립 
		p_hnorth.add(bt_today);
		p_hnorth.add(la_today);
		p_north.add(bt_prev_y);
		p_north.add(bt_prev);
		p_north.add(la_title);
		p_north.add(bt_next);
		p_north.add(bt_next_y);		
		
		pp.add(p_hnorth);
		pp.add(p_north);
	
		add(pp,BorderLayout.NORTH);
		add(p_center);
		
		
		
		getCurrentDate(); //현재날짜 객체 구하기
		setDateTitle();//달력 제목 달기
		createDay(); //요일생성
		createDate(appMain);//날짜생성
		printDate();//각 박스에 날짜 출력
		registDate(appMain);
		
		
		//이벤트 
		bt_prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prevMonth();
				removeDate();
				createDate(appMain);
				removeString();//기존 날짜 지우기
				setDateTitle(); //달력 제목 바꾸기 
				printDate(); //날짜 출력하기	
				registDate(appMain);
			}
		});
		
		bt_next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextMonth();
				removeDate();
				createDate(appMain);
				removeString();//기존 날짜 지우기
				setDateTitle(); //달력 제목 바꾸기 
				printDate(); //날짜 출력하기	
				registDate(appMain);
			}
		});
		bt_prev_y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				prevYear();
				removeDate();
				createDate(appMain);
				removeString();//기존 날짜 지우기
				setDateTitle(); //달력 제목 바꾸기 
				printDate(); //날짜 출력하기	
				registDate(appMain);
			}
		});
		bt_next_y.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				nextYear();
				removeDate();
				createDate(appMain);
				removeString();//기존 날짜 지우기
				setDateTitle(); //달력 제목 바꾸기 
				printDate(); //날짜 출력하기	
				registDate(appMain);
			}
		});
		bt_today.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				moveToday();
				removeDate();
				createDate(appMain);
				removeString();//기존 날짜 지우기
				setDateTitle(); //달력 제목 바꾸기 
				printDate(); //날짜 출력하기	
				registDate(appMain);
			}
		});

	}
	
	// DB연결
		public void conn() {
			try {
				Class.forName("com.mysql.jdbc.Driver");//1) 드라이버 로드
				con = DriverManager.getConnection(url,user,password);
				if(con!=null) {
					System.out.println("캘린더 DB 접속성공");
				}else {
					JOptionPane.showMessageDialog(this, "DB에 접속할 수 없습니다");
				}
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} catch (SQLException e) {
				e.printStackTrace();
			}	
		}
		
	
	//현재날짜 구하기(프로그램 가동과 동시에 사용될 디폴트 날짜 객체) 
	public Calendar getCurrentDate() {
		currentDate = Calendar.getInstance();
		
		return currentDate;
	}
	
	//달력의 제목 즉, 날짜 출력 
	public void setDateTitle() {
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);

		//제목에 출력
		la_title.setText(yy+"-"+getZeroString(mm+1));
		
		la_today.setText(yy_t+"/"+getZeroString(mm_t+1)+"/"+getZeroString(dd_t));
	}
	
	//요일패널 생성 
	public void createDay() {
		DayBox dayBox;
		for(int i=0;i<7;i++) {
			if(i==0) {
				dayBox=new DayBox(dayArray[i],Color.white,130,28,Color.red);						
			}else if(i==6) {
				dayBox=new DayBox(dayArray[i],Color.white,130,28,Color.blue);
			}else {
				 dayBox=new DayBox(dayArray[i],Color.white,130,28,Color.black);
			}
			dayBox.setFont(new Font("Arial-Black",100,18));
			p_center.add(dayBox); //센터에 부착!!		
		}
	}
	
	//날짜패널  생성 
	public void createDate(AppMain appMain) {
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		//int dd = currentDate.get(Calendar.DATE);
		int firstday = getFirstDayOfMonth(yy, mm);
		DateBox dateBox;

		for(int i=0;i<dayArray.length*6;i++) {
			if(i<firstday|| i-(firstday-1)>getLastDate(yy, (mm+1))) { 
				dateBox = new DateBox("", new Color(255,0,0,0), 130,79,Color.black,yy,mm,"","","",category,new Color(255,0,0,0),this); //일 수 외에는 투명하게 배경 처리하기
			}else if(yy==yy_t&&mm==mm_t&&i==dd_t+1) { //오늘 표시
				dateBox = new DateBox("", Color.pink, 130,79,Color.black,yy,mm,"","","",category,new Color(255,0,0,0),this);
				dateBox.click(appMain);
				
			}else if(i==0 || i==7 || i==14 || i==21 || i==28){
				dateBox = new DateBox("", Color.white, 130,79,Color.red,yy,mm,"","","",category,new Color(255,0,0,0),this);		
				dateBox.click(appMain);
			}else if(i==6 || i==13 || i==20 || i==27){
				dateBox = new DateBox("", Color.white, 130,79,Color.blue,yy,mm,"","","",category,new Color(255,0,0,0),this);
				dateBox.click(appMain);
			}else {
				dateBox = new DateBox("", Color.white, 130,79,Color.black,yy,mm,"","","",category,new Color(255,0,0,0),this);
				dateBox.click(appMain);	
			}
			p_center.add(dateBox);
			boxArray[i]=dateBox;
		}

	}
	
	
	//날짜 패널 삭제
	public void removeDate() {
		for(int i=0;i<dayArray.length*6;i++) {
			boxArray[i].setVisible(false);
		}
	}
	
	//해당 월의 시작 요일 구하기!!
	public int getFirstDayOfMonth(int yy, int mm) {
		Calendar cal=Calendar.getInstance(); //날짜 객체 생성 
		cal.set(yy,mm, 1);//해당년도와 월의 1을로 조작!!
		
		return cal.get(Calendar.DAY_OF_WEEK)-1; //주의 js 에서의 습관이 있기에 요일은 0부터 시작하는 걸로..		
	}
	
	public int getFirstDate(int yy, int mm) { 
		Calendar cal = Calendar.getInstance();
		cal.set(yy,mm, 1); //0일이란 존재하지 않는 날짜이므로, 이전 월의 마지막날을 의미한다!!!
		return cal.get(Calendar.DATE);
	}
	
	//해당월이 몇일까지 있는지 구하기!!  8월 31일, 9월 30일
	//호출방법:  2월에 대한 정보를 구할 경우  getLastDate(2021, 2)
	public int getLastDate(int yy, int mm) { 
		Calendar cal = Calendar.getInstance();
		cal.set(yy,mm, 0); //0일이란 존재하지 않는 날짜이므로, 이전 월의 마지막날을 의미한다!!!
		return cal.get(Calendar.DATE);
	}
	

	//Box에 날짜 출력!!!
	public void printDate() {
		int n=1;
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		
		for(int i=0;i<boxArray.length;i++) {
			if(i>=getFirstDayOfMonth(yy,mm) ) {
				//각월의 총 날수까지만 출력되게..
				if(n <=getLastDate(yy, mm+1)) {
					calDates[i] = n;	
					boxArray[i].day=Integer.toString(n);
						boxArray[i].repaint(); //텍스트를 다시 그리자!! 즉 그래픽 갱신!!
						n++;						
				}
			}
		}
	}
	
	//기존의 Box에 그려진 스트링 모두 지우기 
	public void removeString() {
		for(int i=0;i<boxArray.length;i++) {
			boxArray[i].day="";
			boxArray[i].repaint(); //그림 다시 갱신
		}
	}
	
	
	//이전월 구하기
	public void prevMonth() {
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		//현재 날짜 객체에 월을+1한다, 기왕이면 날짜는 1일로...
		currentDate.set(yy, mm-1,1); //yy, mm, dd

	}
	
	//이전 년도 구하기
	public void prevYear() {
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		currentDate.set(yy-1, mm,1); //yy, mm, dd
	}

	
	//다음 월 구하기 
	public void nextMonth() {
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		currentDate.set(yy, mm+1,1); //yy, mm, dd
	}
	
	//다음 년도 구하기
	public void nextYear() {
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		currentDate.set(yy+1, mm,1); //yy, mm, d

	}
	
	//오늘로 돌아오기
	public void moveToday() {
		currentDate = new GregorianCalendar(yy_t,mm_t,dd_t);
	}
	

	//한자리 숫자에 0붙이기
	public static String getZeroString(int n) {
		return (n<10)? "0"+n:Integer.toString(n);
	}
	
	//입력한 곳의 날짜 
	public void registDate(AppMain appMain) {
		
		int member_id = appMain.getMember().getMember_id();
		String slq = "select * from calendar where member_id="+member_id;
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		int dd = getFirstDayOfMonth(yy, mm);
		String title; //제목
		String cal_date; //am or pm
		String cal_time; //시간
		PreparedStatement pstmt=null;	
		PreparedStatement pstmt2=null;	
		ResultSet rs=null; 
		ResultSet rs2=null; 
		String date =null;
		
		try {
			pstmt = con.prepareStatement(slq);
			 rs = pstmt.executeQuery();
			while(rs.next()) {
				int year = rs.getInt("year");
				int month = rs.getInt("month");
				date = rs.getString("date");
				title = rs.getString("cal_title");
				category = rs.getInt("cal_category");
				cal_date = rs.getString("cal_date");
				cal_time = rs.getString("cal_time");
				
				String sql2 = "select cal_name from calendar_category where cal_category="+category;
				pstmt2 = con.prepareStatement(sql2);
				rs2 = pstmt2.executeQuery();
				while(rs2.next()) {
					cate = rs2.getString("cal_name");					
				}
				
				//System.out.println(year+"년"+month+"월"+date+"일"+"에 값이 저장되어 있음");
				//System.out.println(Integer.parseInt(date)+(dd-1)+"번째임");
				if(year==yy && month ==(mm+1)) {
					for(int i=0;i<boxArray.length;i++) {
						if(category ==1) {
							boxArray[Integer.parseInt(date)+(dd-1)].cc = new Color(136,133,164);
							boxArray[Integer.parseInt(date)+(dd-1)].title=cate;
							boxArray[Integer.parseInt(date)+(dd-1)].cal_date=cal_date;
							boxArray[Integer.parseInt(date)+(dd-1)].cal_time=cal_time;
							boxArray[Integer.parseInt(date)+(dd-1)].cate=category;	
							boxArray[Integer.parseInt(date)+(dd-1)].cate=category;	
							boxArray[Integer.parseInt(date)+(dd-1)].repaint();
						}else if(category==2) {
							boxArray[Integer.parseInt(date)+(dd-1)].cc = new Color(128,128,0);
							boxArray[Integer.parseInt(date)+(dd-1)].title=cate;
							boxArray[Integer.parseInt(date)+(dd-1)].cal_date=cal_date;
							boxArray[Integer.parseInt(date)+(dd-1)].cal_time=cal_time;
							boxArray[Integer.parseInt(date)+(dd-1)].cate=category;	
							boxArray[Integer.parseInt(date)+(dd-1)].cate=category;	
							boxArray[Integer.parseInt(date)+(dd-1)].repaint();
						}else if(category==3) {
							boxArray[Integer.parseInt(date)+(dd-1)].cc = new Color(255,127,0);
							boxArray[Integer.parseInt(date)+(dd-1)].title=cate;
							boxArray[Integer.parseInt(date)+(dd-1)].cal_date=cal_date;
							boxArray[Integer.parseInt(date)+(dd-1)].cal_time=cal_time;
							boxArray[Integer.parseInt(date)+(dd-1)].cate=category;	
							boxArray[Integer.parseInt(date)+(dd-1)].cate=category;	
							boxArray[Integer.parseInt(date)+(dd-1)].repaint();
						}else if(category==4) {
							boxArray[Integer.parseInt(date)+(dd-1)].cc = new Color(97,219,240);
							boxArray[Integer.parseInt(date)+(dd-1)].title=cate;
							boxArray[Integer.parseInt(date)+(dd-1)].cal_date=cal_date;
							boxArray[Integer.parseInt(date)+(dd-1)].cal_time=cal_time;
							boxArray[Integer.parseInt(date)+(dd-1)].cate=category;	
							boxArray[Integer.parseInt(date)+(dd-1)].cate=category;	
							boxArray[Integer.parseInt(date)+(dd-1)].repaint();
						}else {
							boxArray[Integer.parseInt(date)+(dd-1)].cc = new Color(255,0,0,0);
							boxArray[Integer.parseInt(date)+(dd-1)].repaint();
						}
						
					}
					
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			release(pstmt2, rs2);
			release(pstmt, rs);
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
