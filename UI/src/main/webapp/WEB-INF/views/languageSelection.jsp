<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!-- languageSelection.jsp -->
<!DOCTYPE html>
<html>
    <head>
        <meta charset="UTF-8">
        <meta http-equiv="X-UA-Compatible" content="IE=edge">
        <meta name="viewport" content="width=device-width, initial-scale=1.0">
        <title>Html 집합체</title>
        <link rel="stylesheet" href="<c:url value='/resources/CSS/NoGridStyle.css?a'/>">
        <link rel="stylesheet" href="<c:url value='/resources/CSS/UIBoxStyle.css?a'/>">
        <link rel="stylesheet" href="<c:url value='/resources/CSS/SelectMain.css?a'/>">
    </head>
    <body>
        <div class="Title">
            <a href="/UI"><img src="<c:url value='/resources/Img/Logo.PNG'/>" alt=""></a>
        </div>
        <div id="UI">
            <div id="UI_first">
                
            </div>
            <div id="UI_middle">
                <div class="UI_boxStyle">
                    <h2>언어를 선택하세요!</h2>
                </div>
                
                <div class="UI_box">
                    <a class="UI_boxStyle" href="<c:url value='/boardList?category=HTML'/>"><img src="<c:url value='/resources/Img/HTML.jpg'/>" class="HTML"></a>
                    <a class="UI_boxStyle" href="<c:url value='/boardList?category=CSS'/>"><img src="<c:url value='/resources/Img/CSS.jpg'/>" class="CSS"></a>
                    <a class="UI_boxStyle" href="<c:url value='/boardList?category=JavaScript'/>"><img src="<c:url value='/resources/Img/JS.jpg'/>" class="JS"></a>
                    <a class="UI_boxStyle" href="<c:url value='/boardList?category=JAVA'/>"><img src="<c:url value='/resources/Img/Java.jpg'/>" class="JAVA"></a>
                    <a class="UI_boxStyle" href="<c:url value='/boardList?category=Oracle'/>"><img src="<c:url value='/resources/Img/Oracle.png'/>" class="Oracle"></a>
                    <a class="UI_boxStyle" href="<c:url value='/boardList?category=JSP'/>"><img src="<c:url value='/resources/Img/JSP.png'/>" class="JSP"></a>
                    <a class="UI_boxStyle" href="<c:url value='/boardList?category=Spring'/>"><img src="<c:url value='/resources/Img/Spring.png'/>" class="Spring"></a>
                    <a class="UI_boxStyle" href="<c:url value='/boardList?category=Python'/>"><img src="<c:url value='/resources/Img/Python.png'/>" class="Python"></a>
                    <a class="UI_boxStyle" href="<c:url value='/boardList?category=C++'/>"><img src="<c:url value='/resources/Img/CPP.jpg'/>" class="C++"></a>
                </div>
            </div>
            <div id="UI_last">
                
            </div>
        </div>
        <script src="<c:url value='/resources/JS/UI_SelectMain.js'/>"></script>
    </body>
</html>
