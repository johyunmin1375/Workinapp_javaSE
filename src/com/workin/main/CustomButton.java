package com.workin.main;

import javax.swing.JButton;

public class CustomButton extends JButton{
	private int id;
	
	public CustomButton(String title) {
		super(title);	//부모의 생성자는 물려받지 않으므로 부모의  생성자를 자식이 호출해야 함
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
}
