package com.UI.domain;
/*BoardDTO.java*/
import java.util.Date;

public class BoardDTO {

	private int bno;
	private String title;
	private String content;
	private String writer;
	private int view_cnt;
	private int comments_cnt;
	private Date reg_date;
	private Date up_date;
	private String c_name;
	
	public BoardDTO(int bno, String title, String content, String writer, String c_name) {
		this.bno = bno;
		this.title = title;
		this.content = content;
		this.writer = writer;
		this.c_name = c_name;
	}

	public int getBno() {
		return bno;
	}

	public void setBno(int bno) {
		this.bno = bno;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getWriter() {
		return writer;
	}

	public void setWriter(String writer) {
		this.writer = writer;
	}

	public int getView_cnt() {
		return view_cnt;
	}

	public void setView_cnt(int view_cnt) {
		this.view_cnt = view_cnt;
	}

	public int getComments_cnt() {
		return comments_cnt;
	}

	public void setComments_cnt(int comments_cnt) {
		this.comments_cnt = comments_cnt;
	}

	public Date getReg_date() {
		return reg_date;
	}

	public void setReg_date(Date reg_date) {
		this.reg_date = reg_date;
	}

	public Date getUp_date() {
		return up_date;
	}

	public void setUp_date(Date up_date) {
		this.up_date = up_date;
	}

	public String getC_name() {
		return c_name;
	}

	public void setC_name(String c_name) {
		this.c_name = c_name;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + bno;
		result = prime * result + ((c_name == null) ? 0 : c_name.hashCode());
		result = prime * result + comments_cnt;
		result = prime * result + ((content == null) ? 0 : content.hashCode());
		result = prime * result + ((reg_date == null) ? 0 : reg_date.hashCode());
		result = prime * result + ((title == null) ? 0 : title.hashCode());
		result = prime * result + ((up_date == null) ? 0 : up_date.hashCode());
		result = prime * result + view_cnt;
		result = prime * result + ((writer == null) ? 0 : writer.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BoardDTO other = (BoardDTO) obj;
		if (bno != other.bno)
			return false;
		if (c_name == null) {
			if (other.c_name != null)
				return false;
		} else if (!c_name.equals(other.c_name))
			return false;
		if (comments_cnt != other.comments_cnt)
			return false;
		if (content == null) {
			if (other.content != null)
				return false;
		} else if (!content.equals(other.content))
			return false;
		if (reg_date == null) {
			if (other.reg_date != null)
				return false;
		} else if (!reg_date.equals(other.reg_date))
			return false;
		if (title == null) {
			if (other.title != null)
				return false;
		} else if (!title.equals(other.title))
			return false;
		if (up_date == null) {
			if (other.up_date != null)
				return false;
		} else if (!up_date.equals(other.up_date))
			return false;
		if (view_cnt != other.view_cnt)
			return false;
		if (writer == null) {
			if (other.writer != null)
				return false;
		} else if (!writer.equals(other.writer))
			return false;
		return true;
	}
}
