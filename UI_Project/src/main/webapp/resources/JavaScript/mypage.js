function WithdrawalMembership() {
    if (confirm('정말로 탈퇴하시겠습니까?')) {
        $.ajax({
            url: '/remove',
            type: 'POST',
            //전송 데이터
            data: {
                id: '${sessionScope.id}'
            },
            //성공시
            success: function(response) {
                alert('회원 탈퇴가 완료되었습니다.');
                window.location.href = '/index';
            },
            //실패시
            error: function(xhr, status, error) {
                alert('회원 탈퇴 처리 중 오류가 발생했습니다.');
                console.error(error);
            }
        });
    }
}