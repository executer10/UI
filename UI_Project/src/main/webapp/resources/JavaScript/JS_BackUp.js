// 웹 페이지의 모든 HTML 요소가 로드된 후에 실행되는 이벤트 리스너
document.addEventListener("DOMContentLoaded", function () {

    // 프로그래밍 언어별 메인 페이지와 하위 페이지들의 ID를 저장하는 객체
    // 각 메인 페이지(예: HTML_main)는 키가 되고, 
    // 배열 안에는 해당 언어의 하위 페이지 ID들이 저장됩니다
    const languageElements = {
        HTML_main: [],
        CSS_main: ["CSS_boxModel_background", "CSS_layout_grid_flax", "CSS_textStyling", "CSS_transition_animation", "CSS_media_table", "CSS_overflow_scroll", "CSS_transform_addProperties", "CSS_cursor_pointer"],
        JS_main: ["JS_arithmeticList", "JS_inOutput", "JS_Implicit_DateObject", "JS_Implicit_MathObject"],
        Java_main: ["Java_Variables_And_Type", "Java_Operator", "Java_Conditional_Statements_And_Loops", "Java_Reference_Type", "Java_Class", "Java_Inheritance", "Java_Interface", "Java_NestedClass_NestedInterface", "Java_ExceptionHandling", "Java_CollectionFramework"],
        MySQL_main: [],
        Oracle_main: [],
        JSP_main: [],
        Spring_main: [],
        Python_main: ["Python_Built_In_Function", "Python_Variables_And_DataTypes", "Python_Data_Structures_Basics", "Python_Numpy", "Python_Pandas", "Python_graph", "Python_Public_Data_Utilization", "Python_Machine_Learning"]
    };

    // 웹 페이지의 main 태그를 찾아서 저장
    const main = document.querySelector("main");

    // fetch: 서버로부터 파일을 비동기적으로 가져오는 함수
    // `languageContents/${ContentsId}.html`: 실제 불러올 HTML 파일의 경로
    function loadContent(ContentsId) {
        //fetch : 파일을 비동기적으로 가져옴
        fetch(`languageContents/${ContentsId}.html`)
            // 서버의 응답을 텍스트 형식으로 변환
            .then(function (response) {
                return response.text();
            })
            // 변환된 텍스트를 main 태그 안에 삽입
            .then(function (data) {
                main.innerHTML = `<section class="${ContentsId}">${data}</section>`;
                // HTML 내용을 로드한 후 Excel 파일도 함께 로드
                loadExcelContent(ContentsId);
            })
            // 오류 발생 시 에러 메시지를 표시
            .catch(function (error) {
                console.error(`Failed to load content for ${ContentsId}:`, error);
                main.innerHTML = `<section class="${ContentsId}">콘텐츠를 불러오는데 실패했습니다.</section>`;
            });
    }

    // Excel 파일을 로드하고 테이블로 변환하는 함수
    function loadExcelContent(ContentsId) {
        // Excel 파일을 가져오기
        fetch(`LanguageContentsExcelFile/${ContentsId}.xlsx`)
            .then(response => {
                if (!response.ok) {
                    throw new Error('Excel 파일을 찾을 수 없습니다');
                }
                return response.arrayBuffer();
            })
            // Excel 데이터를 테이블로 변환
            .then(data => {
                const workbook = XLSX.read(new Uint8Array(data), { type: 'array' });
                const firstSheetName = workbook.SheetNames[0];
                const worksheet = workbook.Sheets[firstSheetName];
                const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });

                // HTML 테이블 생성 및 데이터 삽입
                const table = document.createElement('table');
                table.className = 'excel-table'; // 스타일링을 위한 클래스 추가
                jsonData.forEach((row, index) => {
                    const tr = table.insertRow();
                    row.forEach(cell => {
                        // 첫 번째 행은 헤더로 처리
                        const el = index === 0 ? document.createElement('th') : document.createElement('td');
                        el.textContent = cell || '';
                        tr.appendChild(el);
                    });
                });

                // 기존 테이블이 있다면 제거하고 새 테이블 추가
                const existingTable = main.querySelector('.excel-table');
                if (existingTable) {
                    existingTable.remove();
                }

                // 새 테이블 추가
                main.appendChild(table);
            })
            .catch(() => {
                // Excel 파일이 없는 경우 테이블 제거
                console.log(`Excel 파일이 없거나 로드할 수 없습니다: ${ContentsId}`, error);
            });
    }

    // 현재 페이지가 메인 페이지인 경우 실행
    if (languageElements[main.id]) {
        // 해당 메인 페이지의 콘텐츠를 로드
        loadContent(main.id);

        // 해당 메인 페이지의 모든 하위 페이지에 대해 클릭 이벤트 리스너 추가
        // 클릭하면 해당 페이지의 콘텐츠를 로드하도록 설정
        languageElements[main.id].forEach(id => {
            const element = document.getElementById(id);
            if (element) {
                element.addEventListener("click", () => loadContent(id));
            }
        });
    }
});