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
    <link rel="stylesheet" href="/resources/CSS/CommandCenter.css">
    <!-- XLSX 라이브러리를 HTML에 포함시켜야 합니다 -->
    <script src="https://cdnjs.cloudflare.com/ajax/libs/xlsx/0.18.5/xlsx.full.min.js"></script>
</head>

<body>
    <%@ include file="/WEB-INF/views/header.jsp" %>

    <div id="CommandGrid">
        <nav>
            <section>
                <p>JSP</p>
                <article>
                	부록
                    <ol>
                        <li id="JSP_Default">JSP 기초</li>
                        <li id="JSP_Implicit_Object">내장 객체</li>
                        <li id="JSP_Implicit_Object_Scope">내장 객체 영역</li>
                        <li id="JSP_Cookie">쿠키</li>
                        <li id="JSP_Database">데이터베이스</li>
                        <li id="JSP_Session">세션</li>
                        <li id="JSP_ActionTag">액션 태그</li>
                        <li id="JSP_ExpressionLanguage">표현 언어</li>
                        <li id="JSP_JSTL">JSTL</li>
                        <li id="JSP_Servlet">Servlet</li>
                    </ol>
                    
                    <h4>Model1 방식의 회원제 게시판 만들기</h4>
                    <ol>
                        <li id="JSP_Model1_JDBConnect">JDBConnect.java</li>
                        <li id="JSP_Model1_JSFunction">JSFunction.java</li>
                        <li id="JSP_Model1_MemberDTO_BoardDTO">MemberDTO, BoardDTO.java</li>
                        <li id="JSP_Model1_MemberDAO">MemberDAO.java</li>
                        <li id="JSP_Model1_BorderDAO">BorderDAO.java</li>
                        <li id="JSP_Model1_PageHandler">PageHandler.java</li>
                        <li id="JSP_Model1_HeaderLink">HeaderLink.jsp</li>
                        <li id="JSP_Model1_Login_AND_Process">Login.jsp</li>
                        <li id="JSP_Model1_Join_AND_Process">Join.jsp</li>
                    </ol>
                </article>
            </section>
        </nav>
        <main id="JSP_main">

        </main>
    </div>
    <script src="/resources/JavaScript/languageCommand.js"></script>
</body>

</html>