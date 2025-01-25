let elements = {    //form의 각 입력 필드를 담고있음. DOM(Document Object Model) 요소들을 저장하는 객체
    id: document.getElementById("user_id"),
    pw: document.getElementById("password"),
    pwCheck: document.getElementById("pwCheck"),
    email: document.getElementById("email"),
    name: document.getElementById("name"),
    tel: document.getElementById("phone_number"),
    address: document.getElementById("address")
}
let patterns = {    //각 입력 필드의 유효성을 검사할 정규표현식 패턴
    id: /^[a-z0-9-_]{5,12}$/,  // 5~12자의 영문 소문자, 숫자, 특수문자(-,_)
    pw: /^(?=.*[a-zA-Z])(?=.*[~!@#$%^&*()_+-=])(?=.*[0-9]).{8,12}$/,   // 8~12자의 영문, 특수문자, 숫자 조합
    email: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/, // 이메일 형식
    name: /^[가-힣a-zA-Z]{2,10}$/,     // 2~10자의 한글 또는 영문
    birth: /^(19[0-9]{2}|20[0-9]{2})(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])$/,   // YYYYMMDD 형식의 날짜
    tel: /^010\-[0-9]{4}\-[0-9]{4}$/,   // 010-XXXX-XXXX 형식의 전화번호
    address: /^.{5,100}$/
}

let msgElements = {
    id: document.getElementById("idMsg"),
    pw: document.getElementById("pwMsg"),
    pwCheck: document.getElementById("pwCheckMsg"),
    email: document.getElementById("emailMsg"),
    name: document.getElementById("nameMsg"),
    tel: document.getElementById("telMsg"),
    address: document.getElementById("addressMsg")
}

let messages = {    //유효성 검사 실패 시 표시할 에러 메시지
    id: "5~12자리의 영문 소문자, 숫자, 특수문자(-,_)만 사용 가능합니다.",
    pw: "8~12자리의 영문자, 특수문자, 숫자를 모두 포함하여 작성해주세요.",
    pwCheck: "비밀번호가 일치하지 않거나 비밀번호 확인이 필요합니다.",
    email: "유효한 이메일 형식이 아닙니다.",
    name: "이름은 한글 또는 영어로 2~10글자여야 합니다.",
    tel: "휴대 전화 번호 형식이 아닙니다. (ex: 010-1234-1234)",
    address: "주소는 5~100자 사이로 입력해주세요."
}

//form 전체 검사 함수
function formCheck() {

    //모든 입력이 유효한지 추적하는 변수
    let isValid = true;

    //모든 입력 필드를 순회하며 검사
    for (let key in elements) {
        if (key !== 'pwCheck') {
            //입력 필드(bin)가 비어있는지 검사
            if (!patterns[key].test(elements[key].value)) {

                msgElements[key].innerHTML = messages[key];
                elements[key].focus();
                isValid = false;
            } else {
                msgElements[key].innerHTML = "";

            }
        }
    }

    //비밀번호와 비밀번호 확인 일치 검사
    if (elements.pw.value !== elements.pwCheck.value) {
        msgElements.pwCheck.innerHTML = messages.pwCheck;
        elements.pwCheck.focus();
        isValid = false;
    } else {
        msgElements.pwCheck.innerHTML = "";
    }

    return isValid;
}


function fn_idOverlap() {
        $.ajax({
            url: "/join/idOverlap",    // URL 수정
            type: "post",
            dataType: "json",
            data: {"user_id": $("#user_id").val()},  // 파라미터 이름 수정
            success: function(data) {
            	console.log(data)
                if($("#user_id").val() == null || $("#user_id").val() == "") {
                    alert("아이디를 입력하세요.");
                } else if(data == 0) {
                    $("#idOverlap").attr("value", "Y");
                    alert("사용 가능한 아이디입니다.");
                } else if(data == 1) {
                    $("#idOverlap").attr("value", "N");
                    alert("중복된 아이디입니다.");
                }
            },
            error: function(xhr, status, error) {    // 에러 처리 추가
                alert("서버 오류가 발생했습니다. 관리자에게 문의하세요.");
                console.log("Error: " + error);
			}
		});
}
    
function searchAddress() {
	new daum.Postcode({
        autoClose: true,  // 주소 선택 후 자동으로 창이 닫힙니다
        oncomplete: function(data) {
            var addr = '';

            if (data.userSelectedType === 'R') {
                addr = data.roadAddress;
            } else {
                addr = data.jibunAddress;
            }

            document.getElementById("address").value = addr;
            document.getElementById("address").focus();
        }
    }).open();
}
