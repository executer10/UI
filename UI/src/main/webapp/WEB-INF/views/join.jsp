<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- join.jsp -->
<!DOCTYPE html>
<html lang="en">
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>회원가입</title>
        <link rel="stylesheet" href="<c:url value='/resources/CSS/NoGridStyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/resources/CSS/UIBoxStyle.css'/>">
        <link rel="stylesheet" href="<c:url value='/resources/CSS/UI_JoinStyle.css'/>">
        <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
    </head>
    <body>
        <div id="entire">
            <div class="Title">
                <a href="/UI/"><img src="<c:url value='/resources/Img/Logo.PNG" alt="logo"'/>"></a>
            </div>
            <div id="UI" style="height: 800px;">
                <form action="<c:url value="/join/success"/>" method="post" class="UI_join" onsubmit="return formCheck(this)">
                    <ul class="UI_joinBox">  
                        <li class="UI_join_textWarp">
                            <div class="UI_join_inputWrap">
                                <label for="id">아이디</label>
                                <button type="button" id="idOverlap" class="idOverlap" value="N" onclick="fn_idOverlap()">중복확인</button><br>
                                <input type="text" id="id" name="id" placeholder="아이디를 입력하세요." onblur="checkId()">
                                
                            </div>
                        </li>
                        <li class="UI_join_textWarp">
                            <div class="UI_join_inputWrap">
                                <label for="pw">비밀번호</label><br>
                                <input type="password" id="pw" name="pw" placeholder="비밀번호를 입력하세요" onblur="checkPw()">
                            </div>
                        </li>
                        <li class="UI_join_textWarp">
                            <div class="UI_join_inputWrap">
                                <label for="PWCheck">비밀번호 확인</label><br>
                                <input type="password" id="PWCheck" name="PWCheck">
                            </div>
                        </li>
                        <li class="UI_join_textWarp">
                            <div class="UI_join_inputWrap">
                                <label for="name">이름</label><br>
                                <input type="text" id="name" name="name" placeholder="이름을 입력하세요." onblur="checkName()">
                            </div>
                        </li>
                        <li class="UI_join_textWarp">
                            <div class="UI_join_inputWrap">
                                <label for="birth">생년월일</label><br>
                                <input type="text" id="brith" name="birth" placeholder="생년월일(ex: 20200101)" onblur="Birth()">
                            </div>
                        </li>
                        <li class="UI_join_textWarp">
                            <div class="UI_join_inputWrap">
                                <label for="tel">휴대전화 번호</label><br>
                                <input type="text" id="tel" name="tel" placeholder="전화번호 입력(ex: 010-0000-0000)" onblur="checkTel()">
                            </div>
                        </li>
                        <li class="UI_join_textWarp">
                            <div class="UI_join_inputWrap">
                                <button style="width: 200px; height: 50px;" onclick="checkAll()">가입하기</button>
                            </div>
                        </li>
                    </ul>
                </form>
            </div>
        </div>
        
        <script src="<c:url value='/resources/JS/UI_script.js'/>"></script>
        
        <script>
	    function fn_idOverlap() {
	    	$.ajax({
	    		url: "/UI/join/idOverlap",
	    	  	type: "post",
	    	  	dataType: "json",
	    	   	data: {"id":$("#id").val()},
	    	   	success: function(data) {
	    	    	if($("#id").val() == null || $("#id").val() == "") {
	    	    		alert("아이디를 입력하세요.");
	    	    	} else if(data == 0) {
	    	    		$("#idOverlap").attr("value", "Y");
	    	    		alert("사용 가능한 아이디입니다.");
	    	    	} else if(data == 1) {
	    	    		$("#idOverlap").attr("value", "N");
	    	    		alert("중복된 아이디입니다.");
	    	    	}
	    	  	}
	    	})
	    }
    </script>
    </body>
</html>
