<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>전기적 요인 화재현황</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <h2>전기적 요인 화재현황 (2023년 기준)</h2>

    <div style="width: 90%; margin: auto;">
        <canvas id="fireChart"></canvas>
    </div>

    <script>
        var ctx = document.getElementById('fireChart').getContext('2d');
        var chartData = {
            labels: [
                <c:forEach var="row" items="${fireData}" varStatus="status">
                    '${row.region_province}'<c:if test="${!status.last}">,</c:if>
                </c:forEach>
            ],
            datasets: [{
                label: '사고 발생 건수',
                data: [
                    <c:forEach var="row" items="${fireData}" varStatus="status">
                        ${row.accident_count}<c:if test="${!status.last}">,</c:if>
                    </c:forEach>
                ],
                backgroundColor: 'rgba(75, 192, 192, 0.6)',
                borderColor: 'rgba(75, 192, 192, 1)',
                borderWidth: 1
            }]
        };

        var myChart = new Chart(ctx, {
            type: 'bar',
            data: chartData,
            options: {
                responsive: true,
                scales: {
                    y: {
                        beginAtZero: true,
                        title: {
                            display: true,
                            text: '사고 발생 건수'
                        }
                    },
                    x: {
                        title: {
                            display: true,
                            text: '지역'
                        }
                    }
                },
                plugins: {
                    title: {
                        display: true,
                        text: '전기적 요인 화재현황 (2023년 기준)',
                        font: {
                            size: 18
                        }
                    },
                    legend: {
                        display: false
                    }
                }
            }
        });
    </script>
</body>
</html>
