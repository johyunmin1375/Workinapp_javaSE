package com.workin.chat;

import java.awt.BorderLayout;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import com.workin.main.AppMain;


public class ChatServer extends JFrame{
	JPanel p_north;
	JTextField t_port;
	JButton bt;
	JTextArea area;
	JScrollPane scroll;
	
	//서버관련
	Vector<ServerMsgThread> clientList = new Vector<ServerMsgThread>();
	ServerSocket server;
	Thread serverThread;
	boolean serverFlag=true;
	
	//채팅관련
	AppMain appMain;
	ChatSelect chatSelect;
	int online;
	
	public ChatServer() {
		//생성 
		p_north = new JPanel();
		t_port = new JTextField("7777",10);
		bt = new JButton("서버가동");
		area = new JTextArea();
		scroll  =new JScrollPane(area);
		
		t_port.setEditable(false);
		area.setEditable(false);
		
		//add
		p_north.add(t_port);
		p_north.add(bt);
		add(p_north, BorderLayout.NORTH);
		add(scroll);
		
		//event 
//		bt.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent e) {
				serverThread = new Thread() {
					public void run() {
						runServer();
					}
				};
				serverThread.start();
//			}
//		});
		
		
		//view
		setVisible(true);
		setBounds(600, 300, 300, 400);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
	}
	
	
	public void runServer() {
		int port=Integer.parseInt(t_port.getText());
		try {
			server = new ServerSocket(port);
			area.append("서버가동\n");
			while(serverFlag) {
				Socket socket = server.accept(); //대기 상태에 빠지는 코드는 절대로 메인쓰레드로 처리하지 말자!!
				String ip=socket.getInetAddress().getHostAddress();
				area.append(ip+"접속 발견\n");
				ServerMsgThread smt = new ServerMsgThread( socket , this);//메시지 담당 쓰레드 객체 생성 후, 벡터 명단에 추가!!!
				smt.start(); //열심히 듣고, 말하기 시작~~~~//
				clientList.add(smt); //명단에 넣기
				
				area.append("현재 접속자 수는 "+clientList.size()+"\n");//로그 남기기
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	



	public static void main(String[] args) {
		new ChatServer();
	}
}