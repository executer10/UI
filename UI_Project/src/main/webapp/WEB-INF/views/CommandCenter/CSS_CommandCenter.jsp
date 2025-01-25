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
                <p>CSS</p>
                <article>
                    <ol>
                        <li id="CSS_boxModel_background">box model 및 배경</li>
                        <li id="CSS_layout_grid_flax">레이아웃 및 그리드/플렉스박스</li>
                        <li id="CSS_textStyling">텍스트 스타일링</li>
                        <li id="CSS_transition_animation">트랜지션, 애니메이션 효과</li>
                        <li id="CSS_media_table">반응형 디자인 및 테이블 관련속성</li>
                        <li id="CSS_overflow_scroll">오버플로우 및 스크롤</li>
                        <li id="CSS_transform_addProperties">전환(Transform) 및 추가 속성</li>
                        <li id="CSS_cursor_pointer">커서 및 포인터</li>
                    </ol>
                </article>
            </section>
        </nav>
        <main id="CSS_main">

        </main>
    </div>
    <script src="/resources/JavaScript/languageCommand.js"></script>
</body>

</html>