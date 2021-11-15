package com.workin.cloud;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
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
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import com.workin.main.AppMain;
import com.workin.main.CustomButton;
import com.workin.main.Page;

public class CloudMain extends Page implements ActionListener{
	AppMain appMain;
	FileDetail fileDetail;
	FolderDetail folderDetail;
	FolderForm folderForm;
	
	JPanel p_center;
	JLabel la_title;	//클라우드
	JButton bt_folder;	//폴더생성버튼
	JButton bt_file;	//파일생성버튼
	JPanel p_folder;	//폴더 영역
	JLabel la_folder;	//폴더
	
	JPanel p_file;	//파일영역
	JLabel la_file;	//파일
	
	CustomFolder[] customFolder = {};
	int folderCount; // 폴더를 누적시킬 변수
	CustomFile[] customFile = {};
	int fileCount; // 버튼을 누적시킬 변수

	String filename;
	JFileChooser chooser;
	public CloudMain(AppMain appMain) {
		super(appMain);
		this.appMain = appMain;
		//생성
		p_center = new JPanel();
		la_title = new JLabel("클라우드");
		bt_folder = new JButton("폴더생성");
		bt_file = new JButton("파일추가");		
		p_folder = new JPanel();
		la_folder = new JLabel("폴더");					
		
		//파일 생성
		p_file = new JPanel();
		la_file = new JLabel("파일");
		chooser = new JFileChooser("C:\\Users\\master\\Desktop\\workinTestFile");
		//스타일 및 레이아웃
		p_center.setPreferredSize(new Dimension(1020, 680));
		p_center.setBackground(Color.WHITE);
		la_title.setPreferredSize(new Dimension(815,30));
		la_title.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		bt_folder.setPreferredSize(new Dimension(80,30));
		bt_file.setPreferredSize(new Dimension(80,30));
		
		//폴더
		p_folder.setLayout(new FlowLayout(FlowLayout.LEFT));
		p_folder.setPreferredSize(new Dimension(980,150));
		p_folder.setBackground(Color.WHITE);
		la_folder.setPreferredSize(new Dimension(980,20));
		la_folder.setFont(new Font("맑은 고딕", Font.BOLD, 16));
		
		//파일
		p_file.setLayout(new FlowLayout(FlowLayout.LEFT));
		p_file.setPreferredSize(new Dimension(980,350));
		p_file.setBackground(Color.WHITE);
		la_file.setPreferredSize(new Dimension(980,20));
		la_file.setFont(new Font("맑은 고딕", Font.BOLD, 16));

		//조립
		p_center.add(la_title);
		p_center.add(bt_folder);
		p_center.add(bt_file);

		p_center.add(la_folder);
		p_center.add(p_folder);
		
		p_center.add(la_file);
		p_center.add(p_file);
		
//		for(JButton bt2 : customFile) {
//			p_file.add(bt2);
//		}
		add(p_center);
		
		//이벤트
		bt_file.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CloudMain.this.uploadFile();
				CloudMain.this.getFileList();
			}
		});
		
		bt_folder.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				CloudMain.this.openDialog();
			}
		});
		
		getFolderList();
		getFileList();
	}
	
	public void getFileList() {	
		PreparedStatement pstmt =null;
		ResultSet rs = null;
		
		String sql = "select file_name, regdate, writer from file where folder_id is null" ;
		String writer = appMain.getMember().getUser_name();
		try {
			pstmt = this.getAppMain().getMemberMain().getCon().prepareStatement(sql);
			
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
			this.getAppMain().getMemberMain().release(pstmt, rs);
		}
	}
	
	public void uploadFile() {
		if(fileCount < 20) {		
			FileInputStream fis = null;
			FileOutputStream fos = null;
			
			PreparedStatement pstmt = null;
			String sql ="insert into file(file_name, writer)";
			sql += " values(?, ?)";
			
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
					
					pstmt = this.getAppMain().getMemberMain().getCon().prepareStatement(sql);
					pstmt.setString(1, filename);
					pstmt.setString(2, this.appMain.getMember().getUser_name());
					
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
	
	public void getFolderList() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select folder_id, folder_name from folder";
		
		try {
			pstmt =this.getAppMain().getMemberMain().getCon().prepareStatement(sql);
			
			rs = pstmt.executeQuery();
			rs.last();
			folderCount = rs.getRow();
			p_folder.removeAll();
			rs.beforeFirst();
			
			for(int i =0; i<folderCount; i++) {
				customFolder = new CustomFolder[folderCount];
				if(rs.next()) {
					customFolder[i] = new CustomFolder(rs.getString("folder_name"));
					customFolder[i].setFolder_name(rs.getString("folder_name"));		
					customFolder[i].setFolder_id(i+1);
					customFolder[i].setPreferredSize(new Dimension(190,40));
					customFolder[i].setFocusPainted(false);
					customFolder[i].setContentAreaFilled(false);
					customFolder[i].setBorder(BorderFactory.createLineBorder(Color.GRAY));
					
					customFolder[i].addActionListener(new ActionListener() {
						public void actionPerformed(ActionEvent e) {
							CustomFolder obj = (CustomFolder)e.getSource();
							
							System.out.println(obj.getFolder_id());
							folderDetail = new FolderDetail(CloudMain.this, obj);
						}
					});
				}
				p_folder.add(customFolder[i]);
			}
			p_folder.updateUI();
		} catch (SQLException e1) {
			e1.printStackTrace();
		}finally {
			this.getAppMain().getMemberMain().release(pstmt, rs);
		}
	
	}
	
	public void openDialog() {
		folderForm = new FolderForm(this); 
	}

	public void createFolder() {
		String folder_name = folderForm.t_title.getText();
		PreparedStatement pstmt = null;
		String sql ="insert into folder(folder_name)";
		sql += " values(?)";
		
		try {
			pstmt = this.getAppMain().getMemberMain().getCon().prepareStatement(sql);
			pstmt.setString(1, folder_name);
			
			pstmt.executeUpdate();
			JOptionPane.showMessageDialog(this, "폴더 생성 완료");
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			this.getAppMain().getMemberMain().release(pstmt);
		}

	}
	@Override
	public void actionPerformed(ActionEvent e) {
		Object obj = e.getSource();
		CustomFile bt = (CustomFile)obj;
		fileDetail = new FileDetail(bt);
	}
}