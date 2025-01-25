<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>마이페이지</title>
    <link rel="stylesheet" href="/resources/CSS/default.css">
    <link rel="stylesheet" href="/resources/CSS/mypage.css">
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
</head>
<body>
    <%@ include file="/WEB-INF/views/header.jsp" %>
    
    <section class="mypage-container">
        <!-- 프로필 섹션 -->
        <div class="profile-section">
            <h2>프로필 정보</h2>
            <form action="/member/update" method="post">
                <table>
                    <tr>
                        <th>아이디</th>
                        <td>${sessionScope.id}</td>
                    </tr>
                    <tr>
                        <th>이름</th>
                        <td><input type="text" name="name" value="${member.name}"></td>
                    </tr>
                    <tr>
                        <th>이메일</th>
                        <td><input type="email" name="email" value="${member.email}"></td>
                    </tr>
                    <tr>
                        <th>전화번호</th>
                        <td><input type="tel" name="tel" value="${member.tel}"></td>
                    </tr>
                </table>
                <button type="submit">정보 수정</button>
                <button type="button" onclick="WithdrawalMembership()">회원 탈퇴</button>
            </form>
        </div>
    </section>
    <footer class="footer"></footer>
    <script src="/resources/JavaScript/mypage.js"></script>
</body>
</html>