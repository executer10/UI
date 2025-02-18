document.addEventListener('DOMContentLoaded', () => {
    fetch(/*[[@{/GasDashboard/getGasSupplyData}]]*/)
        .then(response => response.json())
        .then(jsonData => {
            if (jsonData.status === 'success' && jsonData.data) {
                const data = jsonData.data;

                // KPI 업데이트
                const nationalCurrentSupply = data.metrics.currentSupply.national;
                document.getElementById('nationalCurrentSupply').innerText = nationalCurrentSupply.toLocaleString();

                const nationalPredictedDemand = data.metrics.predictedDemand.national;
                document.getElementById('nationalPredictedDemand').innerText = nationalPredictedDemand.toLocaleString();

                const nationalSupplyMargin = data.metrics.supplyMargin.national;
                document.getElementById('nationalSupplyMargin').innerText = nationalSupplyMargin.toFixed(2) + '%';

                // 1) 수요 예측 (demandForecast)
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

                // 2) 온도 - 수요 상관관계
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

                // 3) 예측 오차 (predictionError)
                const errorLabels = data.charts.predictionError.labels;
                const errorsObject = data.charts.predictionError.errors;
                const ctxError = document.getElementById('predictionErrorChart').getContext('2d');

                const companyAverageErrors = {};
                for (const [company, errors] of Object.entries(errorsObject)) {
                    if (company !== 'national' && company !== '합계') {
                        companyAverageErrors[company] = errors.reduce((a, b) => a + Math.abs(b), 0) / errors.length;
                    }
                }

                const top5Companies = Object.entries(companyAverageErrors)
                    .sort((a, b) => b[1] - a[1])
                    .slice(0, 5)
                    .map(entry => entry[0]);

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

                // 4) 회사별 평균 수요 (regionalDemand -> companyDemand)
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
                        indexAxis: 'y',
                        scales: {
                            x: {
                                beginAtZero: false
                            }
                        }
                    }
                });

                // 5) 회사별 예측 수요 (Regional Predictions -> Company Predictions)
                const regionalPredictions = data.predictions.regional;
                const tableBody = document.getElementById('regionalPredictionsTable').getElementsByTagName('tbody')[0];

                for (const [company, details] of Object.entries(regionalPredictions)) {
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