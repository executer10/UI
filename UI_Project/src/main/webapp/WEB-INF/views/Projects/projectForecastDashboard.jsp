<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>데이터 분석</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
  	<link rel="shortcut icon" href="#">
    <link rel="stylesheet" href="/resources/CSS/default.css">
    <style>
        .main-container {
            width: 1200px;
            margin: 0 auto;
            padding: 20px;
            background-color: #f5f5f5;
            min-height: 600px;
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 20px;
        }

        .analysis-box {
            width: 100%;
            aspect-ratio: 1;
            background-color: white;
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
            display: flex;
            flex-direction: column;
            justify-content: center;
            align-items: center;
            position: relative;
            overflow: hidden;
        }

        .upload-form {
            display: flex;
            flex-direction: column;
            gap: 15px;
            width: 100%;
        }

        .file-input {
            margin-bottom: 15px;
        }

        .column-select {
            width: 100%;
            padding: 8px;
            border: 1px solid #ddd;
            border-radius: 4px;
            margin-bottom: 10px;
        }

        .analyze-btn {
            background-color: #4CAF50;
            color: white;
            padding: 10px;
            border: none;
            border-radius: 4px;
            cursor: pointer;
        }

        .analyze-btn:disabled {
            background-color: #cccccc;
            cursor: not-allowed;
        }

        .result-container {
            width: 100%;
            height: 100%;
            display: flex;
            justify-content: center;
            align-items: center;
        }

        .result-graph {
            max-width: 100%;
            max-height: 100%;
            object-fit: contain;
        }

        #filePathDisplay, #newFilePathDisplay {
            margin: 5px 0;
            font-size: 0.9em;
            color: #666;
            word-break: break-all;
        }

        h1 {
            grid-column: 1 / -1;
            text-align: center;
            margin-bottom: 30px;
        }
    </style>
</head>
<body>
    <%@ include file="/WEB-INF/views/header.jsp" %>
    <div class="main-container">
        <h1>데이터 분석</h1>
        <div class="analysis-box" id="box1Box">
            <form id="box1UploadForm" class="upload-form">
                <div class="file-input">
                    <input type="file" id="box1FileInput" name="file" accept=".csv" onchange="handleFileUpload('box1')">
                    <p id="box1FilePathDisplay"></p>
                </div>
                <div>
                    <select id="box1FeatureColumn" name="featureColumn" class="column-select">
                        <option value="">X축 선택</option>
                    </select>
                </div>
                <div>
                    <select id="box1TargetColumn" name="targetColumn" class="column-select">
                        <option value="">Y축 선택</option>
                    </select>
                </div>
                <button type="button" id="box1AnalyzeBtn" class="analyze-btn" onclick="analyzeData('box1')" disabled>분석 시작</button>
            </form>
        </div>
    </div>

    <script type="text/javascript" src="/resources/JavaScript/projectForecastDashboard.js"></script>
</body>
</html>
