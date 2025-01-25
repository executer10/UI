<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<header>
    <a href="/main"><img src="/resources/img/member/Logo.png" alt=""></a>
    <div>
        <c:choose>
            <c:when test="${sessionScope.user_id == null}">
                <button type="button" onclick="location.href='/login'">로그인 하러가기</button>
            </c:when>
            <c:otherwise>
                <button type="button" onclick="location.href='/logout'">로그아웃 하기</button>
                <!-- <button type="button" onclick="location.href='/PDFDownloadLog'">PDFDownloadLog</button> -->
            </c:otherwise>
        </c:choose>
    </div>
</header>