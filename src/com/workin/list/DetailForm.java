package com.workin.list;

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
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileSystemView;

public class DetailForm extends JFrame{
	ListMain listMain;
	
	JPanel p_center;
	JLabel la_title;
	JTextField t_title;
	JLabel la_level;
	JTextField t_level;
	JLabel la_writer;
	JTextField t_writer;
	JTextArea t_area;
	JScrollPane scroll;
	JButton bt_file, bt_edit, bt_del, bt_cancel;
	JLabel la_file_name;
	JLabel la_file;

	String notice_id;
	JFileChooser chooser;
	String filename;
	
	//데이터베스 관련 
	String driver="com.mysql.jdbc.Driver"; // 8.xx 인 경우 com.mysql.jdbc.cj.Driver
	String url="jdbc:mysql://localhost:3306/workinapp?characterEncoding=UTF-8";
	String user="root";
	String password="1234";
	Connection con;
	
	public DetailForm(String notice_id, ListMain listMain) {
		connect();
		this.notice_id = notice_id;
		this.listMain = listMain;
		//생성
		p_center = new JPanel();
		la_title = new JLabel(" 제목");
		t_title = new JTextField();
		la_level = new JLabel(" 중요도");
		t_level = new JTextField();
		la_writer = new JLabel(" 작성자");
		t_writer = new JTextField();
		t_area = new JTextArea();
		scroll = new JScrollPane(t_area);
		bt_file = new JButton("파일 다운로드");
		bt_edit = new JButton("수정");
		bt_del = new JButton("삭제");
		bt_cancel = new JButton("취소");
		la_file_name = new JLabel("첨부파일 : ");
		la_file = new JLabel("파일 이름올라갈 곳");
		chooser = new JFileChooser("C:\\Users\\master\\Desktop\\Workin에서 받은 파일");
		
		
		//스타일 및 레이아웃
		p_center.setPreferredSize(new Dimension(720,520));
		p_center.setBackground(Color.WHITE);
		la_title.setPreferredSize(new Dimension(100,30));
		la_title.setOpaque(true);
		la_title.setBackground(Color.DARK_GRAY);
		la_title.setForeground(Color.WHITE);
		la_title.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		t_title.setPreferredSize(new Dimension(220,30));
		la_level.setPreferredSize(new Dimension(100,30));
		la_level.setOpaque(true);
		la_level.setBackground(Color.DARK_GRAY);
		la_level.setForeground(Color.WHITE);
		la_level.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		t_level.setPreferredSize(new Dimension(220,30));
		la_writer.setPreferredSize(new Dimension(100,30));
		la_writer.setOpaque(true);
		la_writer.setBackground(Color.DARK_GRAY);
		la_writer.setForeground(Color.WHITE);
		la_writer.setFont(new Font("맑은 고딕", Font.BOLD, 12));
		t_writer.setPreferredSize(new Dimension(550,30));
		t_area.setPreferredSize(new Dimension(655,350));
		
		//이벤트
		bt_cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				disConnect(); //DB 접속해제
				DetailForm.this.setVisible(false);
			}
		});
		
		bt_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				saveFile();
			}
		});
		
		bt_edit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				editData();
				DetailForm.this.setVisible(false);
				DetailForm.this.listMain.getNoticeList();
				DetailForm.this.listMain.getImportantList();
			}
		});
		
		bt_del.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				delData();
				DetailForm.this.setVisible(false);
				DetailForm.this.listMain.getNoticeList();
				DetailForm.this.listMain.getImportantList();
			}
		});
		
		//조립
		p_center.add(la_title);
		p_center.add(t_title);
		p_center.add(la_level);
		p_center.add(t_level);
		p_center.add(la_writer);
		p_center.add(t_writer);
		p_center.add(scroll);
		p_center.add(la_file_name);
		p_center.add(la_file);
		p_center.add(bt_file);
		p_center.add(bt_edit);
		p_center.add(bt_del);
		p_center.add(bt_cancel);
		add(p_center);
		
		//보여주기
		setSize(720,520);
		setLocationRelativeTo(null);
		
		getData();
	}
	
	public void getData() {
		String sql = "select title, level, writer, content, filename from notice where notice_id=" + notice_id;
		
		PreparedStatement pstmt = null;
		ResultSet rs =null;
		
		try {
			pstmt = con.prepareStatement(sql);
			rs = pstmt.executeQuery();
			
			if(rs.next()) {
				t_title.setText(rs.getString("title"));
				t_level.setText(rs.getString("level"));
				t_writer.setText(rs.getString("writer"));
				t_area.setText(rs.getString("content"));
				la_file.setText(rs.getString("filename"));
				filename = rs.getString("filename");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}finally {
			release(pstmt, rs);
		}
		System.out.println(filename);
	}
	
	//파일 다운로드(로컬파일에 저장)
	public void saveFile() {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		
		chooser.setSelectedFile(new File(filename));
		//String path ="D:\\Korea202102_javaworkspace\\WorkinApp\\data"+filename;
		if(chooser.showSaveDialog(this)==JFileChooser.APPROVE_OPTION) {
			File file = new File("D:\\Korea202102_javaworkspace\\WorkinApp\\data",filename);
			try {
				fis = new FileInputStream(file);
				fos = new FileOutputStream("C:\\Users\\master\\Desktop\\Workin에서 받은 파일\\"+ filename);
				
				//파일 복사
				int data = -1;
				byte[] buff = new byte[1024];
				while(true) {
					data = fis.read(buff);
					if(data == -1)break;
					fos.write(buff);
				}
				JOptionPane.showMessageDialog(this, "다운로드 완료");
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
		}
		//System.out.println("여기맞아?" + fis);
	}
	
	public void editData() {
		PreparedStatement pstmt = null;
		String sql = "update notice set title = ?, content =? where notice_id="+notice_id;
		String title = t_title.getText();
		String content = t_area.getText();
		
		try {
			pstmt = con.prepareStatement(sql);
			pstmt.setString(1, title);
			pstmt.setString(2, content);
			int result = pstmt.executeUpdate();
			
			if(result == 1) {
				JOptionPane.showMessageDialog(this, "수정 완료");
			}else {
				JOptionPane.showMessageDialog(this, "수정 실패");
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
		
	}
	
	public void delData() {
		int result = JOptionPane.showConfirmDialog(this, "삭제하시겠습니까?","삭제", JOptionPane.YES_NO_OPTION);
		
		if(result == JOptionPane.YES_OPTION) {
			PreparedStatement pstmt = null;
			String sql = "delete from notice where notice_id="+notice_id;
			
			try {
				pstmt = con.prepareStatement(sql);
				int result2 = pstmt.executeUpdate();
				if(result2 == 1) {
					JOptionPane.showMessageDialog(this, "삭제 완료");
				}else {
					JOptionPane.showMessageDialog(this, "삭제 실패");
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
