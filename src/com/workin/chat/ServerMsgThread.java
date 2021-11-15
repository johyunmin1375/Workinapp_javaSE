package com.workin.chat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.workin.main.AppMain;
import com.workin.model.domain.Member;


public class ServerMsgThread extends Thread{
	Socket socket;
	ChatServer chatServer;
	BufferedReader buffr;
	BufferedWriter buffw;
	boolean flag = true;
	
	AppMain appMain;
	Member member; //회원정보를 보관할 객체
	ClientMsgThread clientThread;
	
	public ServerMsgThread(Socket socket, ChatServer chatServer) {
		this.socket=socket;
		this.chatServer=chatServer;

		try {
			buffr= new BufferedReader(new InputStreamReader(socket.getInputStream()));
			buffw = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		} catch (IOException e) {
			e.printStackTrace();
		}		
	};

	
	public void listen() {
		String msg=null;
		
		try {
			msg= buffr.readLine();// 클라이언트가 보낸 msg 이 포함된 스트링빌더(=sb(String))를 받아와 파싱을 한다
			//JSON 파싱 시작!!!
			JSONParser jsonParser=new JSONParser();
			try {
				JSONObject packet=(JSONObject)jsonParser.parse(msg);
				System.out.println("서버스레드 리슨 "+packet);
				String cmd = (String)packet.get("cmd");
				
				if(cmd.equals("login")) { //로그인 정보가 전송되어 오면
					chatServer.area.append("클라이언트가 로그인 정보를 보냈습니다\n");
					member = new Member(); //empty 상태의 VO 생성  
					
					//member VO에 정보 입력
					JSONObject obj=(JSONObject)packet.get("member");
					member.setMember_id(Integer.parseInt((String)obj.get("member_id")));  
					member.setUser_name((String)obj.get("user_name")); 
					member.setUser_id((String)obj.get("user_id")); 
					member.setUser_pass((String)obj.get("user_pass")); 
					member.setRegdate((String)obj.get("regdate")); 
					member.setImg((String)obj.get("img"));
	
					StringBuilder sb = new StringBuilder();
					//System.out.println(chatServer.clientList.size());
					
					sb.append("{");
					sb.append("\"cmd\" : \"List\",");
					sb.append("\"memberList\":[");
					for(int i=0; i<chatServer.clientList.size(); i++) { //벡터의 크기만큼 돌리라구요 
						Member member = chatServer.clientList.get(i).member;
				
						sb.append("{");
						sb.append("\"member_id\" :  \""+member.getMember_id()+"\",");
						sb.append("\"user_name\" :\""+member.getUser_name()+"\",");
						sb.append("\"user_id\" :  \""+member.getUser_id()+"\",");
						sb.append("\"user_pass\" :  \""+member.getUser_pass()+"\",");
						sb.append("\"regdate\" :  \""+member.getRegdate()+"\",");
						sb.append("\"img\" :  \""+member.getImg()+"\"");
						if(i<chatServer.clientList.size()-1) {
							sb.append("},");
						}else {
							sb.append("}");
						}
					}
					sb.append("]");
					sb.append("}");	
					
					for(int i=0;i<chatServer.clientList.size();i++) {
						ServerMsgThread smt=chatServer.clientList.get(i);
						smt.send(sb.toString()); //broadcasing..
					}
					
					System.out.println(sb.toString());
					
					
				}else if(cmd.equals("chat")) { //대화의 메시지가 전송되어 오면 
					String message = (String)packet.get("message");
					String user_name = (String)packet.get("user_name");
					StringBuilder sb = new StringBuilder();
					sb.append("{");
					sb.append("\"cmd\":\"chat\", ");
					sb.append("\"user_name\": \""+user_name+"\"");
					sb.append("\"message\": \""+message+"\"");
					sb.append("}");
					

					//대화일때 broadcasting !!!
					for(int i=0;i<chatServer.clientList.size();i++) {
						ServerMsgThread msgThread=chatServer.clientList.get(i);
						msgThread.send(sb.toString());
					}
					chatServer.area.append(member.getUser_name()+"의 말:"+message+"\n");//area에 로그 남기기 
				}
				
			} catch (ParseException e) {
				e.printStackTrace();
			}
			
			
		} catch (IOException e) {
//			e.printStackTrace();
			flag=false; //쓰레드 dead 상태로 두기 
			chatServer.clientList.remove(this);//명단에서도 제거
			chatServer.area.append("현재까지 참여자 수 : "+chatServer.clientList.size()+"\n");
		}
		
	}
	
	//말하기
	public void send(String msg) {
		try {
			buffw.write(msg+"\n");
			buffw.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void chat() {
		
	}
	

		//듣고,말하는 행위는 서버이기에 끝없이!!! 즉 쓰레드가 절대 죽으면 안됨 
	public void run() {
		while(flag) {
			listen();
		}
	}
	
}