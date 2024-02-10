<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- index.jsp -->
<c:set var="loginOutLink" value="${sessionScope.id == null ? '/' : '/logout'}"/>
<c:set var="loginOut" value="${sessionScope.id == null ? '로그인' : '로그아웃'}"/>
<c:set var="textPrint" value="${sessionScope.id == null ? '' : '님 안녕하세요'}"/>
<c:set var="removeText" value="${sessionScope.id == null ? '' : '회원 탈퇴'}"/>
<c:set var="pageMove" value="${sessionScope.id == null ? '/' : '/boardList/languageSelection'}"/>
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>메인페이지</title>
        <link rel="stylesheet" href="<c:url value='/resources/CSS/NoGridStyle.css?a'/>">
        <link rel="stylesheet" href="<c:url value='/resources/CSS/UIBoxStyle.css?a'/>">
        <link rel="stylesheet" href="<c:url value='/resources/CSS/UI_Index.css?a'/>">
    </head>
    <body>
        <div id="entire">
            <div class="Title">
                <a href="/UI"><img src="<c:url value='/resources/Img/Logo.PNG'/>" alt=""></a>
            </div>
            
            <div id="UI" style="height: 800px;">
                <div class="UIMain">
                    <div class="UIStyle" style="height: 90%; ">
						<a href="/UI${pageMove}"><img alt="" src="<c:url value='/resources/Img/OpenDoor.png'/>"></a>
                    </div>
                </div>
                <div class="UIBox">
                    <div class="UIStyle UIStyleSecond">
	                    <c:choose>
	                    	<c:when test="${sessionScope.id == null}">
	                    		<form action="<c:url value='/'/>" method="post" onsubmit="return formCheck(this);">
		                            <input type="text" name="id" value="${cookie.id.value}" size="20" placeholder="아이디를 입력하세요."><br>
		                            <input type="password" name="pw" size="20" placeholder="비밀번호를 입력하세요"><br>
		                            <input type="hidden" name="toURL" value="${param.toURL}">
		                            <button>${loginOut}</button>
		                            <a href="<c:url value='/join'/>">회원가입</a><br>
		                            <label><input type="checkbox" name="rememberId" ${empty cookie.id.value? "" : "checked" }> 아이디 기억</label>
		                    	</form>
	                    	</c:when>
	                    	<c:otherwise>
	                    		<p>${sessionScope.id}${textPrint}</p>
					            <a href="<c:url value='${loginOutLink}'/>">${loginOut}</a> <br>
					            <a href="#">${removeText}</a> <br>
	                    	</c:otherwise>
	                    </c:choose>
                    </div>
                    <div class="UIStyle" style="height: 87%;">

                    </div>
                </div>
            </div>
            <div class="Footer">
                <div class="UIStyle" style="height: 70%; width: 74.7%;">

                </div>
            </div>
        </div>
        <script src="<c:url value='/resources/JS/login.js'/>"></script>
    </body>
</html>
