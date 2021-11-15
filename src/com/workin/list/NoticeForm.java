package com.workin.list;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.workin.main.AppMain;
import com.workin.member.MemberMain;
import com.workin.util.FileManager;

public class NoticeForm extends JFrame{
	AppMain appMain;
	MemberMain memberMain;
	ListMain listMain;
	
	JPanel p_center;
	JLabel la_title;
	JComboBox<String> cb_level;
	String[] level = {"일반","긴급","공지"};
	JTextField t_title;
	JTextArea t_area;
	JButton bt_file;
	JButton bt_regist;
	JButton bt_cancel;
	JLabel la_file_name;
	JLabel la_file;
	
	JFileChooser chooser;
	String filename; //복사에 의해 생성될 파일명
	
	//데이터베스 관련 
	String driver="com.mysql.jdbc.Driver"; // 8.xx 인 경우 com.mysql.jdbc.cj.Driver
	String url="jdbc:mysql://localhost:3306/workinapp?characterEncoding=UTF-8";
	String user="root";
	String password="1234";
	Connection con;
	
	public NoticeForm(AppMain appMain, MemberMain memberMain, ListMain listMain) {
		this.appMain = appMain;
		this.memberMain = memberMain;
		this.listMain = listMain;
		
		connect();
		
		//생성
		p_center = new JPanel();
		la_title = new JLabel("신규 게시물 작성");
		cb_level = new JComboBox<String>(level);
		t_title = new JTextField();
		t_area = new JTextArea();
		bt_file = new JButton("파일 첨부");
		bt_regist = new JButton("등록");
		bt_cancel = new JButton("취소");
		chooser = new JFileChooser("C:\\Users\\master\\Desktop\\workinTestFile");
		la_file_name = new JLabel("첨부파일 : ");
		la_file = new JLabel("-----------");
		
		//스타일 및 레이아웃
		setLayout(new BorderLayout());
		la_title.setPreferredSize(new Dimension(650,30));
		la_title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		cb_level.setPreferredSize(new Dimension(150,35));
		t_title.setPreferredSize(new Dimension(500,35));
		t_area.setPreferredSize(new Dimension(650,350));
		
		//조립
		p_center.add(la_title);
		p_center.add(cb_level);
		p_center.add(t_title);
		p_center.add(t_area);
		p_center.add(la_file_name);
		p_center.add(la_file);
		p_center.add(bt_file);
		p_center.add(bt_regist);
		p_center.add(bt_cancel);
		add(p_center);
		
		//이벤트		
		bt_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disConnect();
				NoticeForm.this.setVisible(false);
			}
		});
		
		bt_regist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				regist();
				NoticeForm.this.setVisible(false);
				NoticeForm.this.listMain.getNoticeList();
				NoticeForm.this.listMain.getImportantList();
			}
		});
		
		//파일 첨부 (파일 복사 및 DB에 저장)
		bt_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				selectFile();
			}
		});
		
		//보여주기
		setSize(720,520);
		setBackground(Color.WHITE);
		setLocationRelativeTo(null);
	}
	
	public void regist() {
		String name = appMain.getMember().getUser_name();
		String level = cb_level.getSelectedItem().toString();
		PreparedStatement pstmt = null;
		
		String sql = "insert into notice(title, level, writer, content, filename)";
		sql += " values(?,'"+level+"','"+name+"',?,?)";

		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, t_title.getText());
			pstmt.setString(2, t_area.getText());
			pstmt.setString(3, filename);
			
			int result = pstmt.executeUpdate();
			if(result ==1) {
				JOptionPane.showMessageDialog(this, "일정 등록 완료");
			}else {
				JOptionPane.showMessageDialog(this, "일정 등록 실패");				
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			release(pstmt);
		}
	}
	
	public void selectFile() {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		if(chooser.showOpenDialog(this)==JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			String text = file.getName();
			try {
				fis = new FileInputStream(file);
				long time = System.currentTimeMillis();
				filename = time+"."+FileManager.getExtend(file.getAbsolutePath(),"\\");
				fos = new FileOutputStream("D:\\Korea202102_javaworkspace\\WorkinApp\\data\\"+filename);
				
				//파일 복사
				int data = -1;
				byte[] buff = new byte[1024];
				while(true) {
					data = fis.read(buff);
					if(data == -1)break;
					fos.write(buff);
				}
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally {
				if(fos!=null) {
					try {
						fos.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
				if(fis!=null) {
					try {
						fis.close();
					} catch (IOException e) {
						e.printStackTrace();
					}
				}			
			}
			la_file.setText(text);
		}
	}
	
	public void connect() {
		try {
			Class.forName(driver); //드라이버 로드 
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
}
