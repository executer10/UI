let id = document.getElementById("id");
let pw = document.getElementById("pw");
let name = document.getElementById("name");
let birth = document.getElementById("birth");
let tel = document.getElementById("tel");

function formCheck(frm){
    if(frm.id.value == ""){
        alert("아이디를 입력하세요.");
        return false;
    }

    if(frm.pw.value == ""){
        alert("비밀번호를 입력하세요.");
        return false;
    }

    if(frm.PWCheck.value == ""){
        alert("재확인을 위한 비밀번호를 입력하세요.");
        return false;
    }

    if(frm.pw.value != frm.PWCheck.value){
        alert("비밀번호가 일치하지 않습니다.");
        return false;
    }

    if(frm.name.value ==""){
        alert("이름을 입력하세요");
    }

    if(frm.birth.value == ""){
        alert("생년월일을 입력하세요.");
    }

    if(frm.tel.value==""){
        alert("전화번호를 입력하세요.");
    }

    if(frm.idOverlap.value=="N"){
        alert("아이디 중복확인 버튼을 눌러주세요.");
    }
}
/* --------------------------------------------------------------------- */

function checkId(){
    //5~12자리의 영문자와 소문자, -, _ 조합
    let pattern = /^[a-z0-9-_]{5,12}$/;
    
    if(id.value == ""){
        return false;
    }else if(!pattern.test(id.value)){
        alert("5~12자리의 영문 소문자, 숫자, 특수문자(-,_)만 사용 가능합니다.");
        return false;
    }
    return true;
}
function checkPw(){
    //8~12글자의 영문자, 특수문자 ,숫자를 모두 포함하여 작성
    let pattern = /^(?=.*[a-zA-Z])(?=.*[~!@#$%^&*()_+/-=])(?=.*[0-9]).{8,12}$/;

    if(pw.value == ""){
        return false;
    }else if(!pattern.test(pw.value)){
        alert("8~12자리의 영문자, 특수문자, 숫자 모두를 포함하여 작성하세요.");
        return false;
    }
    return true;
}
function checkName(){
    //한글, 영어 2~10글자
    let pattern = /^[가-힣a-zA-Z]{2,10}$/;

    if(name.value == ""){
        return false;
    }else if(!pattern.test(name.value)){
        alert("한글, 영어 2~10글자만 사용 가능합니다.");
        return false;
    }

    return true;
}

function checkBirth(){
    // yyyymmdd 형식
    let pattern = /^(19[0-9][0-9]|20\d{2})(0[1-9]|1[0-2])(0[1-9]|[1-2][0-9]|3[0-1])$/;
    
    if(birth.value == ""){
        return false;
    }else if(!pattern.test(birth.value)){
        alert("날짜 형식이 아닙니다(ex: 20200101)");
        return false;
    }
    return true;
}
function checkTel() {
	// 010-1234-1234 형식
	let pattern = /^010\-[0-9]{4}\-[0-9]{4}$/;
	    	
	if(tel.value == ""){
		return false;
	}else if(!pattern.test(tel.value)) {
		alert("휴대 전화 번호 형식이 아닙니다.(ex: 010-1234-1234)");
		return false;
	}
	    	
	return true;
}