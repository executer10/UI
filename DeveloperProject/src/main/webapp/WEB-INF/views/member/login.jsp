<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>로그인</title>
    <link rel="stylesheet" href="/resources/CSS/member/header.css">
    <link rel="stylesheet" href="/resources/CSS/member/login.css">
</head>
<body>
	<%@include file="/WEB-INF/views/member/header.jsp" %>
	
    <form action="/login/processing" method="post" onsubmit="return formCheck(this)">
        <table>
            <tr>
                <td>아이디</td>
                <td><input type="text" name="user_id" id="user_id"></td>
            </tr>
            <tr>
                <td>비밀번호</td>
                <td><input type="password" name="password" id="password"></td>
            </tr>
            <tr>
                <td><input type="checkbox" name="loginCookie">로그인 유지하기</td>
                <td><button type="submit" style="width: 100%;">로그인하기</button></td>
            </tr>
            <tr>
                <td colspan="2"><button type="button" onclick="location.href='/join.html';" style="width: 100%;">회원가입</button></td>
            </tr>
        </table>
    </form>

    <script src="resources/member/JS/login.js"></script>
</body>
</html>