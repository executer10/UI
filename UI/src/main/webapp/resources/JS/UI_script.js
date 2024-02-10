let nameInput = document.querySelector("#user_name");
let idInput = document.querySelector("#user_id");
let passwordInput = document.querySelector("#user_password");
let PWCheck = document.querySelector("#user_PWCheck");
let Email = document.querySelector("#user_email");
let check = true;

function checkName(){
	if (check == true && nameInput.value.length < 1) {
        alert("이름을 입력해주세요.");
        nameInput.select();
        check = false;
    }
}
function checkId(){
	if (check == true && (idInput.value == null || idInput.value.length < 4 || idInput.value.length > 15)) {
        alert("아이디는 4~15자리의 문자나 숫자로 입력해주세요.");
        idInput.select();
        check = false;
    }
}

function checkPassword(){
	if (check == true && passwordInput.value.length < 7) {
        alert("비밀번호는 8자리 이상 입력해야합니다.");
        passwordInput.value="";
        check = false;
    }
}

function checkPWCheck() {
    if (check == true && passwordInput.value != PWCheck.value){
        alert("비밀번호가 일치하지 않습니다.");
        PWCheck.value="";
        check = false;
    }
}

function checkEmail() {
    if (check == true && Email.value.length < 1){
        alert("이름을 입력하세요");
        Email.value="";
        check = false;
    }
}

function checkAll() {
    checkName();
    checkId();
    checkPassword();
    checkPWCheck();
    checkEmail();
}