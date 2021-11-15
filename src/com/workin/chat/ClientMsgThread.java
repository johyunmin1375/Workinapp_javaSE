package com.workin.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Vector;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.workin.main.AppMain;
import com.workin.model.domain.Member;


public class ClientMsgThread extends Thread{
	Member member;
	AppMain appMain;
	Socket socket;
	BufferedReader buffr;
	BufferedWriter buffw;
	public boolean flag = true;
	Vector<Member> memberList = new Vector<Member>();
	
	public ClientMsgThread(Socket socket, AppMain appMain) {
		this.socket = socket;
		this.appMain =appMain;
		
		try {
			buffr= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}	
	}
	
	//끊임없이 듣기 위한 리슨객체
	public void listen() {
		String msg=null;
		try {
			msg= buffr.readLine();
			
			JSONParser jsonParser=new JSONParser();
			try {
				System.out.println("클라이언트 msg 스레드 리슨정보"+msg);
				JSONObject packet=(JSONObject)jsonParser.parse(msg);
				String cmd = (String)packet.get("cmd");

				
				if(cmd.equals("List")) { //로그인 정보가 전송되어 오면 정보를 →VO→Vector에 담아 보내자
					JSONArray memberArray=(JSONArray)packet.get("memberList");
					memberList.removeAll(memberList); //기ㅗㅈㄴ 데이터 지우기
					
					for(int i=0;i<memberArray.size();i++) {
						member = new Member(); //empty 
						JSONObject obj=(JSONObject)memberArray.get(i);
						
						member.setMember_id(Integer.parseInt((String)obj.get("member_id"))); 
						member.setUser_name((String)obj.get("user_name")); 
						member.setUser_id((String)obj.get("user_id")); 
						member.setUser_pass((String)obj.get("user_pass")); 
						member.setRegdate((String)obj.get("regdate")); 
						member.setImg((String)obj.get("img")); 
		
						memberList.add(member);			
					}
					appMain.savemain(memberList);
					appMain.chatSelect.onlinecount(memberList);						

				}else if(cmd.equals("chat")) { //채팅정보가 전송되어 오면
					String message = (String)packet.get("message");
					String user_name = (String)packet.get("user_name");
					appMain.chatClient.area.append(user_name+"님의 말 : "+message+"\n");				
					
				}

				
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} catch (IOException e) {
			//e.printStackTrace();
			flag=false; //쓰레드 dead 상태로 두기 
		}
	}
	
	
	public void send(String msg) {
		try {
			System.out.println("클라이언트 msg 스레드 샌드정보"+msg);
			buffw.write(msg+"\n");
			buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	
	//듣고,말하는 행위는 서버이기에 끝없이!!! 즉 쓰레드가 절대 죽으면 안됨 
		public void run() {
			while(flag) {
				listen();
			}
			//쓰레드 종료에 따른, 스트림 닫기
			if(buffw!=null) {
				try {
					buffw.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			if(buffr!=null) {
				try {
					buffr.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}


}