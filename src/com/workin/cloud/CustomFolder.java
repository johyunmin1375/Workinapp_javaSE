package com.workin.cloud;

import javax.swing.JButton;

public class CustomFolder extends JButton{
	private int folder_id;
	private String folder_name;
	
	public CustomFolder(String title) {
		super(title);
	}


	public int getFolder_id() {
		return folder_id;
	}


	public void setFolder_id(int folder_id) {
		this.folder_id = folder_id;
	}



	public String getFolder_name() {
		return folder_name;
	}

	public void setFolder_name(String folder_name) {
		this.folder_name = folder_name;
	}

	
}
