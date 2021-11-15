package com.workin.list;

import java.awt.BorderLayout;
import java.awt.Choice;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import com.workin.main.AppMain;
import com.workin.main.Page;
import com.workin.member.MemberMain;

public class ListMain extends Page{
	AppMain appMain;
	MemberMain memberMain;
	NoticeForm form;
	DetailForm detailForm;
	
	//센터관련
	JPanel p_center; // 검색창 게시판 영역
	JPanel p_search; //검색 컴포넌트들 올려놓을 패널
	Choice ch_category;//검색 카테고리 선택
	JTextField t_keyword;//검색어 입력
	JButton bt_search; 
	JTable table;
	JScrollPane scroll_table; 
	JButton bt_regist;	
	
	//동쪽관련
	JPanel p_east; //공지사항 영역
	JLabel la_notice;
	JPanel p_notice;
	JTable importantTable;
	JScrollPane scroll_table2; 
	
	//데이터베이스 관련
	Date date;
	SimpleDateFormat dateFormat;
	String regdate;
	
	String[] columns = {"No","제목","중요도","작성자","내용","작성일","첨부파일"};
	String[][] records = {};
	String[] columns2 = {"제목","중요도","작성자","내용"};
	String[][] records2 = {};
	
	public ListMain(AppMain appMain, MemberMain memberMain) {
		super(appMain);
		this.appMain = appMain;
		this.memberMain = memberMain;
		
		//생성
		p_center = new JPanel();
		p_search = new JPanel();
		ch_category = new Choice();
		t_keyword = new JTextField();
		bt_search = new JButton("검색");
		//전체 게시판
		table = new JTable(new AbstractTableModel() {
			public int getRowCount() {
				return records.length;
			}
			
			public int getColumnCount() {
				return columns.length;
			}

			//컬럼의 제목을 배열로부터 구한다
			public String getColumnName(int col) {
				return columns[col];
			}
			
			public Object getValueAt(int row, int col) {
				return records[row][col];
			}
		});
		//공지게시판
		importantTable = new JTable(new AbstractTableModel() {
			public int getRowCount() {
				return records2.length;
			}

			public int getColumnCount() {
				return columns2.length;
			}
			
			//컬럼의 제목을 배열로부터 구한다
			public String getColumnName(int columns) {
				return columns2[columns];
			}
			
			public Object getValueAt(int rowIndex, int columnIndex) {
				return records2[rowIndex][columnIndex];
			}
			
		});
		
		scroll_table = new JScrollPane(table);
		scroll_table2 = new JScrollPane(importantTable);
		bt_regist = new JButton("글 작성");
		
		p_east = new JPanel();
		la_notice = new JLabel("공지사항",SwingConstants.LEFT);
		p_notice = new JPanel();
		
		//스타일 및 레이아웃
		p_center.setPreferredSize(new Dimension(700,650));
		p_center.setBackground(Color.WHITE);
		//p_center.setLayout(new BorderLayout());
		p_search.setPreferredSize(new Dimension(700,35));
		p_search.setBackground(Color.WHITE);
		ch_category.setPreferredSize(new Dimension(160,30));
		t_keyword.setPreferredSize(new Dimension(460,30));
		bt_search.setPreferredSize(new Dimension(60,30));
		scroll_table.setPreferredSize(new Dimension(700,500));
		
		p_east.setPreferredSize(new Dimension(280,650));
		la_notice.setPreferredSize(new Dimension(280,20));
		la_notice.setFont(new Font("맑은 고딕", Font.BOLD, 18));
		p_notice.setPreferredSize(new Dimension(280,340));
		p_notice.setBackground(Color.WHITE);
		scroll_table2.setPreferredSize(new Dimension(280,340));
		
		
		//테이블 디자인
		tableCellCenter(table);
		table.getColumnModel().getColumn(0).setPreferredWidth(5);
		table.getColumnModel().getColumn(1).setPreferredWidth(70);
		table.getColumnModel().getColumn(2).setPreferredWidth(5);
		table.getColumnModel().getColumn(3).setPreferredWidth(20);
		table.getColumnModel().getColumn(4).setPreferredWidth(180);
		table.getColumnModel().getColumn(5).setPreferredWidth(90);
		table.getColumnModel().getColumn(5).setPreferredWidth(80);
		table.setRowHeight(40);
		table.setShowVerticalLines(false);
		tableCellCenter2(importantTable);
		importantTable.getColumnModel().getColumn(0).setPreferredWidth(5);
		importantTable.getColumnModel().getColumn(1).setPreferredWidth(5);
		importantTable.getColumnModel().getColumn(2).setPreferredWidth(5);
		importantTable.getColumnModel().getColumn(3).setPreferredWidth(5);
		importantTable.setRowHeight(20);
		importantTable.setShowVerticalLines(false);
	
		JTableHeader table_header = table.getTableHeader();
		table_header.setPreferredSize(new Dimension(10,30));

		
		//검색 카테고리 등록
		ch_category.add("choice category");
		ch_category.add("writer");
		ch_category.add("title");

		//조립
		p_search.add(ch_category);
		p_search.add(t_keyword);
		p_search.add(bt_search);
		p_center.add(p_search,BorderLayout.NORTH);
		p_center.add(scroll_table);
		p_center.add(bt_regist);
		add(p_center, BorderLayout.CENTER);
		p_notice.add(la_notice);
		p_notice.add(scroll_table2);
		p_east.add(p_notice);
		add(p_east, BorderLayout.EAST);
		
		//이벤트	
		bt_regist.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				getNoticeForm();
			}
		});
		
		bt_search.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				//검색을 안할 경우 모든 데이터가 나오게
				if(ch_category.getSelectedIndex()==00 && t_keyword.getText().length()==0) {
					getNoticeList();
				}else {
					getListBySearch();
				}
			}
		});
		
		//테이블과 리스너 연결
		table.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				getDetail();
			}
		});
		getNoticeList();
		getImportantList();
	}
	
	public void getNoticeList() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select notice_id, title, level, writer, content, regdate, filename from notice";
		sql += " order by notice_id desc";
		try {
			pstmt=this.getAppMain().getMemberMain().getCon().prepareStatement(sql
					, ResultSet.TYPE_SCROLL_INSENSITIVE
					, ResultSet.CONCUR_READ_ONLY);
			
			rs = pstmt.executeQuery();
			rs.last();
			int total = rs.getRow();
			
			//JTable이 참조하고 있는 records이차원배열의 값을, rs를 이용하여 갱신
			records=new String[total][columns.length];
			
			rs.beforeFirst(); // 커서를 제자리로
			int index = 0;
			while(rs.next()) {
				date = rs.getTimestamp("regdate");
				dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm");
				regdate = dateFormat.format(date);
				//System.out.println(regdate);
				records[index][0]=rs.getString("notice_id");
				records[index][1]=rs.getString("title");
				records[index][2]=rs.getString("level");
				records[index][3]=rs.getString("writer");
				records[index][4]=rs.getString("content");
				records[index][5]=regdate;
				records[index][6]=rs.getString("filename");
				index++;
			}
			table.updateUI();//JTable 갱신 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			this.getAppMain().getMemberMain().release(pstmt, rs);
		}
	}
	
	//공지사항 테이블
	public void getImportantList() {
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select title, level, writer, content from notice";
		sql += " where level='공지'";
		sql += " order by notice_id desc";
		
		try {
			pstmt=this.getAppMain().getMemberMain().getCon().prepareStatement(sql
					, ResultSet.TYPE_SCROLL_INSENSITIVE
					, ResultSet.CONCUR_READ_ONLY);
			
			rs = pstmt.executeQuery();
			rs.last();
			int total = rs.getRow();
			
			//JTable이 참조하고 있는 records이차원배열의 값을, rs를 이용하여 갱신
			records2=new String[total][columns2.length];
			
			rs.beforeFirst(); // 커서를 제자리로
			int index = 0;
			while(rs.next()) {
				records2[index][0]=rs.getString("title");
				records2[index][1]=rs.getString("level");
				records2[index][2]=rs.getString("writer");
				records2[index][3]=rs.getString("content");
				index++;
			}
			importantTable.updateUI();//JTable 갱신 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			this.getAppMain().getMemberMain().release(pstmt, rs);
		}
	}
	
	//검색
	public void getListBySearch() {
		String category = ch_category.getSelectedItem();
		String keyword = t_keyword.getText();
		//System.out.println(keyword);
		
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		
		String sql = "select title, level, writer, content, regdate, filename from notice";
		sql += " where notice_id ";
		sql += " and "+category+" like '%"+keyword+"%'";
		sql += " order by notice_id desc";
		
		try {
			pstmt=this.getAppMain().getMemberMain().getCon().prepareStatement(sql
					, ResultSet.TYPE_SCROLL_INSENSITIVE
					, ResultSet.CONCUR_READ_ONLY);
			
			rs = pstmt.executeQuery();
			rs.last();
			int total = rs.getRow();
			
			//JTable이 참조하고 있는 records이차원배열의 값을, rs를 이용하여 갱신
			records=new String[total][columns.length];
			
			rs.beforeFirst(); // 커서를 제자리로
			int index = 0;
			while(rs.next()) {
				date = rs.getTimestamp("regdate");
				dateFormat =new SimpleDateFormat("yyyy-MM-dd HH:mm");
				regdate = dateFormat.format(date);
				records[index][0]=rs.getString("title");
				records[index][1]=rs.getString("level");
				records[index][2]=rs.getString("writer");
				records[index][3]=rs.getString("content");
				records[index][4]=regdate;
				records[index][5]=rs.getString("filename");
				index++;
			}
			table.updateUI();//JTable 갱신 
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			this.getAppMain().getMemberMain().release(pstmt, rs);
		}
		//System.out.println(sql);
	}
	
	//상세정보
	public void getDetail() {
		//선택한 레코드의 notice_id
		String notice_id = (String)table.getValueAt(table.getSelectedRow(), 0);
		//System.out.println(notice_id);
		
		detailForm = new DetailForm(notice_id, this);
		detailForm.setVisible(true);
	}
	
	//등록폼 띄우기
	public void getNoticeForm() {
		form = new NoticeForm(appMain, memberMain, this);
		form.setVisible(true);
	}
	
	public void tableCellCenter(JTable table) {
	    // 테이블 내용 가운데 정렬하기
	      DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(); // 디폴트테이블셀렌더러를 생성
	      dtcr.setHorizontalAlignment(SwingConstants.CENTER); // 렌더러의 가로정렬을 CENTER로
	     
	      TableColumnModel tcm = table.getColumnModel() ; // 정렬할 테이블의 컬럼모델을 가져옴
	     
	      //특정 열에 지정
	      tcm.getColumn(0).setCellRenderer(dtcr);  
	      tcm.getColumn(2).setCellRenderer(dtcr);
	      tcm.getColumn(3).setCellRenderer(dtcr);
	      tcm.getColumn(5).setCellRenderer(dtcr);
	}
	
	public void tableCellCenter2(JTable table) {
		// 테이블 내용 가운데 정렬하기
		DefaultTableCellRenderer dtcr = new DefaultTableCellRenderer(); // 디폴트테이블셀렌더러를 생성
		dtcr.setHorizontalAlignment(SwingConstants.CENTER); // 렌더러의 가로정렬을 CENTER로
		
		TableColumnModel tcm = table.getColumnModel() ; // 정렬할 테이블의 컬럼모델을 가져옴
		
		//특정 열에 지정
		tcm.getColumn(0).setCellRenderer(dtcr);  
		tcm.getColumn(1).setCellRenderer(dtcr);
		tcm.getColumn(2).setCellRenderer(dtcr);
		tcm.getColumn(3).setCellRenderer(dtcr);
	}
}
