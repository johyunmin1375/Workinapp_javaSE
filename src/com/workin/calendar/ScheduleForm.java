package com.workin.calendar;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import com.workin.main.AppMain;
import com.workin.member.MemberMain;

//패널을 클릭했을 때 보여짐
public class ScheduleForm extends JDialog{
	JPanel p;
	MemberMain memberMain;
	Integer time[]= {0,1,2,3,4,5,6,7,8,9,10,11,12};
	String[] rbb = {"AM","PM"};
	
	JPanel p_sum;
	
	JPanel p_top;
	JLabel la_title;
	
	JPanel p_date;
	JRadioButton[] radio;
	ButtonGroup group;
	JComboBox<Integer> com_time;
	
	JPanel p_north;
	JComboBox<String> com_sch;
	JTextField title;
	
	JPanel p_center;
	JTextArea detail;
	JScrollPane scroll;
	
	JPanel p_south;
	JButton bt_regist;
	JButton bt_ok;
	
	String url="jdbc:mysql://localhost:3306/workinapp?useSSL=false&characterEncoding=UTF-8";
	String user="root";
	String password="1234";
	Connection  con;
	String cal_date;
	
	ArrayList<String> array = new ArrayList<String>();
	CalendarMain calendarmain;
	DateBox db;
	AppMain appMain;
	String choose= null;
	int year=0;
	int month=0;
	int t ;
	
	class MyItemListener implements ItemListener{
		public void itemStateChanged(ItemEvent e) {
			 if (e.getStateChange() == ItemEvent.DESELECTED)
	                return; //라디오버튼이 선택 해제된 경우 그냥 리턴
	         if (radio[0].isSelected()) {
	        	 cal_date="AM";
	         }else if (radio[1].isSelected()) {
	        	 cal_date="PM";
	         }
			
		}
	}
	
	public ScheduleForm(JFrame frame,String top,AppMain appMain,String day,int year, int month, JPanel p,CalendarMain calendarmain) {
		super(frame,top);
		this.p=p;
		this.calendarmain = calendarmain;
		this.appMain = appMain;
		conn();
		
		
		try{ //룩앤필이용해서 UI깔끔하게 
			UIManager.setLookAndFeel ("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");//LookAndFeel Windows 스타일 적용
			SwingUtilities.updateComponentTreeUI(this) ;
		}catch(Exception e){
			JOptionPane.showMessageDialog(this, e);
		}

		
		//생성
		p_sum = new JPanel();
		p_top = new JPanel();
		la_title = new JLabel("신규 일정 등록");
		
		p_date = new JPanel();
		radio = new JRadioButton[2];
		group = new ButtonGroup();
		com_time = new JComboBox<Integer>(time);
		
		p_north = new JPanel();
		com_sch = new JComboBox<String>(array.toArray(new String[array.size()]));
		title = new JTextField();
		p_center = new JPanel();
		detail = new JTextArea(18,54);
		scroll = new  JScrollPane(detail);
		p_south = new JPanel();
		bt_regist = new JButton("등록");
		bt_ok	 = new JButton("취소");
		
		//스타일
		p_sum.setLayout(new GridLayout(3,1));
		com_sch.setPreferredSize(new Dimension(100,30));
		com_time.setPreferredSize(new Dimension(100,30));
		title.setPreferredSize(new Dimension(350,30));
		la_title.setFont(new Font("맑은 고딕", Font.BOLD, 20));
		combolist();
		
		
		//조립
		p_top.add(la_title);
		for(int i=0;i<radio.length;i++) {
			radio[i] = new JRadioButton(rbb[i]);
			group.add(radio[i]);
			p_date.add(radio[i]);
			radio[i].addItemListener(new MyItemListener());
		}
		
		p_date.add(com_time);
		
		p_north.add(com_sch);
		p_north.add(title);
		
		p_sum.add(p_top);
		p_sum.add(p_north);
		p_sum.add(p_date);
		
		
		add(p_sum,BorderLayout.NORTH);
		p_center.add(scroll);
		add(p_center);
		
		p_south.add(bt_regist);
		p_south.add(bt_ok);
		add(p_south,BorderLayout.SOUTH);

		
		bt_regist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				checkE(appMain);
			}
		});
		
		bt_ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		
		
		//보여주기
		setSize(520,540);
		setLocationRelativeTo(frame);
		
		//System.out.println("가져온값"+day);
		choose = day;
		//System.out.println("choose값"+choose);
		this.year = year;
		this.month = month;
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
	
	//db에서 콤보박스 값 가져오기
	public void combolist() {
		String sql = "select  cal_name from calendar_category";
		PreparedStatement pstmt =null;
		ResultSet rs =null;
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			while(rs.next()) {
				String dd = rs.getString("cal_name");
				com_sch.addItem(dd);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			release(pstmt, rs);
		}
	}
	
	
	//입력값 지워놓기
	public void setClear() {
		for(int i=0;i<radio.length;i++) {
			radio[i].setSelected(false);
		}
		
		title.setText("");
		detail.setText("");
	}
	
	//일정등록
	public void regist(AppMain appMain) {		
		//System.out.println("마지막"+this.choose);
		int id = appMain.getMember().getMember_id(); //member_id
		int r = com_sch.getSelectedIndex(); //cal_category
		String m_title = title.getText(); //cal_title
		String m_content =detail.getText(); //cal_content
		String name = appMain.getMember().getUser_name(); //cal_writer
		t = com_time.getSelectedIndex();//cal_time
		int year = this.year;
		int month = this.month;
		int date = Integer.parseInt(this.choose); //선택한 date값
		//System.out.println("선택한 날짜의 입력폼"+year+(month+1)+date);
		
		//System.out.println("이름은 "+name+"아이디는 "+id +"선택된 값의 인덱스는" +r+"title:"+m_title+"content: "+m_content+"t : "+t);
		String sql = "insert into calendar(member_id, cal_category, cal_title, cal_content, cal_writer,cal_date, cal_time,year,month,date)";
		sql+=" values("+id+","+(r+1)+",?,?,?,?,?,?,?,?)";
		
		PreparedStatement pstmt = null;
		
		//System.out.println(sql);
		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, m_title);
			pstmt.setString(2, m_content);
			pstmt.setString(3, name);
			pstmt.setString(4, cal_date);
			pstmt.setInt(5, t);
			pstmt.setInt(6, year);
			pstmt.setInt(7, (month+1));
			pstmt.setInt(8, date);	
			int result = pstmt.executeUpdate();
			
			if(result==1) {
				JOptionPane.showMessageDialog(this, "등록성공");
				
			}else {
				JOptionPane.showMessageDialog(this, "등록실패");
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if(pstmt !=null) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		this.setVisible(false);
		//updateD(appMain);
//		p.repaint();
//		calendarmain.p_center.repaint();
//		p_center.updateUI();
//		calendarmain.removeDate(); //날짜 패널 삭제
//		calendarmain.createDate(appMain); //날짜 패널 생성
//		calendarmain.removeString();//기존 날짜 지우기
//		calendarmain.setDateTitle(); //달력 제목 바꾸기 
//		calendarmain.printDate(); //날짜 출력하기	
		calendarmain.registDate(appMain); //일정 추가하기	
		setClear();//입력폼 값 비워두기
	}
	
	//일정 입력할때 비어있으면 경고하기
	public void checkE(AppMain appMain) {	
		if(title.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "제목을 입력해주세요");
		}else if(radio[0].isSelected()==false && radio[1].isSelected()==false){
			JOptionPane.showMessageDialog(this, "시간을 선택해주세요");
		}else if(detail.getText().equals("")) {
			JOptionPane.showMessageDialog(this, "내용을 입력해주세요");
		}else {
			regist(appMain);
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
