package com.workin.cloud;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JOptionPane;

public class CustomFile extends JButton{
	private int id;
	private String file_name;
	private String regdate;
	private String writer;
	
	public CustomFile(String title) {
		super(title);

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getFile_name() {
		return file_name;
	}
	
	public void setFile_name(String file_name) {
		this.file_name = file_name;
	}
	
	public String getRegdate() {
		return regdate;
	}
	
	public void setRegdate(String regdate) {
		this.regdate = regdate;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}
	
	public void downLoadFile() {
		FileInputStream fis = null;
		FileOutputStream fos = null;
		int result = JOptionPane.showConfirmDialog(this, this.getFile_name() +" 다운로드 하시겠습니까?","다운로드", JOptionPane.YES_NO_OPTION);
		
		if(result == JOptionPane.YES_OPTION) {
			File file = new File("D:\\Korea202102_javaworkspace\\WorkinApp\\cloud\\"+file_name);
			try {
				fis = new FileInputStream(file);
				fos = new FileOutputStream("C:\\Users\\master\\Desktop\\Workin에서 받은 파일\\"+ file_name);
				
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
			}catch (IOException e) {
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
	}
}
