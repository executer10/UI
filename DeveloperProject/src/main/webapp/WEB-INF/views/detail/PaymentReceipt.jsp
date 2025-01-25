<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">    
    <title>요금수납확인서</title>
<link rel="stylesheet" href="/resources/CSS/detail/payment.css" />
</head>
<body>
    <div>
        <h2>요 금 수 납 확 인 서</h2>
        <h3>고 객 일 반 사 항</h3>
        <table>
            <tr>
                <td>서비스 번호</td>
                <td>${PRDTO[0].contract_number}</td>
                <td>고 객 번 호</td>
                <td>${PRDTO[0].customer_phone}</td>
                <td>고 객 명</td>
                <td>${PRDTO[0].customer_name}</td>
            </tr>
        </table>
        <h3>요 금 청 구 내 역</h3>
        <table>
            <tr>
                <th>청 구 날 짜</th><th>청 구 금 액</th><th>납 부 금 액</th>
            </tr>
            <c:forEach var="item" items="${PRDTO}">
                <tr>
                    <td>${item.billing_date}</td>
                    <td>${item.billed_amount}</td>
                    <td>${item.paid_amount}</td>
                </tr>
            </c:forEach>
        </table>
        <table>
            <tr>
                <td>납부금액 합계</td>
                <td>
                    <c:set var="totalPaid" value="0" />
                    <c:forEach var="item" items="${PRDTO}">
                        <c:set var="totalPaid" value="${totalPaid + item.paid_amount}" />
                    </c:forEach>
                    <b>${totalPaid}</b>
                </td>
            </tr>
        </table>
        <table>
            <tr>
                <td>위와 같이 납부하였음을 증명합니다.<br>
                    <span>출력날짜</span><br>
                    <div style="position: relative;">
                        <span id="Seal">한국지역난방공사</span> 
                        <img id="Seall" alt="도장" src="/resources/img/detail/Seal.png" width="50" height="50">
                    </div>
                </td>
            </tr>
        </table>
        <p>대표 번호 1688-2488</p>
    </div>
    
    <script type="text/javascript">
    document.addEventListener('DOMContentLoaded', function() {
        window.print();
    });
    </script>
</body>
</html>
