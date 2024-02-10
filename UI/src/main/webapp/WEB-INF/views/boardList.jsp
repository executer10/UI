<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jstl/fmt_rt" prefix="fmt"%>
<!-- boardList.jsp -->
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Html 집합체</title>
        <link rel="stylesheet" href="<c:url value='/resources/CSS/MainGrideStyle.css?a'/>">
        <link rel="stylesheet" href="<c:url value='/resources/CSS/UIBoxStyle.css?a'/>">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
    </head>
    <body>
    	<script>
    		let msg = "${msg}";
    		if(msg =="DEL_OK") alert("게시글이 삭제되었습니다.");
    		if(msg == "DEL_ERR") alert("게시글 삭제에 실패하였습니다.");
    	    if(msg=="LIST_ERR")  alert("게시물 목록을 가져오는데 실패했습니다. 다시 시도해 주세요.");
    		if(msg == "WRT_OK") alert("성공적으로 등록되었습니다.");
    		if(msg == "MOD_OK") alert("게시글 수정을 성공하였습니다.");
    	</script>
        <div class="Title">
			<a href="/UI/"><img src="<c:url value='/resources/Img/Logo.PNG'/>" alt="logo"/></a>
		</div>
        <div id="UI" style="height: 800px;">
            <div id="UI_middle">
            	<!-- 검색창 추가 -->
			    <div>
			    
			    <form action="<c:url value="/boardList"/>" class="search-form" method="get">
			        <select class="search-option" name="option">
			          <option value="A" ${option=='A' ? "selected" : ""}>제목+내용</option>
			          <option value="T" ${option=='T' ? "selected" : ""}>제목만</option>
			          <option value="W" ${option=='W' ? "selected" : ""}>작성자</option>
			        </select>
			
					<input type="text" name="keyword" class="search-input" type="text" value="${param.keyword}" placeholder="검색어를 입력해주세요">
					<input type="hidden" name="category" id="category" value="${c_name}" />
					<input type="submit" class="search-button" value="검색">
				</form>
			      
			      
			    </div>
            
				<button type="button" id="writenBtn" onclick="location.href='<c:url value="/board/write"/>'">글쓰기</button>
				
				<table border="1" width="100%">
					<tr>
						<th>제목</th>
						<th>이름</th>
						<th>작성일</th>
						<th>조회수</th>
						<th>카테고리</th>
					</tr>
					
					<c:forEach var="boardDTO" items="${list}">
						<tr>
							<td><a href="<c:url value='/board/read${ph.sc.getQueryString()}&bno=${boardDTO.bno}'/>"><c:out value='${boardDTO.title}'/></a></td>
							<td>${boardDTO.writer}</td>
							<c:choose>
								<c:when test="${boardDTO.reg_date.time >= startOfToday}">
									<td class="regdate"><fmt:formatDate value="${boardDTO.reg_date}" pattern="HH:mm" type="time"/></td>
								</c:when>
								<c:otherwise>
					              <td class="regdate"><fmt:formatDate value="${boardDTO.reg_date}" pattern="yyyy-MM-dd" type="date"/></td>
					            </c:otherwise>
					          </c:choose>
							<td>${boardDTO.view_cnt}</td>
							<td>${boardDTO.c_name}</td>
						</tr>
					</c:forEach>
					
					
				</table>
				<div>
					<c:if test="${ph.getTotalCnt()== null || ph.getTotalCnt() == 0}">
						<div>게시글이 없읍니다.</div>
						<div>Total Count: ${ph.getTotalCnt()}</div>  <!-- 로깅 -->
					</c:if>
				</div>
				
				<div>
				        <c:if test="${ph.showPrev}">
				        	<a href="<c:url value='/boardList${ph.sc.getQueryString(ph.beginPage-1)}&category=${c_name}'/>"> &lt;</a>   
				        </c:if>
				        	
				        <c:forEach var="i" begin="${ph.beginPage}" end="${ph.endPage}">
				        	<a href="<c:url value='/boardList${ph.sc.getQueryString(i)}&category=${c_name}'/>">${i}</a>
				        </c:forEach>
				        	
				        <c:if test="${ph.showNext}">
				        	<a href="<c:url value='/boardList${ph.sc.getQueryString(ph.endPage+1)}&category=${c_name}'/>"> &gt;</a>   
				        </c:if>
				</div>
            </div>
            <div id="UI_last">
                
            </div>
        </div>
    </body>
</html>