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
                <p>MySQL</p>
                <article>
                    <ol>
                        <li id="MySQL_CommandList">SQL 명령어 리스트</li>
                        <li id="MySQL_DDL">데이터 정의어</li>
                        <li id="MySQL_DML">데이터 조작어</li>
                        <li id="MySQL_DCL_and_TCL">데이터, 트랜잭션 제어어</li>
                        <li id="MySQL_DefaultCMD">기본 시스템 명령어</li>
                    </ol>
                </article>
            </section>
        </nav>
        <main id="MySQL_main">

        </main>
    </div>
    <script src="/resources/JavaScript/languageCommand.js"></script>
</body>

</html>