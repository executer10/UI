<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Document</title>
    <link rel="stylesheet" href="/resources/CSS/default.css">
    <link rel="stylesheet" href="/resources/CSS/login.css">
</head>

<body>
    <%@ include file="/WEB-INF/views/header.jsp" %>
    <section>
        <form action="/login/processing" class="loginBox" method="post" onsubmit="return formCheck(this)">
            <table>
                <tr>
                    <th>아이디</th>
                    <td><input type="text" name="id" id="id" placeholder="아이디를 입력하세요" size="50%"></td>
                </tr>
                <tr>
                    <th>비밀번호</th>
                    <td><input type="password" name="pw" id="pw" placeholder="비밀번호를 입력하세요" size="50%"></td>
                </tr>
                <tr>
                    <th><input type="checkbox" name="rememberId" ${empty cookie.id.value ? "" : "checked"}>로그인 유지하기</th>
                    <td><button type="submit" class="btn_style">로그인하기</button></td>
                </tr>
                <tr>
                    <td colspan="2"><button class="btn_style" type="button" onclick="location.href='/join';">회원가입</button></td>
                </tr>
            </table>
        </form>
    </section>

    <script src="/resources/JavaScript/login.js"></script>
</body>

</html>