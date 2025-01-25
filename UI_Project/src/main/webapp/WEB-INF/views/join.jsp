<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>회원가입</title>
    <link rel="stylesheet" href="/resources/CSS/default.css">
    <link rel="stylesheet" href="/resources/CSS/join.css">
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.7.0/jquery.min.js"></script>
    <script src="https://t1.daumcdn.net/mapjsapi/bundle/postcode/prod/postcode.v2.js"></script>
</head>

<body>
    <%@ include file="/WEB-INF/views/header.jsp" %>

    <section>
        <form onsubmit="return formCheck()" method="post" action="/join/register">
            <table border="1">
                <tr>
                    <td>
                        <input type="text" name="id" id="id" placeholder="아이디">
                        <button type="button" style="height: 45px;" value="N" id="idOverlap" onclick="fn_idOverlap()">중복 체크</button>
                        <p id="idMsg"></p>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="password" name="pw" id="pw" placeholder="비밀번호">
                        <p id="pwMsg"></p>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="password" name="pwCheck" id="pwCheck" placeholder="비밀번호 확인">
                        <p id="pwCheckMsg"></p>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="email" name="email" id="email" placeholder="이메일">
                        <p id="emailMsg"></p>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="text" name="name" id="name" placeholder="이름">
                        <p id="nameMsg"></p>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="text" name="birth" id="birth" placeholder="생년월일">
                        <p id="birthMsg"></p>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input type="tel" name="tel" id="tel" placeholder="휴대폰 번호">
                        <p id="telMsg"></p>
                    </td>
                </tr>
            </table>
            <ul class="radio_box">
                <li class="radio_item">
                    <input type="radio" name="gender" placeholder="성별" value="male">
                    <label for="gender">남자</label>
                </li>
                <li class="radio_item">
                    <input type="radio" name="gender" placeholder="성별" value="female">
                    <label for="gender">여자</label>
                </li>
            </ul>
            <p id="genderMsg"></p>
            <input type="submit" value="회원 가입" style="width: 100%; height: 40px;">
        </form>
    </section>

    <footer class="footer">

    </footer>

    <script src="/resources/JavaScript/join.js"></script>
</body>

</html>