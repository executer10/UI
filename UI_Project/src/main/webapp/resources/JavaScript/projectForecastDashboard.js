 // 폼 유효성 검사 함수
 function validateForm(boxId) {
    // 파일 입력 요소와 X축, Y축 선택 요소를 가져옵니다.
    const fileInput = document.getElementById(boxId + 'FileInput');
    const selectX = document.getElementById(boxId + 'FeatureColumn');
    const selectY = document.getElementById(boxId + 'TargetColumn');
    
    // 파일이 선택되지 않은 경우 알림을 표시하고 false를 반환
    if (!fileInput.files[0]) {
        alert('파일을 선택해주세요.');
        return false;
    }
    // X축과 Y축이 모두 선택되지 않은 경우 알림을 표시하고 false를 반환
    if (!selectX.value || !selectY.value) {
        alert('X축과 Y축을 모두 선택해주세요.');
        return false;
    }
    // 모든 검사를 통과하면 true를 반환
    return true;
}

// 파일 업로드 처리 함수
function handleFileUpload(boxId) {
    // 파일 입력 요소와 X축, Y축 선택 요소를 가져옵니다.
    const fileInput = document.getElementById(boxId + 'FileInput');
    const selectX = document.getElementById(boxId + 'FeatureColumn');
    const selectY = document.getElementById(boxId + 'TargetColumn');
    const analyzeBtn = document.getElementById(boxId + 'AnalyzeBtn');
    const file = fileInput.files[0];

    // 파일이 선택된 경우
    if (file) {
        const reader = new FileReader(); // 파일 리더 객체 생성
        reader.onload = function(e) {
            const csv = e.target.result; // 파일 내용을 가져옵니다.
            const lines = csv.split('\n'); // 줄 단위로 분리합니다.
            if (lines.length > 0) {
                const headers = lines[0].split(','); // 첫 번째 줄(헤더)을 가져옵니다.
                selectX.innerHTML = ''; // X축 선택 요소 초기화
                selectY.innerHTML = ''; // Y축 선택 요소 초기화
                selectX.add(new Option('X축 선택', '')); // 기본 옵션 추가
                selectY.add(new Option('Y축 선택', '')); // 기본 옵션 추가
                
                // 헤더를 순회하며 선택 옵션으로 추가
                headers.forEach(function(header) {
                    header = header.trim(); // 공백 제거
                    if (header) {
                        selectX.add(new Option(header, header));
                        selectY.add(new Option(header, header));
                    }
                });
            }
        };
        reader.readAsText(file); // 파일을 텍스트로 읽습니다.
        analyzeBtn.disabled = false; // 분석 버튼 활성화
        document.getElementById(boxId + 'FilePathDisplay').textContent = file.name; // 파일 경로 표시
    }
}

// 데이터 분석 요청 함수
function analyzeData(boxId) {
    console.log('analyzeData called with boxId:', boxId);
    const currentBox1 = document.getElementById(boxId + 'Box');
    if (!currentBox1) {
        console.error('Box not found:', boxId + 'Box');
        return;
    }

    // 폼 유효성 검사
    if (!validateForm(boxId)) {
        console.log('폼 유효성 검사 실패');
        return;
    }

    // 폼 데이터를 FormData 객체로 생성
    const formData = new FormData(document.getElementById(boxId + 'UploadForm'));
    console.log('formData created:', formData);

    // AJAX 요청
    $.ajax({
        url: '/dataAnalysis/analyze', // 요청 URL
        type: 'POST', // 요청 방식
        data: formData, // 전송할 데이터
        processData: false, // jQuery가 데이터를 처리하지 않도록 설정
        contentType: false, // 콘텐츠 타입을 자동으로 설정하지 않도록 설정
        success: function(response) {
            console.log('응답 데이터:', response);
            if (response.status === 'success') {
                const currentBox = document.getElementById(boxId + 'Box');
                console.log('currentBox:', currentBox);
                if (currentBox) {
                    // 기존 폼 제거
                    const oldForm = document.getElementById(boxId + 'UploadForm');
                    if (oldForm) {
                        oldForm.remove();
                        console.log('Form removed');
                    }

                    // 새로운 결과 컨테이너 생성
                    const resultContainer = document.createElement('div');
                    resultContainer.className = 'result-container';
                    console.log('Created result container');

                    // 이미지 생성 및 추가
                    const img = document.createElement('img');
                    img.className = 'result-graph';
                    img.src = response.graphPath;
                    img.alt = '분석 그래프';
                    console.log('Image source set to:', response.graphPath);
					img.onload = function() {
			            console.log('Image loaded successfully');
			        };
			        img.onerror = function() {
			            console.log('Image failed to load');
			        };
								
                    resultContainer.appendChild(img);
                    currentBox.appendChild(resultContainer);
                    console.log('Result container added to box');

                    // 새 박스 생성 (최대 9개까지)
                    const boxCount = 1;
                    console.log('Current box count:', boxCount);
                    if (boxCount < 9) {
                        createNewAnalysisBox(boxCount + 1);
                        console.log('New analysis box created with boxCount:', boxCount + 1);
                    }
                }
            }
        },
        error: function(xhr, status, error) {
            console.log('에러 상태:', status);
            console.log('에러 내용:', error);
        }
    });
}

 // 새 분석 박스 생성 함수 수정
function createNewAnalysisBox(boxNumber) {
    console.log('createNewAnalysisBox called with boxNumber:', boxNumber);

    const newBoxId = 'box' + boxNumber;
    const newBox = document.createElement('div');
    newBox.className = 'analysis-box';
    newBox.id = newBoxId + 'Box';
    console.log('newBox created with id:', newBox.id);

    newBox.innerHTML = `
        <form id="${newBoxId}UploadForm" class="upload-form">
            <div class="file-input">
                <input type="file" id="${newBoxId}FileInput" name="file" accept=".csv" onchange="handleFileUpload('${newBoxId}')">
                <p id="${newBoxId}FilePathDisplay"></p>
            </div>
            <div>
                <select id="${newBoxId}FeatureColumn" name="featureColumn" class="column-select">
                    <option value="">X축 선택</option>
                </select>
            </div>
            <div>
                <select id="${newBoxId}TargetColumn" name="targetColumn" class="column-select">
                    <option value="">Y축 선택</option>
                </select>
            </div>
            <button type="button" id="${newBoxId}AnalyzeBtn" class="analyze-btn" onclick="analyzeData('${newBoxId}')" disabled>분석 시작</button>
        </form>
    `;

    document.querySelector('.main-container').appendChild(newBox);
    console.log('newBox appended to main-container');
}

// 이벤트 위임 설정 (페이지 로드 시 한 번만 실행)
document.addEventListener('DOMContentLoaded', function() {
    document.querySelector('.main-container').addEventListener('change', function(e) {
        if (e.target.type === 'file') {
            const boxId = e.target.closest('.analysis-box').id.replace('Box', '');
            handleFileUpload(boxId);
        }
    });
});