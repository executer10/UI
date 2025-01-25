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
                <p>Java</p>
                <article>
                    <ol>
                        <li id="Java_Variables_And_Type">변수와 타입</li>
                        <li id="Java_Operator">연산자</li>
                        <li id="Java_Conditional_Statements_And_Loops">조건문과 반복문</li>
                        <li id="Java_Reference_Type">참조타입</li>
                        <li id="Java_Class">클래스</li>
                        <li id="Java_Inheritance">상속</li>
                        <li id="Java_Interface">인터페이스</li>
                        <li id="Java_NestedClass_NestedInterface">중첩 클래스와 중첩 인터페이스</li>
                        <li id="Java_ExceptionHandling">예외 처리</li>
                        <li id="Java_CollectionFramework">컬렉션 프레임워크</li>
                    </ol>
                </article>
            </section>
        </nav>
        <main id="Java_main">

        </main>
    </div>
    <script src="/resources/JavaScript/languageCommand.js"></script>
</body>

</html>