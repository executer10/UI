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
                <p>Spring</p>
                <article>
                    <ol>
                        <li id="Spring_Setting">기본세팅</li>
                        <li id="Spring_Controller_Guide">컨트롤러 가이드</li>
                    </ol>
                </article>
            </section>
        </nav>
        <main id="Spring_main">

        </main>
    </div>
    <script src="/resources/JavaScript/languageCommand.js"></script>
</body>

</html>