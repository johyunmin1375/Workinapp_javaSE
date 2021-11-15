package com.workin.member;

import java.awt.Dimension;

import javax.swing.JPanel;

public class PageControl extends JPanel{
	private MemberMain memberMain;
	
	public MemberMain getMemberMain() {
		return memberMain;
	}

	public PageControl(MemberMain memberMain) {
		this.memberMain = memberMain;
		
		setPreferredSize(new Dimension(420, 420));
		setVisible(false);
	}
}
