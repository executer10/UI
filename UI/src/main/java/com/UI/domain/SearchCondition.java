package com.UI.domain;
/*SearchCondition.java*/
import org.springframework.web.util.UriComponentsBuilder;

public class SearchCondition {

	private Integer page=1;
	private Integer pageSize=10;
	private String option="";
	private String keyword="";
	private String c_name;

	
	public SearchCondition() {}

	public SearchCondition(Integer page, Integer pageSize, String option, String keyword, String c_name) {
		this.page = page;
		this.pageSize = pageSize;
		this.option = option;
		this.keyword = keyword;
		this.c_name = c_name;
	}

	public Integer getPage() {
		return page;
	}

	public void setPage(Integer page) {
		this.page = page;
	}

	public Integer getPageSize() {
		return pageSize;
	}

	public void setPageSize(Integer pageSize) {
		this.pageSize = pageSize;
	}

	public String getOption() {
		return option;
	}

	public void setOption(String option) {
		this.option = option;
	}

	public String getKeyword() {
		return keyword;
	}

	public void setKeyword(String keyword) {
		this.keyword = keyword;
	}
	
	public String getC_name() {
		return c_name;
	}

	public void setC_name(String c_name) {
		this.c_name = c_name;
	}

	public String getQueryString() {
		return getQueryString(page);
	}
	//?page=1&pageSize=10&option=T&keyword="title" 를 표현한 코드
		public String getQueryString(Integer page) {
			//uriComponentsBuilder : components를 생성함
			return UriComponentsBuilder.newInstance()
					.queryParam("page", page)
					.queryParam("pageSize", pageSize)
					.queryParam("option", option)
					.queryParam("keyword", keyword)
					.build().toString();
		}
	@Override
	public String toString() {
		return "SearchCondition [page=" + page + ", pageSize=" + pageSize + ", option=" + option + ", keyword="
					+ keyword + "]";
		}

	
}
