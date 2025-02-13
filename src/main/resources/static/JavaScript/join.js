let elements = {};
        let patterns = {};
        let msgElements = {};
        let messages = {};

        document.addEventListener('DOMContentLoaded', function() {
            elements = {    
                userId: document.getElementById("userId"),
                userPw: document.getElementById("userPw"),
                pwCheck: document.getElementById("pwCheck"),
                email: document.getElementById("email"),
                name: document.getElementById("name"),
                phoneNumber: document.getElementById("phoneNumber"),
                postcode: document.getElementById("postcode"),
                address: document.getElementById("address"),
                addressDetail: document.getElementById("addressDetail")
            };
            
            patterns = {    
                userId: /^[a-z0-9-_]{5,20}$/,
                userPw: /^(?=.*[a-zA-Z])(?=.*[~!@#$%^&*()_+\-=\[\]{};':"\\|,.<>\/?]).{8,20}$/,
                email: /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,30}$/,
                name: /^[가-힣a-zA-Z]{2,10}$/,
                phoneNumber: /^010\-[0-9]{4}\-[0-9]{4}$/,
                postcode: /^\d{5}$/,
                address: /^.{5,200}$/,
                addressDetail: /^.{0,200}$/
            };

            msgElements = {
                userId: document.getElementById("userIdMsg"),
                userPw: document.getElementById("userPwMsg"),
                pwCheck: document.getElementById("pwCheckMsg"),
                email: document.getElementById("emailMsg"),
                name: document.getElementById("nameMsg"),
                phoneNumber: document.getElementById("phoneNumberMsg"),
                postcode: document.getElementById("postcodeMsg"),
                address: document.getElementById("addressMsg"),
                addressDetail: document.getElementById("addressDetailMsg")
            };

            messages = {    
                userId: "5~20자리의 영문 소문자, 숫자, 특수문자(-,_)만 사용 가능합니다.",
                userPw: "8~20자리의 영문자, 특수문자, 숫자를 모두 포함하여 작성해주세요.",
                pwCheck: "비밀번호가 일치하지 않거나 비밀번호 확인이 필요합니다.",
                email: "유효한 이메일 형식이 아닙니다.",
                name: "이름은 한글 또는 영어로 2~10글자여야 합니다.",
                phoneNumber: "휴대 전화 번호 형식이 아닙니다. (ex: 010-1234-1234)",
                postcode: "우편번호는 5자리 숫자여야 합니다.",
                address: "주소는 5~200자 사이로 입력해주세요.",
                addressDetail: "상세주소는 200자 이내로 입력해주세요."
            };

            for (let key in elements) {
                if (elements[key]) {
                    elements[key].addEventListener('input', function() {
                        if (key !== 'pwCheck' && key !== 'addressDetail') {
                            if (!patterns[key].test(this.value)) {
                                msgElements[key].innerHTML = messages[key];
                            } else {
                                msgElements[key].innerHTML = "";
                            }
                        } else if (key === 'pwCheck') {
                            if (this.value !== elements.userPw.value) {
                                msgElements.pwCheck.innerHTML = messages.pwCheck;
                            } else {
                                msgElements.pwCheck.innerHTML = "";
                            }
                        } else if (key === 'addressDetail') {
                            if (this.value.length > 200) {
                                msgElements.addressDetail.innerHTML = messages.addressDetail;
                            } else {
                                msgElements.addressDetail.innerHTML = "";
                            }
                        }
                    });
                }
            }

            $("#userId").on('input', function(){
                $("#userIdOverlap").attr("data-overlap", "N");
                $("#userIdMsg").text("");
            });

            $(".signup-form").on('submit', function(e){
                if($("#userIdOverlap").attr("data-overlap") !== "Y"){
                    alert("아이디 중복 확인을 해주세요.");
                    e.preventDefault();
                }
            });
        });

        function formCheck() {
            let isValid = true;

            for (let key in elements) {
                if (key !== 'pwCheck' && key !== 'addressDetail') {
                    if (!patterns[key].test(elements[key].value)) {
                        msgElements[key].innerHTML = messages[key];
                        elements[key].focus();
                        isValid = false;
                    } else {
                        msgElements[key].innerHTML = "";
                    }
                }
            }

            if (elements.userPw.value !== elements.pwCheck.value) {
                msgElements.pwCheck.innerHTML = messages.pwCheck;
                elements.pwCheck.focus();
                isValid = false;
            } else {
                msgElements.pwCheck.innerHTML = "";
            }

            if (elements.addressDetail.value.length > 200) {
                msgElements.addressDetail.innerHTML = messages.addressDetail;
                elements.addressDetail.focus();
                isValid = false;
            }

            return isValid;
        }

        function fn_idOverlap() {
            $.ajax({
                url: "/join/idOverlap",
                type: "POST",
                dataType: "json",
                data: { "userId": $("#userId").val() },
                success: function(data) {
                    let userId = $("#userId").val().trim();
                    if(userId === "") {
                        alert("아이디를 입력하세요.");
                    } else if(data === 0) {
                        $("#userIdOverlap").attr("data-overlap", "Y");
                        alert("사용 가능한 아이디입니다.");
                    } else if(data === 1) {
                        $("#userIdOverlap").attr("data-overlap", "N");
                        alert("중복된 아이디입니다.");
                    }
                },
                error: function(xhr, status, error) {
                    alert("서버 오류가 발생했습니다. 관리자에게 문의하세요.");
                    console.log("Error: " + error);
                }
            });
        }

        function searchAddress() {
            new daum.Postcode({
                autoClose: true,
                oncomplete: function(data) {
                    document.getElementById("postcode").value = data.zonecode;
                    var addr = '';
                    if (data.userSelectedType === 'R') {
                        addr = data.roadAddress;
                    } else {
                        addr = data.jibunAddress;
                    }
                    document.getElementById("address").value = addr;
                    document.getElementById("addressDetail").focus();
                }
            }).open();
        }