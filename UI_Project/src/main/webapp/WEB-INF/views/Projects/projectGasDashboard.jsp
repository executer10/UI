<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="ko">
<head>
    <meta charset="UTF-8">
    <title>가스 대시보드</title>
    <link rel="shortcut icon" href="#">
    
    <!-- Chart.js 로드 (CDN) -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <!-- 간단한 CSS: grid를 활용한 배치 -->
    <style>
        body {
            margin: 20px;
            font-family: 'Noto Sans KR', sans-serif;
            background-color: #f9f9f9;
            color: #333;
        }
        h1, h2, h3 {
            margin: 10px 0;
        }
        .grid-container {
            display: grid;
            grid-template-columns: 1fr 1fr; /* 2열 */
            grid-template-rows: auto auto; /* 2행으로 수정 */
            gap: 20px; /* 항목 간격 */
        }
        /* 각 아이템(차트/테이블)을 구분 */
        .item1 { grid-column: 1; grid-row: 1; }
        .item2 { grid-column: 2; grid-row: 1; }
        .item3 { grid-column: 1; grid-row: 2; }
        .item4 { grid-column: 2; grid-row: 2; }

        /* 공통 chart wrapper */
        .chart-wrapper {
            padding: 20px;
            background-color: #fff;
            border: 1px solid #ddd;
            border-radius: 6px;
        }
        .chart-wrapper canvas {
            width: 100%;
            height: 400px; /* 고정 높이 */
        }

        /* KPI 섹션 스타일 */
        .kpi-container {
            display: flex;
            flex-wrap: wrap;
            gap: 20px;
            margin-bottom: 20px;
        }
        .kpi-item {
            flex: 1 1 30%;
            padding: 20px;
            background-color: #fff;
            border: 1px solid #ddd;
            border-radius: 6px;
            text-align: center;
        }
        .kpi-item h3 {
            margin-bottom: 10px;
            font-size: 1.2em;
            color: #555;
        }
        .kpi-item p {
            font-size: 2em;
            margin: 0;
            color: #000;
        }

        /* 예측 테이블 스타일 */
        .predictions-table {
            width: 100%;
            border-collapse: collapse;
            margin-top: 20px;
            background-color: #fff;
            border: 1px solid #ddd;
            border-radius: 6px;
            overflow: hidden;
        }
        .predictions-table th, .predictions-table td {
            padding: 12px 15px;
            border-bottom: 1px solid #ddd;
            text-align: center;
        }
        .predictions-table th {
            background-color: #f2f2f2;
            color: #333;
            position: sticky;
            top: 0;
            z-index: 1;
        }
        .predictions-table tr:last-child td {
            border-bottom: none;
        }

        /* 스크롤 가능한 테이블 컨테이너 스타일 */
        .scrollable-table {
            max-height: 400px; /* 원하는 높이로 조정 */
            overflow-y: auto;
            border: 1px solid #ddd;
            border-radius: 6px;
        }

        .error-msg {
            color: red;
            font-weight: bold;
            margin-top: 20px;
        }

        /* 추가적인 스타일: 스크롤바 꾸미기 (선택 사항) */
        .scrollable-table::-webkit-scrollbar {
            width: 8px;
        }
        .scrollable-table::-webkit-scrollbar-thumb {
            background-color: rgba(0, 0, 0, 0.2);
            border-radius: 4px;
        }
    </style>
</head>
<body>

    <h1>가스 수요/공급 대시보드</h1>

    <!-- KPI 섹션 시작 -->
    <div class="kpi-container">
        <div class="kpi-item">
            <h3>지난달 공급량</h3>
            <p id="nationalCurrentSupply">0</p>
        </div>
        <div class="kpi-item">
            <h3>이번달 예측 수요</h3>
            <p id="nationalPredictedDemand">0</p>
        </div>
        <div class="kpi-item">
            <h3>공급 마진 (%)</h3>
            <p id="nationalSupplyMargin">0%</p>
        </div>
    </div>
    <!-- KPI 섹션 끝 -->

    <!-- 그리드 컨테이너 시작 -->
    <div class="grid-container">
        <!-- 1. 수요 예측 -->
        <div class="chart-wrapper item1">
            <h2>1. 수요 예측 (Demand Forecast)</h2>
            <p>과거 11개월 + 이번 달(또는 다음 달) 예측을 라인 차트로 표시</p>
            <canvas id="demandForecastChart"></canvas>
        </div>

        <!-- 2. 온도 - 가스 수요 상관관계 -->
        <div class="chart-wrapper item2">
            <h2>2. 온도 - 가스 수요 상관관계</h2>
            <p>월 평균기온 vs 가스 공급량을 라인/산점도로 표시</p>
            <canvas id="temperatureCorrelationChart"></canvas>
        </div>

        <!-- 3. 예측 오차 -->
        <div class="chart-wrapper item3">
            <h2>3. 예측 오차 (Prediction Error)</h2>
            <p>도시가스사의 예측 오차를 바 차트로 표시(상위 다섯 회사)</p>
            <canvas id="predictionErrorChart"></canvas>
        </div>

        <!-- 4. 회사별 평균 수요 -->
        <div class="chart-wrapper item4">
            <h2>4. 회사별 평균 수요 (Company Demand)</h2>
            <p>최근 연도 기준, 각 도시가스사별 평균 공급(수요)량 표시</p>
            <canvas id="regionalDemandChart"></canvas>
        </div>
    </div>
    <!-- 그리드 컨테이너 끝 -->

    <!-- 예측 테이블 섹션 시작 -->
    <div class="chart-wrapper">
        <h2>5. 회사별 예측 수요 (Company Predictions)</h2>
        <p>각 회사별 예측된 가스 수요와 모델의 RMSE(평균 제곱근 오차)를 표로 표시</p>
        <!-- 스크롤 가능한 컨테이너로 테이블 감싸기 -->
        <div class="scrollable-table">
            <table class="predictions-table" id="regionalPredictionsTable">
                <thead>
                    <tr>
                        <th width="30%">회사</th>
                        <th width="35%">예측 수요</th>
                        <th width="35%">모델 RMSE(평균 제곱근 오차)</th>
                    </tr>
                </thead>
                <tbody>
                    <!-- 데이터가 여기에 추가됩니다 -->
                </tbody>
            </table>
        </div>
    </div>
    <!-- 예측 테이블 섹션 끝 -->

    <!-- 데이터 fetch 및 차트 생성 로직 -->
    <script>
    document.addEventListener('DOMContentLoaded', () => {
        fetch('/GasDashboard/getGasSupplyData')  // Controller의 엔드포인트
            .then(response => response.json())
            .then(jsonData => {
                // 백엔드에서 'status' 필드를 확인
                if (jsonData.status === 'success' && jsonData.data) {
                    const data = jsonData.data;  // 실제 데이터는 'data' 필드 내부에 있음

                    // -----------------------------
                    // 1) KPI 업데이트
                    // -----------------------------
                    // 전국 현재 공급량
                    const nationalCurrentSupply = data.metrics.currentSupply.national;
                    document.getElementById('nationalCurrentSupply').innerText = nationalCurrentSupply.toLocaleString();

                    // 전국 예측 수요
                    const nationalPredictedDemand = data.metrics.predictedDemand.national;
                    document.getElementById('nationalPredictedDemand').innerText = nationalPredictedDemand.toLocaleString();

                    // 전국 공급 마진
                    const nationalSupplyMargin = data.metrics.supplyMargin.national;
                    document.getElementById('nationalSupplyMargin').innerText = nationalSupplyMargin.toFixed(2) + '%';

                    // -----------------------------
                    // 2) 수요 예측 (demandForecast)
                    // -----------------------------
                    const forecastLabels = data.charts.demandForecast.labels;
                    const forecastValues = data.charts.demandForecast.values;
                    const forecastActuals = data.charts.demandForecast.actualValues;
                    const ctxForecast = document.getElementById('demandForecastChart').getContext('2d');
                    new Chart(ctxForecast, {
                        type: 'line',
                        data: {
                            labels: forecastLabels,
                            datasets: [
                                {
                                    label: '예측값',
                                    data: forecastValues,
                                    borderColor: '#007bff',
                                    backgroundColor: 'rgba(0, 123, 255, 0.1)',
                                    fill: true
                                },
                                {
                                    label: '실제값',
                                    data: forecastActuals,
                                    borderColor: '#f66',
                                    backgroundColor: 'rgba(255, 99, 132, 0.1)',
                                    fill: true
                                }
                            ]
                        },
                        options: {
                            responsive: true,
                            scales: {
                                y: {
                                    beginAtZero: false
                                }
                            }
                        }
                    });

                    // -----------------------------
                    // 3) 온도 - 수요 상관관계
                    // -----------------------------
                    const tempData = data.charts.temperatureCorrelation.temperature;
                    const demandData = data.charts.temperatureCorrelation.demand;
                    const correlationLabels = forecastLabels.slice(0, tempData.length);

                    const ctxTempCorr = document.getElementById('temperatureCorrelationChart').getContext('2d');
                    new Chart(ctxTempCorr, {
                        type: 'line',
                        data: {
                            labels: correlationLabels,
                            datasets: [
                                {
                                    label: '평균 기온(℃)',
                                    data: tempData,
                                    borderColor: '#ffa500',
                                    backgroundColor: 'rgba(255, 165, 0, 0.2)',
                                    yAxisID: 'yTemp'
                                },
                                {
                                    label: '가스 공급량',
                                    data: demandData,
                                    borderColor: '#28a745',
                                    backgroundColor: 'rgba(40, 167, 69, 0.2)',
                                    yAxisID: 'yDemand'
                                }
                            ]
                        },
                        options: {
                            responsive: true,
                            scales: {
                                yTemp: {
                                    type: 'linear',
                                    position: 'left',
                                    beginAtZero: false,
                                    title: {
                                        display: true,
                                        text: '기온(℃)'
                                    }
                                },
                                yDemand: {
                                    type: 'linear',
                                    position: 'right',
                                    beginAtZero: false,
                                    title: {
                                        display: true,
                                        text: '가스 공급량'
                                    },
                                    grid: {
                                        drawOnChartArea: false
                                    }
                                }
                            }
                        }
                    });

                 // 4) 예측 오차 (predictionError)
                    const errorLabels = data.charts.predictionError.labels;
                    const errorsObject = data.charts.predictionError.errors;
                    const ctxError = document.getElementById('predictionErrorChart').getContext('2d');

                    // 회사별 평균 오차 계산
                    const companyAverageErrors = {};
                    for (const [company, errors] of Object.entries(errorsObject)) {
                        if (company !== 'national' && company !== '합계') {
                            companyAverageErrors[company] = errors.reduce((a, b) => a + Math.abs(b), 0) / errors.length;
                        }
                    }

                    // 평균 오차를 기준으로 상위 5개 회사 선택
                    const top5Companies = Object.entries(companyAverageErrors)
                        .sort((a, b) => b[1] - a[1])
                        .slice(0, 5)
                        .map(entry => entry[0]);

                    // 데이터셋 생성
                    const datasets = [];
                    const colors = [
                        'rgba(255, 99, 132, 0.6)', 'rgba(54, 162, 235, 0.6)', 
                        'rgba(255, 206, 86, 0.6)', 'rgba(75, 192, 192, 0.6)', 
                        'rgba(153, 102, 255, 0.6)'
                    ];

                    top5Companies.forEach((company, index) => {
                        datasets.push({
                            label: company,
                            data: errorsObject[company],
                            backgroundColor: colors[index],
                            borderColor: colors[index].replace('0.6', '1'),
                            borderWidth: 1
                        });
                    });

                    new Chart(ctxError, {
                        type: 'bar',
                        data: {
                            labels: errorLabels,
                            datasets: datasets
                        },
                        options: {
                            responsive: true,
                            scales: {
                                y: {
                                    beginAtZero: false,
                                    title: {
                                        display: true,
                                        text: '오차율(%)'
                                    }
                                }
                            },
                            plugins: {
                                legend: {
                                    display: true,
                                    position: 'top'
                                }
                            }
                        }
                    });


                    // -----------------------------
                    // 5) 회사별 평균 수요 (regionalDemand -> companyDemand)
                    // -----------------------------
                    const regionalLabels = data.charts.regionalDemand.labels;
                    const regionalValues = data.charts.regionalDemand.values;

                    const ctxRegional = document.getElementById('regionalDemandChart').getContext('2d');
                    new Chart(ctxRegional, {
                        type: 'bar',
                        data: {
                            labels: regionalLabels,
                            datasets: [
                                {
                                    label: '회사별 평균 수요',
                                    data: regionalValues,
                                    backgroundColor: 'rgba(153, 102, 255, 0.6)',
                                    borderColor: 'rgba(153, 102, 255, 1)',
                                    borderWidth: 1
                                }
                            ]
                        },
                        options: {
                            responsive: true,
                            indexAxis: 'y', // 가로 바 차트
                            scales: {
                                x: {
                                    beginAtZero: false
                                }
                            }
                        }
                    });

                    // -----------------------------
                    // 6) 회사별 예측 수요 (Regional Predictions -> Company Predictions)
                    // -----------------------------
                    const regionalPredictions = data.predictions.regional;
                    const tableBody = document.getElementById('regionalPredictionsTable').getElementsByTagName('tbody')[0];

                    for (const [company, details] of Object.entries(regionalPredictions)) {
                        // '합계' 항목은 제외하거나 별도로 처리할 수 있습니다.
                        if (company === '합계') continue;

                        const row = tableBody.insertRow();

                        const cellCompany = row.insertCell(0);
                        const cellPredicted = row.insertCell(1);
                        const cellRMSE = row.insertCell(2);

                        cellCompany.innerText = company;
                        cellPredicted.innerText = details.predicted_value.toLocaleString();
                        cellRMSE.innerText = details.train_rmse.toFixed(2);
                    }

                } else {
                    // 에러 발생 시 메시지 표시
                    console.error('데이터 로드 실패:', jsonData.data ? jsonData.data.message : 'Unknown error');
                    document.body.insertAdjacentHTML('beforeend',
                        `<p class="error-msg">데이터 로드 실패: ${jsonData.data ? jsonData.data.message : '알 수 없는 오류'}.</p>`);
                }
            })
            .catch(error => {
                console.error('AJAX 요청 에러:', error);
                document.body.insertAdjacentHTML('beforeend',
                    `<p class="error-msg">AJAX 요청 에러 발생: ${error}</p>`);
            });
    });
    </script>
</body>
</html>
