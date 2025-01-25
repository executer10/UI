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
                <p>Python</p>
                <article>
                    <ol>
                        <li id="Python_Built_In_Function">내장함수</li>
                        <li id="Python_Variables_And_DataTypes">변수와 자료형</li>
                        <li id="Python_Data_Structures_Basics">자료구조 기초</li>
                        <li id="Python_Numpy">넘파이</li>
                        <li id="Python_Pandas">판다스</li>
                        <li id="Python_graph">그래프 그리기</li>
                        <li id="Python_Public_Data_Utilization">공공데이터 활용</li>
                        <li id="Python_Machine_Learning">머신러닝</li>
                    </ol>
                </article>
            </section>
        </nav>
        <main id="Python_main">

        </main>
    </div>
    <script src="/resources/JavaScript/languageCommand.js"></script>
</body>

</html>