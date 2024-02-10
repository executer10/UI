<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- board.jsp -->
<!DOCTYPE html>
<html>
    <head>
    	<!-- 문자 인코딩 설정 -->
        <meta charset="UTF-8">
        
        <!-- 브라우저의 호환성 보기 모드 설정 -->
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        
        <!-- 반응형 웹을 위한 뷰포트 설정 -->
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        
        <!-- 문서 제목 -->
        <title>Html 집합체</title>
        
        <!-- CSS 파일 참조 -->
        <link rel="stylesheet" href="<c:url value='/resources/CSS/MainGrideStyle.css?a'/>">
        <link rel="stylesheet" href="<c:url value='/resources/CSS/UIBoxStyle.css?a'/>">
        
        <!-- JQuery 라이브러리 참조 -->
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
    </head>
    <body>
     	<!-- 제목 영역 -->
        <div class="Title">
			<!-- 로고 이미지, 클릭 시 메인 페이지로 이동 -->
			<a href="/UI/"><img src="<c:url value='/resources/Img/Logo.PNG" alt="logo"'/>"></a>
		</div>
        <div id="UI">
            <div id="UI_middle">
            	<div style="height: 800px;">
            	
            		<!-- mode 파라미터 값에 따라 "작성" 또는 "읽기" 텍스트 표시 -->
	            	<h2>게시글 ${mode=="new" ? "작성" : "읽기"}</h2>
	            	
	                <form id="form">
	                
						<!-- 게시글 번호를 hidden input에 설정 -->
	                	<input type="hidden" name="bno" value="${boardDTO.bno != null ? boardDTO.bno : 0}">
	                	<table>
							<tr>
	                			<td>작성자</td>
	                			<td><input type="text" name="writer" value="${boardDTO.writer}" readonly="readonly"></td>
	                		</tr>
	                		<tr>
								<td>제목</td>
								<!-- XSS(Cross-site scripting 공격) : 웹사이트에 스크립트 코드를 주입하는 웹 사이트 공격 방법 -->
								<td><input type="text" name="title" value="<c:out value='${boardDTO.title}'/>" ${mode == 'new'? '' : 'readonly="readonly"'}></td>                		
	                		</tr>
	                		<tr>
								<td>카테고리</td>
								<td>
									<c:choose>
										<c:when test="${mode == 'new'}">
							      
											<!-- 새 글 작성 시 카테고리 선택 상자 표시 -->
											<select name="c_name">
												<option value="HTML">HTML</option>
												<option value="CSS">CSS</option>
												<option value="JavaScript">JavaScript</option>
												<option value="JAVA">JAVA</option>
												<option value="Oracle">Oracle</option>
												<option value="JSP">JSP</option>
												<option value="Spring">Spring</option>
												<option value="Python">Python</option>
											</select>
										</c:when>
										<c:otherwise>
										
											<!-- 기존 글 읽기 시 카테고리 텍스트만 표시 -->
											<input type="text" name="c_name" value="<c:out value='${boardDTO.c_name}'/>" ${mode == 'new' ? '' : 'readonly="readonly"'}>
										</c:otherwise>
									</c:choose>
								</td>
							</tr>
	                		<tr>
				                <td>내용</td>
				                <td><textarea name="content" ${mode == 'new'? '' : 'readonly="readonly"'} cols="100" rows="20"><c:out value='${boardDTO.content}'/></textarea></td>
				            </tr>
				            <tr>
				            	<!-- 버튼 영역 -->
				                <td colspan="2">
				                    <button type="button" id="writeBtn" class="btn">글쓰기</button>
				                    <button type="button" id="modifyBtn" class="btn">수정</button>
				                    <button type="button" id="removeBtn" class="btn">삭제</button>
				                    <button type="button" id="listBtn" class="btn">목록</button>
				                </td>
				            </tr>
				            <tr>
				            	<td>
				            		<input type="hidden" id="hiddenCategory" name="category" value="${param.category}">
				            	</td>
				            </tr>
	                	</table>
	                </form>
            	</div>
			<c:if test="${mode != 'new' }">
				comment : <input type="text" name="comments"> <br>
				<button id="sendBtn" type="button">SEND</button>
				<button id="modBtn" type="button">확인</button>
				
				<!-- 댓글 표시 -->
				<div id="commentsList"></div>
				
				<div id="replyForm" style="display: none">
					<input type="text" name="replyComment">
					<button type="button" id="wrtRepBtn">등록</button>
				</div>
				
				<script>
					// 게시글의 번호를 변수에 저장
					let bno=${boardDto.bno};
					
					// 해당 게시글의 댓글 목록을 서버에서 가져와 화면에 표시
					let showList = function(bno) {
						
						// 서버에서 /board/comments API를 통해 댓글 정보 GET 요청
						$.ajax({
			                type:'GET',       // 요청 메서드
			                url: '/board/comments?bno='+bno,  // 요청 URI
			                success : function(result){
							
			                	// 받아온 댓글 정보를 화면에 표시할 HTML로 변환 후 표시
								$("#commentsList").html(toHtml(result));
			                },
			                error   : function(){ alert("error") } // 에러가 발생했을 때, 호출될 함수
			            }); // $.ajax()
			        } //function
					
			     	// 문서가 로드된 후 실행
					$(document).ready(function() {
						
						// 게시글 댓글 목록 표시
						showList(bno);
						
						// 댓글 등록 버튼 클릭 시
						$("#sendBtn").click (function() {
							
							// 입력된 댓글 내용 가져오기
							let comments = $("input[name=comments]").val()
							
							// 댓글 내용 검증
							if (comments.trim() == '') {
								alert("댓글을 입력하세요!")
								$("input[name=comments]").focus();
								return;
							}
							
							$.ajax({
				                type:'POST',       // 요청 메서드
				                url: '/board/comments?bno='+bno,  // 요청 URI
				                headers : {"content-type" : "application/json"},//요청헤더
				                data: JSON.stringify({bno: bno, comments:comments}),
				                success : function(result){
				                   
				                  alert(result);
				                  showList(bno);
				                },
				                error   : function(){ alert("error") } // 에러가 발생했을 때, 호출될 함수
				            }); // $.ajax()
							
						})
						
						$("#wrtRepBtn").click (function() {
							
							let comments = $("input[name=replyComment]").val()
							
							let pcno = $("#replyForm").parent().attr("data-pcno");
							
							if (comments.trim() == '') {
								alert("댓글을 입력하세요!")
								$("input[name=replyComment]").focus();
								return;
							}
							
							$.ajax({
				                type:'POST',       // 요청 메서드
				                url: '/board/comments?bno='+bno,  // 요청 URI
				                headers : {"content-type" : "application/json"},//요청헤더
				                data: JSON.stringify({pcno: pcno, bno: bno, comments:comments}),
				                success : function(result){
				                   
				                  alert(result);
				                  showList(bno);
				                },
				                error   : function(){ alert("error") } // 에러가 발생했을 때, 호출될 함수
				            }); // $.ajax()
				            
				            $("#replyForm").css("display", "none");
				            $("input[name=replyComment]").val('');
							$("#replyForm").appendTo("body");
						})
						
						$("#modBtn").click (function() {
							
							let comments = $("input[name=comments]").val()
							
							let cno = $(this).attr("data-cno");
							
							if (comments.trim() == '') {
								alert("댓글을 입력하세요!")
								$("input[name=comments]").focus();
								return;
							}
							
							$.ajax({
				                type:'PATCH',       // 요청 메서드
				                url: '/board/comments/'+cno,  // 요청 URI
				                headers : {"content-type" : "application/json"},//요청헤더
				                data: JSON.stringify({cno: cno, comments:comments}),
				                success : function(result){
				                   
				                  alert(result);
				                  showList(bno);
				                },
				                error   : function(){ alert("error") } // 에러가 발생했을 때, 호출될 함수
				            }); // $.ajax()
							
						})
						
						$("#commentsList").on("click", ".delBtn", function() {
							
							let cno = $(this).parent().attr("data-cno");
							let bno = $(this).parent().attr("data-bno");
							
							$.ajax({
				                type:'DELETE',       // 요청 메서드
				                url: '/board/comments/'+cno + '?bno='+bno,  // 요청 URI
				                success : function(result){
				                   	alert("result")
				                  	
				                    showList(bno);
				                },
				                error   : function(){ alert("error") } // 에러가 발생했을 때, 호출될 함수
				            }); // $.ajax()
						})
						
						$("#commentsList").on("click", ".replyBtn", function() {
							
							//appendTo : 선택한 요소의 뒤에 붙여줌
							$("#replyForm").appendTo($(this).parent());
							$("#replyForm").css("display", "block");
							
						})
						
						$("#commentsList").on("click", ".modBtn", function() {
							
							let cno = $(this).parent().attr("data-cno");
							
							
							// comment내용을 input에 넣어주기
							let comments = $("span.comments", $(this).parent()).text();
							
							$("input[name=comments]").val(comments);
							
							// cno 전달
							$("#modBtn").attr("data-cno", cno)
						})
					})
					
					//결과를 화면에 출력하기위한 함수를 생성
					let toHtml = function(comments) {
						
						let tmp="<ul id='comments'>";
						
						comments.forEach(function(comments) {
							tmp += '<li data-cno=' + comments.cno
							tmp += ' data-pcno=' + comments.pcno
							tmp += ' data-bno=' + comments.bno + '>'
							
							if (comments.cno != comments.pcno)
								tmp += 'ㄴ'
								
							tmp += ' commenter=<span class="commenter">' + comments.commenter + '</span>'
							tmp += ' comments=<span class="comments">' + comments.comments + '</span>'
							tmp += ' up_date=' + dateToString(comments.up_date)				
							tmp += ' <button class="delBtn">삭제</button>'
							tmp += ' <button class="modBtn">수정</button>'
							tmp += ' <button class="replyBtn">답글</button>'
							tmp += '</li>'
						})
						
						tmp+= "</ul>"
						
						return tmp;
					}
			        
			        let addZero = function(value=1) {
			        	return value > 9 ? value : "0" + value;
			        }
			        
			        let dateToString = function(ms=0) {
			        	let date = new Date(ms);
			        	
			        	let yyyy = date.getFullYear();
			        	let mm = addZero(date.getMonth()+1);
			        	let dd = addZero(date.getDate());
			        	
			        	let HH = addZero(date.getHours());
			        	let MM = addZero(date.getMinutes());
			        	let ss = addZero(date.getSeconds());
			        	
			        	return yyyy+ "." + mm + "." + dd + " " + HH + ":" + MM + ":"+ ss;
			        }
			        
				</script>
				
			</c:if>
			
			
			
				<script>
					$(document).ready(function() {
						
						$("#listBtn").on("click", function() {
							location.href="<c:url value='/boardList?category=${boardDTO.c_name}&page=${page}&pageSize=${pageSize}'/>";
						})
						
						$("#removeBtn").on("click", function() {
							
							if(!confirm("정말로 삭제하시겠습니까?")) return;
							
							let form = $('#form')
							form.attr("action", "<c:url value='/board/delete?page=${page}&pageSize=${pageSize}'/>")
							form.attr("method", "post")
							
							form.submit();
							
							
						})
						
						$("#writeBtn").on("click", function() {
			
							let form = $('#form')
							form.attr("action", "<c:url value='/board/write'/>")
							form.attr("method", "post")
							
							form.submit();
						})
						
						//
						//
						
						$("#modifyBtn").on("click", function() {
			
							let form = $('#form')
							
							let isReadOnly = $("input[name=title]").attr('readonly');
							

							//1. 읽기 상태라면 수정 상태로 변경
							if (isReadOnly == 'readonly') {
								$("h2").html("게시글 수정");
								
								$("input[name=title]").attr('readonly', false);
								$("textarea").attr('readonly', false)
								$("#modifyBtn").html("수정 완료");
								
								return;
							}
							
							//2. 수정 상태라면 수정된 내용을 서버로 전송				
							form.attr("action", "<c:url value='/board/update'/>")
							form.attr("method", "post")
							
							form.submit();
						})
						
					})
				</script>
                
            </div>
            <div id="UI_last">
                
            </div>
        </div>
    </body>
</html>