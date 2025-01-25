// 페이지가 로드되면 이벤트 리스너를 추가
document.addEventListener("DOMContentLoaded", function () {

    // 각 언어별로 HTML 요소 ID를 모아놓은 객체
    const languageElements = {
        HTML_main: [],
        CSS_main: ["CSS_boxModel_background", "CSS_layout_grid_flax", "CSS_textStyling", "CSS_transition_animation", "CSS_media_table", "CSS_overflow_scroll", "CSS_transform_addProperties", "CSS_cursor_pointer"],
        JS_main: ["JS_arithmeticList", "JS_inOutput", "JS_Implicit_DateObject", "JS_Implicit_MathObject"],
        Java_main: ["Java_Variables_And_Type", "Java_Operator", "Java_Conditional_Statements_And_Loops", "Java_Reference_Type", "Java_Class", "Java_Inheritance", "Java_Interface", "Java_NestedClass_NestedInterface", "Java_ExceptionHandling", "Java_CollectionFramework"],
        Python_main: ["Python_Built_In_Function", "Python_Variables_And_DataTypes", "Python_Data_Structures_Basics", "Python_Numpy", "Python_Pandas", "Python_graph", "Python_Public_Data_Utilization", "Python_Machine_Learning"],
        MySQL_main: ["MySQL_CommandList", "MySQL_DDL", "MySQL_DML", "MySQL_DCL_and_TCL", "MySQL_DefaultCMD"],
        JSP_main: ["JSP_Default", "JSP_Implicit_Object", "JSP_Implicit_Object_Scope", "JSP_Cookie", "JSP_Database", "JSP_Session", "JSP_ActionTag", "JSP_ExpressionLanguage", "JSP_JSTL", "JSP_Servlet", "JSP_Model1_JDBConnect", "JSP_Model1_JSFunction", "JSP_Model1_MemberDTO_BoardDTO", "JSP_Model1_MemberDAO", "JSP_Model1_BorderDAO", "JSP_Model1_PageHandler", "JSP_Model1_HeaderLink", "JSP_Model1_Login_AND_Process", "JSP_Model1_Join_AND_Process"],
        Spring_main: ["Spring_Setting", "Spring_Controller_Guide"],
        Github_main: []
    };

    // <main> 요소를 선택해 저장 (이곳에 콘텐츠를 추가)
    const main = document.querySelector("main");

    // 특정 파일을 불러오는 비동기 함수 정의 (html 또는 excel 파일)
    async function loadFileContent(ContentsId, fileType) {

        // 불러올 파일의 확장자와 폴더 경로 설정
        const fileExtension = fileType === 'html' ? 'html' : 'xlsx';
        const folderPath = fileType === 'html' ? '/resources/languageContents' : '/resources/LanguageContentsExcelFile';

        try {
            // 파일을 서버에서 가져오기
            const response = await fetch(`${folderPath}/${ContentsId}.${fileExtension}`);

            // 파일을 찾지 못한 경우 예외 처리
            if (!response.ok) {
                throw new Error(`${fileType} 파일을 찾을 수 없습니다`);
            }

            // html 파일을 불러오는 경우
            if (fileType === 'html') {
                // HTML 텍스트 데이터를 main 요소에 추가
                const data = await response.text();
                main.innerHTML = `<section class="${ContentsId}">${data}</section>`;
            } 
            else {
				// Excel 파일을 불러오는 경우
				const data = await response.arrayBuffer();  // 배열 버퍼 형식으로 데이터 가져오기
				const workbook = XLSX.read(new Uint8Array(data), { type: 'array' });    // XLSX 라이브러리로 읽기
				const firstSheetName = workbook.SheetNames[0];  // 첫 번째 시트 이름 가져오기
				const worksheet = workbook.Sheets[firstSheetName];  // 해당 시트를 변수에 저장
				const jsonData = XLSX.utils.sheet_to_json(worksheet, { header: 1 });    // 시트 데이터를 JSON으로 변환

                // 테이블 생성
                const table = document.createElement('table');
                table.className = 'excel-table';

                // JSON 데이터를 테이블에 추가
                jsonData.forEach((row, index) => {
                    const tr = table.insertRow();   // 새 행 추가
                    row.forEach(cell => {
                        // 첫 행은 <th>로, 나머지는 <td>로 셀 생성
                        const el = index === 0 ? document.createElement('th') : document.createElement('td');
                        el.textContent = cell || '';    // 셀 내용 추가
                        tr.appendChild(el);
                    });
                });

                // 이미 기존에 테이블이 있으면 제거 후 새 테이블 추가
                const existingTable = main.querySelector('.excel-table');
                if (existingTable) {
                    existingTable.remove();
                }
                main.appendChild(table);
            }
        } catch (error) {
            // 오류가 발생하면 콘솔에 오류 메시지 출력 및 화면에 오류 내용 표시
            console.error(`${fileType} 파일 로드 실패 (${ContentsId}):`, error);
            if (fileType === 'html') {
                main.innerHTML = `<section class="${ContentsId}">콘텐츠를 불러오는데 실패했습니다.</section>`;
            }
        }
    }

    // HTML과 Excel 파일 모두 로드하는 함수
    async function loadContent(ContentsId) {
        await loadFileContent(ContentsId, 'html');  // HTML 파일 로드
        await loadFileContent(ContentsId, 'excel'); // Excel 파일 로드
    }

    // 현재 main 요소의 ID에 해당하는 콘텐츠가 있는지 확인
    if (languageElements[main.id]) {
        loadContent(main.id);   // 해당 ID의 콘텐츠 로드

        // 각 콘텐츠 요소에 클릭 이벤트 리스너 추가
        languageElements[main.id].forEach(id => {
            const element = document.getElementById(id);
            if (element) {
                // 클릭 시 해당 콘텐츠를 로드
                element.addEventListener("click", () => loadContent(id));
            }
		})
	}
});