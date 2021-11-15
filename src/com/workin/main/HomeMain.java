package com.workin.main;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import com.workin.member.MemberMain;

public class HomeMain extends Page{
	AppMain appMain;
	MemberMain memberMain;
	
	JPanel p_super; //전체 패널
	
	JPanel p_north; //북쪽 패널
	JButton bt_prev; //저번주로 이동
	JLabel l_date; //현재 년도와 월을 나타냄
	JButton bt_next; //다음주로 이동
	
	JPanel p_t; //테이블 올려놓을 패널
	JScrollPane t_scroll; 
	JTable w_table; //테이블

	//테이블 관련
	String[] col = {}; //컬럼제목 = 날짜 가로
	String[][] record = {}; //데이터 값 세로
	
	
	Calendar currentDate; //다음달, 이전달로 이동할 때 사용
	Calendar todayDate; 
	Calendar today = Calendar.getInstance(); 
	
	String  dates[] = new String[8]; //한주의 날짜를 담아놓을 배열
	JTableHeader table_header;
	Integer nm[]= {};
	
	int member_n; //멤버 수 
	
	String[] c_member= {};
	int n_category;
	String cal_date;
	int cal_time;
	int year;
	int month;
	int date;
	int w;
	
	int yy_t = today.get(Calendar.YEAR);
	int mm_t = today.get(Calendar.MONTH);
	int dd_t = today.get(Calendar.DATE);
	
	String url="jdbc:mysql://localhost:3306/workinapp?useSSL=false&characterEncoding=UTF-8";
	String user="root";
	String password="1234";
	Connection  con;
	
	public HomeMain(AppMain appMain, MemberMain memberMain) {
		
		super(appMain);
		conn();
		try{
			UIManager.setLookAndFeel ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");//LookAndFeel Windows 스타일 적용
			SwingUtilities.updateComponentTreeUI(this) ;
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, e);
		}
		this.appMain = appMain;
		this.memberMain = memberMain;
		
		col = new String[dates.length];
		//record = new String[8][10]; //회원수 만큼으로 교체해야함
		
		//생성
		p_super = new JPanel();
		p_north = new JPanel();
		bt_prev = new JButton("<<");
		l_date  = new JLabel();
		bt_next = new JButton(">>");
		p_t = new JPanel();
		
	
	
		
		getCurrentDate();
		setTitle();
		setDate();
		//setMemberName();// 멤버이름 채워 넣기
		//이차원 배열의 0번째에는 회원명 
		getMName();
		
	
		//테이블생성
		w_table = new JTable(){
            @Override
            public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {                
                if (col == 0) {
                    return this.getTableHeader().getDefaultRenderer()
                        .getTableCellRendererComponent(this, this.getValueAt(
                            row, col), false, false, row, col);
                }
                else {
                    return super.prepareRenderer(renderer, row, col);
                }
            }
 
        };

		w_table.setModel(new AbstractTableModel() {
			public Object getValueAt(int rowIndex, int columnIndex) {
				return record[rowIndex][columnIndex];
			}
			
			public int getRowCount() {
				return record.length;
			}
			
			public int getColumnCount() {
				return col.length;
			}
			public String getColumnName(int cols) {
				//System.out.println(dates[5]);
				return dates[cols];
			}
		}); 
		
		
	
		
		
		tableCellCenter(w_table);
		w_table.getColumnModel().getColumn(0).setPreferredWidth(20);
		w_table.getColumnModel().getColumn(1).setPreferredWidth(45);
		w_table.getColumnModel().getColumn(2).setPreferredWidth(45);
		w_table.getColumnModel().getColumn(3).setPreferredWidth(45);
		w_table.getColumnModel().getColumn(4).setPreferredWidth(45);
		w_table.getColumnModel().getColumn(5).setPreferredWidth(45);
		w_table.getColumnModel().getColumn(6).setPreferredWidth(45);
		w_table.getColumnModel().getColumn(7).setPreferredWidth(45);
		w_table.setRowHeight(40);
		w_table.setShowVerticalLines(true);
		
		//이름
        final JTableHeader header = w_table.getTableHeader();
        header.setDefaultRenderer(new HeaderRenderer(w_table));
    	header.setPreferredSize(new Dimension(10,30));


		t_scroll = new JScrollPane(w_table);
		
		//스타일 및 레이아웃
		p_super.setPreferredSize(new Dimension(1020,680));
		l_date.setFont(new Font("Arial-Black",Font.BOLD,22));
		t_scroll.setPreferredSize(new Dimension(960,600));
//		t_scroll.setBorder(BorderFactory.createEmptyBorder());
		t_scroll.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));
		
		getCurrentDate();
		//이벤트
		bt_prev.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDateFirst();
				prev_week(); //이전 주
				setTitle();
				tableEmpty() ;
				getSchedule();
				w_table.updateUI();
				 
			}
		});
		bt_next.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setDateFirst2();
				next_week(); //다음주
				setTitle();
				tableEmpty() ;
				getSchedule();
				w_table.updateUI();		
			}
		});
		l_date.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				turnToday(); //오늘로 돌아옴
				setTitle();
				tableEmpty() ;
				getSchedule();
				w_table.updateUI();
				System.out.println("라벨클릭");
			}
		});
		//날짜
		w_table.setAutoCreateRowSorter(false);
		table_header = w_table.getTableHeader();
		table_header.setPreferredSize(new Dimension(10,30));
		
		

		
		
		//조립
		p_north.add(bt_prev);
		p_north.add(l_date);
		p_north.add(bt_next);
		p_t.add(t_scroll);
		p_super.add(p_north,BorderLayout.NORTH);
		p_super.add(p_t);
		add(p_super);
		
	
		
		//보여주기
		setVisible(true);
		//setBackground(Color.WHITE);
		getSchedule();
		
		

		
			
	}
	// DB연결
	public void conn() {
		try {
			Class.forName("com.mysql.jdbc.Driver");//1) 드라이버 로드
			con = DriverManager.getConnection(url,user,password);
			if(con!=null) {
				System.out.println("홈 메인 DB 접속성공");
			}else {
				JOptionPane.showMessageDialog(this, "DB에 접속할 수 없습니다");
			}
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (SQLException e) {
			e.printStackTrace();
		}	
	}
	
	//디자인
	private static class HeaderRenderer implements TableCellRenderer {

        TableCellRenderer renderer;

        public HeaderRenderer(JTable table) {
            renderer = table.getTableHeader().getDefaultRenderer();
        }

        @Override
        public Component getTableCellRendererComponent(
            JTable table, Object value, boolean isSelected,
            boolean hasFocus, int row, int col) {
            return renderer.getTableCellRendererComponent(
                table, value, isSelected, hasFocus, row, col);
        }
    }
	

	//멤버 이름과 아이디  가져오기
	public void getMName(){
		String sql = "select member_id , user_name from member order by member_id asc";
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			//해보는것
			rs.last();
			int total = rs.getRow();
			record = new String[total][8];
			nm = new Integer[total];
			rs.beforeFirst();
			int index =0;
			
			while(rs.next()) {
				record[index][0] =rs.getString("user_name");
				nm[index] = rs.getInt("member_id");
				index++;
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			release(pstmt, rs);
		}
	}
	
	//캘린더에서 정보 가져오기
	public void getSchedule() {
		//System.out.println("정보가져오기!");
		c_member = new String[record.length]; 
		
		for(int i=0;i<c_member.length;i++) {
			c_member[i] = record[i][0]; //값 옮겨넣기!
			//System.out.println(c_member[i]);
		}
		
		for(int i=0;i<c_member.length;i++) {		
			String sql ="select c.member_id, c.cal_category, c.cal_writer, c.cal_date, c.cal_time, c.year, c.month, c.date";
			sql+=" from calendar as c join member as m"; 
			sql+=" on c.member_id = m.member_id";
			sql+=" where c.member_id ="+nm[i];
			
			
			String writer=null; //회원이름 = 작성자
			int member_id =0;//회원넘버
			
			
			int [] array;
			int yy = currentDate.get(Calendar.YEAR);
			int mm = currentDate.get(Calendar.MONTH);
			String m = getZeroString(mm+1);
			String ym = Integer.toString(yy) +m;
			int wm = currentDate.get(Calendar.WEEK_OF_MONTH);
			array = getRangeDateOfWeek(ym, wm); //오늘이 포함된 한주의 배열
			
			PreparedStatement pstmt = null;
			ResultSet rs = null;
			
			try {
				pstmt = con.prepareStatement(sql);
				rs = pstmt.executeQuery();
				rs.beforeFirst();
				while(rs.next()) {
					member_id = rs.getInt("member_id");
					year = rs.getInt("year");
					month = rs.getInt("month");
					date = rs.getInt("date");
					
					n_category = rs.getInt("cal_category");
					cal_date = rs.getString("cal_date");
					cal_time = rs.getInt("cal_time");
					
					if(year==yy&&month==(mm+1)) {
						for(int k=0;k<array.length;k++) {
							if(date == array[k]) {
								String y = Integer.toString(year);
								String m1 = Integer.toString(month);
								String d = Integer.toString(date);
								w = dayOfWeek(y, m1, d); //해당 일의 요일의 값을 반환
//								System.out.println(c_member[i]+"멤버는 "+year+"년 "+(month)+"월 "+date+"일 "+w+"요일에 일정이 있어요");
//								System.out.println(n_category+"글이고 "+cal_date+" "+cal_time+"에 있어요");
//								System.out.println("-------------------------------------------");
								if(record[i][0] == c_member[i]) {
									if(n_category==1) {
										record[i][w+1] = "회의  "+cal_date +" "+ cal_time;	
										
									}else if(n_category==2) {
										record[i][w+1] = "외근  "+cal_date +" "+ cal_time;
									}else if(n_category==3) {
										record[i][w+1] = "재택근무";
									}else if(n_category==4) {
										record[i][w+1] = "휴가";
									}
								}
							}
						}
					}	
				} 
			} catch (SQLException e) {
				e.printStackTrace();
			}finally {
				release(pstmt, rs);
			}
		}
	}
	
	//테이블 초기화 하기
	public void tableEmpty() {
		for(int i=0;i<c_member.length;i++) {
			for(int j=1;j<8;j++) {
				record[i][j] = " ";
			}
		}
	}

	
	//현재 날짜 구하기
	public Calendar getCurrentDate() {
		currentDate = Calendar.getInstance();
		return currentDate;
	}
	
	//달력의 제목 출력
	public void setTitle() {
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		
		l_date.setText("   "+yy +"-"+getZeroString(mm+1)+"   ");
		
	}
	
	//오늘로 돌아오기
	public void turnToday() {
		currentDate = new GregorianCalendar(yy_t,mm_t,dd_t);
		
		TableColumnModel tcm = table_header.getColumnModel();
		TableColumn tc ;
 
		int [] array;
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		int dd = currentDate.get(Calendar.DATE);
		
		String m = getZeroString(mm+1);
		String ym = Integer.toString(yy) +m;
		int wm = currentDate.get(Calendar.WEEK_OF_MONTH);
		array = getRangeDateOfWeek(ym, wm);
		for(int i=0;i<array.length;i++) {
			if(i==0) {
				dates[i+1] = Integer.toString(array[i])+" / 일";				
			}else if(i==1) {
				dates[i+1] = Integer.toString(array[i])+" / 월";		
			}else if(i==2) {
				dates[i+1] = Integer.toString(array[i])+" / 화";		
			}else if(i==3) {
				dates[i+1] = Integer.toString(array[i])+" / 수";		
			}else if(i==4) {
				dates[i+1] = Integer.toString(array[i])+" / 목";		
			}else if(i==5) {
				dates[i+1] = Integer.toString(array[i])+" / 금";		
			}else if(i==6) {
				dates[i+1] = Integer.toString(array[i])+" / 토";		
			}
		}
		showToday();
		for(int i=0;i<dates.length;i++) {
			tc = tcm.getColumn(i);
			tc.setHeaderValue(dates[i]);
			System.out.println(dates[i]);
			table_header.repaint();			
		}
		
	}
	
	
	//현재 한 주의 값 구하기
	public void setDate() {
		int [] array;
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		String m = getZeroString(mm+1);
		String ym = Integer.toString(yy) +m;
		int wm = currentDate.get(Calendar.WEEK_OF_MONTH);
		array = getRangeDateOfWeek(ym, wm);
		
		for(int i=0;i<array.length;i++) {
			if(i==0) {
				dates[i+1] = Integer.toString(array[i])+" / 일";				
			}else if(i==1) {
				dates[i+1] = Integer.toString(array[i])+" / 월";		
			}else if(i==2) {
				dates[i+1] = Integer.toString(array[i])+" / 화";		
			}else if(i==3) {
				dates[i+1] = Integer.toString(array[i])+" / 수";		
			}else if(i==4) {
				dates[i+1] = Integer.toString(array[i])+" / 목";		
			}else if(i==5) {
				dates[i+1] = Integer.toString(array[i])+" / 금";		
			}else if(i==6) {
				dates[i+1] = Integer.toString(array[i])+" / 토";		
			}
		}
		
		showToday();
	}
	
	//오늘 표시하기
	public void showToday() {
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		String d =null;
		for(int i=0;i<dates.length-1;i++) {		
			if(dates[i+1].length()==5) {
				d = dates[i+1].substring(0, 1);				
			}else if(dates[i+1].length()==6) {
				d = dates[i+1].substring(0, 2);	
			}
			int da = Integer.parseInt(d);			
			if(da==dd_t&& yy==yy_t &&mm==mm_t ) {
				dates[i+1] = dates[i+1]+ "  [today]";
			}
		}
	}

	//저번 주 구하기
	public void prev_week() {
		TableColumnModel tcm = table_header.getColumnModel();
		TableColumn tc ;
 
		int [] array;
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		int dd = currentDate.get(Calendar.DATE);
		
		String m = getZeroString(mm+1);
		String ym = Integer.toString(yy) +m;
		int wm = currentDate.get(Calendar.WEEK_OF_MONTH);
		array = getRangeDateOfWeek(ym, wm-1);
		for(int i=0;i<array.length;i++) {
			if(i==0) {
				dates[i+1] = Integer.toString(array[i])+" / 일";				
			}else if(i==1) {
				dates[i+1] = Integer.toString(array[i])+" / 월";		
			}else if(i==2) {
				dates[i+1] = Integer.toString(array[i])+" / 화";		
			}else if(i==3) {
				dates[i+1] = Integer.toString(array[i])+" / 수";		
			}else if(i==4) {
				dates[i+1] = Integer.toString(array[i])+" / 목";		
			}else if(i==5) {
				dates[i+1] = Integer.toString(array[i])+" / 금";		
			}else if(i==6) {
				dates[i+1] = Integer.toString(array[i])+" / 토";		
			}
		}
		showToday();
		for(int i=0;i<dates.length;i++) {
			tc = tcm.getColumn(i);
			tc.setHeaderValue(dates[i]);
			System.out.println(dates[i]);
			table_header.repaint();			
		}
		
//		if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==0) { //일요일
//			currentDate.set(yy, mm, dd-1);								
//		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==1) { //월요일
//			currentDate.set(yy, mm, dd-2);			
//		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==2) { //화요일
//			currentDate.set(yy, mm, dd-3);			
//		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==3) { //수요일
//			currentDate.set(yy, mm, dd-4);			
//		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==4) { //목요일
//			currentDate.set(yy, mm, dd-5);			
//		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==5) { //금요일
//			currentDate.set(yy, mm, dd-6);			
//		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==6) { //토요일
//			currentDate.set(yy, mm, dd-7);			
//		}

		currentDate.set(yy, mm, dd-7);					
	
		
	}
	
	
	
	public void setDateFirst() {
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		int dd = currentDate.get(Calendar.DATE);
		System.out.println("이전 "+yy+"년"+(mm+1)+"월"+dd+"일");
		if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==0) { //일요일
			currentDate.set(yy, mm,dd);				
		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==1) { //월요일
			currentDate.set(yy, mm, dd-1);			
		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==2) { //화요일
			currentDate.set(yy, mm, dd-2);			
		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==3) { //수요일
			currentDate.set(yy, mm, dd-3);			
		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==4) { //목요일
			currentDate.set(yy, mm, dd-4);			
		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==5) { //금요일
			currentDate.set(yy, mm, dd-5);			
		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==6) { //토요일
			currentDate.set(yy, mm, dd-6);			
		}
	}
	
	public void setDateFirst2() {
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		int dd = currentDate.get(Calendar.DATE);
		System.out.println("다음 "+yy+"년"+(mm+1)+"월"+dd+"일");
		if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==0) { //일요일
			currentDate.set(yy, mm, dd+6);								
		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==1) { //월요일
			currentDate.set(yy, mm, dd+5);			
		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==2) { //화요일
			currentDate.set(yy, mm, dd+4);			
		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==3) { //수요일
			currentDate.set(yy, mm, dd+3);			
		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==4) { //목요일
			currentDate.set(yy, mm, dd+2);			
		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==5) { //금요일
			currentDate.set(yy, mm, dd+1);			
		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==6) { //토요일
			currentDate.set(yy, mm, dd);			
		}
	}
	
	//다음주 구하기
	public void next_week() {
		TableColumnModel tcm = table_header.getColumnModel();
		TableColumn tc ;
 
		int [] array;
		int yy = currentDate.get(Calendar.YEAR);
		int mm = currentDate.get(Calendar.MONTH);
		int dd = currentDate.get(Calendar.DATE);
		
		String m = getZeroString(mm+1);
		String ym = Integer.toString(yy) +m;
		int wm = currentDate.get(Calendar.WEEK_OF_MONTH); //지금 현재 몇번째 주인지 알려줌
		
		array = getRangeDateOfWeek(ym, wm+1); //한주의 값
		for(int i=0;i<array.length;i++) {
			if(i==0) {
				dates[i+1] = Integer.toString(array[i])+" / 일";				
			}else if(i==1) {
				dates[i+1] = Integer.toString(array[i])+" / 월";		
			}else if(i==2) {
				dates[i+1] = Integer.toString(array[i])+" / 화";		
			}else if(i==3) {
				dates[i+1] = Integer.toString(array[i])+" / 수";		
			}else if(i==4) {
				dates[i+1] = Integer.toString(array[i])+" / 목";		
			}else if(i==5) {
				dates[i+1] = Integer.toString(array[i])+" / 금";		
			}else if(i==6) {
				dates[i+1] = Integer.toString(array[i])+" / 토";		
			}
		}
		showToday();
		for(int i=0;i<dates.length;i++) {
			tc = tcm.getColumn(i);
			tc.setHeaderValue(dates[i]);
			//System.out.println(dates[i]);
			table_header.repaint();			
		}
		
		
//		System.out.println("dd값 "+dd);
//		System.out.println("wm값 "+wm);
//		if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==0) { //일요일
//			currentDate.set(yy, mm, dd+6);								
//		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==1) { //월요일
//			currentDate.set(yy, mm, dd+5);			
//		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==2) { //화요일
//			currentDate.set(yy, mm, dd+4);			
//		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==3) { //수요일
//			currentDate.set(yy, mm, dd+3);			
//		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==4) { //목요일
//			currentDate.set(yy, mm, dd+2);			
//		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==5) { //금요일
//			currentDate.set(yy, mm, dd+1);			
//		}else if(dayOfWeek(Integer.toString(yy),Integer.toString(mm+1),Integer.toString(dd) )==6) { //토요일
//			currentDate.set(yy, mm, dd+7);			
//		}
		currentDate.set(yy, mm, dd+7);
	}
	


	//해당 년도 월 주의 날짜 배열을 얻어옴
    public static int[] getRangeDateOfWeek(String yyyymm,int weekSeq) {
        int rangeDateOfWeek [] =new int[7];
         
        int startDayOfWeek = dayOfWeek(yyyymm.substring(0,4), yyyymm.substring(4,6),"1"); //한주의 시작
         
        if( startDayOfWeek ==0 || weekSeq >1 ){
            Calendar cal = converterDate(yyyymm+"01");
            int lastDateOfMonth = getLastDateOfMonth(new SimpleDateFormat("yyyyMM").format(cal.getTime()));
             
            int startDate =1 + ((weekSeq-1)*7) - startDayOfWeek;
            for(int i=0; i<7; i++ ){
                if( startDate > lastDateOfMonth ){
                    startDate =1;
                }
                rangeDateOfWeek[i] = startDate++;
            }
        }else{
            Calendar cal = converterDate(yyyymm+"01");
            cal.add(Calendar.MONTH, -1);
            int lastDateOfBeforeMonth = getLastDateOfMonth(new SimpleDateFormat("yyyyMM").format(cal.getTime()));
             
            int startDate = (lastDateOfBeforeMonth +1) - startDayOfWeek;
            for(int i=0; i<7; i++ ){
                if( startDate > lastDateOfBeforeMonth ){
                    startDate =1;
                }              
                rangeDateOfWeek[i] = startDate++;
            }
        }
        return rangeDateOfWeek;
    }


    //특정 날짜의 요일의 숫자를 리턴
    public static int dayOfWeek(String sYear, String sMonth, String sDay) {    
        
        int iYear = Integer.parseInt(sYear);
        int iMonth = Integer.parseInt(sMonth) -1;
        int isDay = Integer.parseInt(sDay);
         
        GregorianCalendar gc =new GregorianCalendar(iYear, iMonth, isDay);
 
        return gc.get(gc.DAY_OF_WEEK) -1;             
    }

    //String 형식의 날짜를 Calendar로 변환해줌
    public static Calendar converterDate(String yyyymmdd) {
        Calendar cal = Calendar.getInstance();// 양력 달력
        if (yyyymmdd ==null)
            return cal;
 
        String date = yyyymmdd.trim();
        if (date.length() !=8) {
            if (date.length() ==4)
                date = date +"0101";
            else if (date.length() ==6)
                date = date +"01";
            else if (date.length() >8)
                date = date.substring(0,8);
            else
                return cal;
        }
 
        cal.set(Calendar.YEAR, Integer.parseInt(date.substring(0,4)));
        cal.set(Calendar.MONTH, Integer.parseInt(date.substring(4,6)) -1);
        cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(date.substring(6)));
 
        return cal;
    }

    //해당월의 마지막날을 구해줌
    public static int getLastDateOfMonth() {
        return getLastDateOfMonth(new Date());
    }
    public static int getLastDateOfMonth(Date date) {
        return getLastDateOfMonth(new SimpleDateFormat("yyyyMM").format(date));
    }
    public static int getLastDateOfMonth(String yyyymm) {
        int year = Integer.parseInt(yyyymm.substring(0,4));
        int month = Integer.parseInt(yyyymm.substring(4,6)) -1;
         
        Calendar destDate = Calendar.getInstance();
        destDate.set(year, month,1);
         
        return destDate.getActualMaximum(Calendar.DATE);
    }


	//한자리 숫자에 0붙이기
	public static String getZeroString(int n) {
		return (n<10)? "0"+n:Integer.toString(n);
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
	
	public void tableCellCenter(JTable table) {
	    // 테이블 내용 가운데 정렬하기
	      DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(); // 디폴트테이블셀렌더러를 생성
	      dtcr.setHorizontalAlignment(SwingConstants.CENTER); // 렌더러의 가로정렬을 CENTER로
	     
	      TableColumnModel tcm = table.getColumnModel() ; // 정렬할 테이블의 컬럼모델을 가져옴
	      
	      //특정 열에 지정
	      tcm.getColumn(0).setCellRenderer(dtcr);  
	      tcm.getColumn(1).setCellRenderer(dtcr);  
	      tcm.getColumn(2).setCellRenderer(dtcr);  
	      tcm.getColumn(3).setCellRenderer(dtcr);
	      tcm.getColumn(4).setCellRenderer(dtcr);
	      tcm.getColumn(5).setCellRenderer(dtcr);
	      tcm.getColumn(6).setCellRenderer(dtcr);
	      tcm.getColumn(7).setCellRenderer(dtcr);
	      
	}	
}