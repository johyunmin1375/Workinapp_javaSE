package com.workin.chat;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.Socket;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.workin.main.AppMain;



public class ChatClient extends JFrame {
	// 상단부분
	JPanel p_north;
	JPanel p_menu;

	JLabel la_center;

	JPanel p_exit;
	JLabel la_exit;
	JLabel la_back;

	// 센터부분
	JTextArea area;
	JScrollPane scroll;

	// 하단부분
	JPanel p_south;
	JPanel p_file;
	JTextField t_input;
	JButton bt_send;
	
	ImageBox imageBox;
	ImageBox imagebox1;
	ImageBox imagebox2;
	ImageBox imagebox3;

	Socket socket;
	ClientMsgThread msgThread;
	AppMain appMain;
	
	Image img;
	
	boolean serverFlag = true;
	protected int yDrag;
	protected int xDrag;
	protected int xPress;
	protected int yPress;

	
	public ChatClient(AppMain appMain) {
		this.appMain = appMain;
		// 생성

		// 상단부분
		p_north = new JPanel();
		p_menu = new JPanel();
		imagebox1 = new ImageBox(new Color(44, 62, 80), 30,20, new ImageIcon(getIcon("menu.png")).getImage());
		

		la_center = new JLabel("오픈채팅방");
		la_center.setHorizontalAlignment(JLabel.CENTER);
		p_exit = new JPanel();
		imagebox2 = new ImageBox(new Color(44, 62, 80), 30,20, new ImageIcon(getIcon("exit.png")).getImage());

		// 센터부분
		area = new JTextArea();
		area.setEditable(false);
		scroll = new JScrollPane(area);
		

		// 하단부분
		Color b = new Color(44, 62, 80);
		p_south = new JPanel();
		p_file = new JPanel();
		imagebox3 = new ImageBox(new Color(44, 62, 80), 30,20, new ImageIcon(getIcon("file.png")).getImage());
		t_input = new JTextField(15);
		bt_send = new JButton("전송");

		// 레이아웃,스타일
		p_north.setBackground(b);
		p_menu.setBackground(b);
		la_center.setFont(new Font("Serif", Font.BOLD, 18));
		la_center.setForeground(Color.WHITE);
		p_exit.setBackground(b);
		p_south.setBackground(b);
		t_input.setPreferredSize(new Dimension(30, 27));
		p_file.setBackground(b);

		// 조립
		p_menu.add(imagebox1);
		p_exit.add(imagebox2);
		p_north.setLayout(new BorderLayout());
		p_north.add(p_menu, BorderLayout.WEST);
		p_north.add(la_center);
		p_north.add(p_exit, BorderLayout.EAST);
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		p_file.add(imagebox3);
		p_south.add(p_file);
		p_south.add(t_input);
		p_south.add(bt_send);
		add(p_south, BorderLayout.SOUTH);

		
		//닫으면 setvisible만 false
		this.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				setVisible(false);
			}
		});
		
		imagebox2.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				setVisible(false);
			}
		});
		
		//챗클라이언트는 더미일뿐 서버와 통신하는건 메인프레임인 자신 //메시지 입력 엔터처리 
		t_input.addKeyListener(new KeyAdapter() {
			public void keyReleased(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) { //엔터치면..
					String msg =t_input.getText();
					appMain.sendMsg(msg);
					t_input.setText("");
				}
			}
		});
		//챗클라이언트는 더미일뿐 서버와 통신하는건 메인프레임인 자신 //메시지 입력 버튼처리 
		bt_send.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String msg =t_input.getText();
				appMain.sendMsg(msg);
				t_input.setText("");
			}
		});

		//화면 옮기기
		p_north.addMouseListener(new MouseAdapter() {
			public void mouseDragged(MouseEvent e) {
				xDrag = e.getX();
			    yDrag = e.getY();
			    JFrame sFrame = (JFrame) e.getSource();
			    sFrame.setLocation(sFrame.getLocation().x+xDrag-xPress, 
			    sFrame.getLocation().y+yDrag-yPress);
			}
			
			public void mouseMoved(MouseEvent e) {
				xPress = e.getX();
				yPress = e.getY();
			}
		});
		
		// 보이기
		setResizable(false);
		setVisible(false);
		setBounds(800, 300, 330, 500);
//		setUndecorated(true);
//		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
	}
	

	
	public Image getIcon(String filename) {
		URL url = this.getClass().getClassLoader().getResource(filename);
		ImageIcon icon=new ImageIcon(url);
		return icon.getImage();
	}

}