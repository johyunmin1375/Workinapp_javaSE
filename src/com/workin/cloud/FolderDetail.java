package com.workin.cloud;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class FolderDetail extends JFrame implements ActionListener{
	CloudMain cloudMain;
	FileDetail fileDetail;
	CustomFolder customFolder; 
	String folder_name;
	String filename;
	
	JPanel p_center;
	JLabel la_title;
	JButton bt_file;
	JButton bt_back;
	
	JPanel p_file;	//파일영역
	JLabel la_file;	//파일
	CustomFile[] customFile = {};
	int fileCount; // 버튼을 누적시킬 변수
	
	JFileChooser chooser;
	
	public FolderDetail(CloudMain cloudMain, CustomFolder customFolder) {
		this.cloudMain = cloudMain;
		this.customFolder = customFolder;
		folder_name = this.customFolder.getFolder_name();
		//생성
		p_center = new JPanel();
		la_title = new JLabel("클라우드 - " + folder_name);
		bt_file = new JButton("파일추가");	
		bt_back = new JButton("뒤로가기");
		p_file = new JPanel();
		la_file = new JLabel("파일");
		chooser = new JFileChooser("C:\\Users\\master\\Desktop\\workinTestFile");
		//디자인 및 레이아웃
		p_center.setPreferredSize(new Dimension(1000,630));
		p_center.setBackground(Color.WHITE);
		la_title.setPreferredSize(new Dimension(805,30));
		la_title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		bt_file.setPreferredSize(new Dimension(80,30));
		bt_back.setPreferredSize(new Dimension(80,30));
		
		//파일
		p_file.setLayout(new FlowLayout(FlowLayout.LEFT));
		p_file.setPreferredSize(new Dimension(980,650));
		p_file.setBackground(Color.WHITE);
		la_file.setPreferredSize(new Dimension(980,20));
		la_file.setFont(new Font("맑은 고딕", Font.BOLD, 16));
				
		//조립
		p_center.add(la_title);
		p_center.add(bt_file);
		p_center.add(bt_back);
		p_center.add(la_file);
		p_center.add(p_file);
		add(p_center);
		
		//이벤트
		bt_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				uploadFile();
				getFileList();
			}
		});
		
		bt_back.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				FolderDetail.this.setVisible(false);
			}
		});
		
		//보여주기
		setUndecorated(true);
		setLocation(590,185);
		setSize(1000, 625);
		setVisible(true);
		
		getFileList();
	}
	
	public void getFileList() {	
		PreparedStatement pstmt =null;
		ResultSet rs = null;
		
		String sql = "select file_name, regdate, writer from file where folder_id = " + customFolder.getFolder_id() ;
		String writer = cloudMain.appMain.getMember().getUser_name();
		try {
			pstmt = cloudMain.getAppMain().getMemberMain().getCon().prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			rs.last(); //커서를 맨 밑으로
			fileCount = rs.getRow(); // 총개수를 구함
			System.out.println("파일의 갯수는 "+fileCount);
			p_file.removeAll(); //패널의 자식 컴포넌트를 모두 삭제
			rs.beforeFirst(); // file_name을 추출하기 위해 커서를 다시 맨위로!! 
			
			for(int i =0; i<fileCount; i++) {
				customFile  = new CustomFile[fileCount];
				if(rs.next()) {
					customFile[i] = new CustomFile(rs.getString("file_name"));
					customFile[i].setFile_name(rs.getString("file_name"));
					customFile[i].setRegdate(rs.getString("regdate"));
					customFile[i].setWriter(writer);
				}
				customFile[i].setId(i);
				customFile[i].setPreferredSize(new Dimension(190,40));
				customFile[i].setFocusPainted(false);
				customFile[i].setContentAreaFilled(false);
				customFile[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));		
				customFile[i].addActionListener(this);
				//System.out.println(customFile[i].getId());
				p_file.add(customFile[i]);
			}
			p_file.updateUI();	//UI 업데이트!!
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			cloudMain.getAppMain().getMemberMain().release(pstmt, rs);
		}
	}
	
	public void uploadFile() {
		if(fileCount < 20) {		
			FileInputStream fis = null;
			FileOutputStream fos = null;
			
			PreparedStatement pstmt = null;
			String sql ="insert into file(folder_id, file_name, writer)";
			sql += " values(?, ?, ?)";
			
			if(chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
				File file = chooser.getSelectedFile();
				
				try {
					fis = new FileInputStream(file);
					long time = System.currentTimeMillis();
					filename = time + "." + file.getName();
					fos = new FileOutputStream("D:\\Korea202102_javaworkspace\\WorkinApp\\cloud\\"+filename);
					
					//파일 복사
					int data = -1;
					byte[] buff = new byte[1024];
					while(true) {
						data = fis.read(buff);
						if(data == -1)break;
						fos.write(buff);
					}
					
					pstmt = cloudMain.getAppMain().getMemberMain().getCon().prepareStatement(sql);
					pstmt.setInt(1, customFolder.getFolder_id());
					pstmt.setString(2, filename);
					pstmt.setString(3, cloudMain.appMain.getMember().getUser_name());
					
					pstmt.executeUpdate();
					JOptionPane.showMessageDialog(this, "업로드 완료");
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}catch (SQLException e) {
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
		}else {
			JOptionPane.showMessageDialog(this, "파일은 최대 20개까지 업로드 가능");
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		CustomFile bt = (CustomFile)obj;
		fileDetail = new FileDetail(bt);
	}
}
