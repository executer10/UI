<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>계산기</title>
    <script src="https://code.jquery.com/jquery-3.6.0.min.js"></script>
    <link rel="shortcut icon" href="#">
</head>
<body>
	<h2>계산기</h2>
	    <form method="post" action="/projectPython/calculate">
	        <div>
	            <label>첫 번째 숫자:</label>
	            <input type="number" name="num1" required>
	        </div>
	        <div>
	            <label>두 번째 숫자:</label>
	            <input type="number" name="num2" required>
	        </div>
	        <button type="submit">계산</button>
	    </form>
	    
	    <div>
	        <h3>결과:</h3>
	        <p>${result}</p>
	    </div>
</body>
</html>