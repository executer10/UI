<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>CRUD게시판</title>
    <link rel="stylesheet" href="/resources/CSS/default.css">
    <link rel="stylesheet" href="/resources/CSS/borderList.css">

</head>

<body>
    <%@ include file="/WEB-INF/views/header.jsp" %>

    <section>
        <article>
            <span><a href="">전체</a></span>
        </article>

        <article>
            <table>
                <tr>
                    <th style="width: 50px;">번호</th>
                    <th style="width: 300px;">제목</th>
                    <th style="width: 100px;">작성자</th>
                    <th style="width: 100px;">작성일</th>
                    <th style="width: 100px;">조회수</th>
                    <th style="width: 100px;">추천</th>
                </tr>
                <!-- 데이터받아서 반복문 돌릴 예정 -->
                <tr>
                    <td>1</td>
                    <td>제목</td>
                    <td>작성자</td>
                    <td>작성일</td>
                    <td>조회수</td>
                    <td>추천</td>
                </tr>
            </table>
            <div>

            </div>
        </article>

        <article>
            <select id="">
                <option value="">등록순</option>
                <option value="">최근 댓글 순</option>
                <option value="">댓글 개수 순</option>
            </select>

            <select name="" id="">
                <option value="">전체</option>
                <option value="">제목/내용</option>
                <option value="">제목</option>
                <option value="">내용</option>
                <option value="">글쓴이</option>
                <option value="">댓글</option>
            </select>
            <input type="text">
            <input type="submit" value="검색">

            <a href="/WEB-INF/views/borderWriting.jsp">글쓰기</a>
        </article>
    </section>

    <script src="/resources/JavaScript/border.js"></script>
</body>

</html>