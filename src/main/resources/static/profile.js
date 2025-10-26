function showProfile() {
	const profile = $('#profile')[0].files[0];
	const maxSize = 1024*1024;
	if(profile.size>maxSize) {
		alert('사진 크기는 1MB이하여야 합니다');
		$('#profile')[0].files[0] = '';
	}

	let reader = new FileReader();
	// reader.readAsDataURL()이 끝나면 onload 이벤트 발생
	reader.onload = function() {
		$('#show-profile').css('display','inline').attr('src', reader.result);
	}
	// 사용자가 <input type="file">로 선택한 파일을 Base64 인코딩된 데이터 URL(Data URL) 형태로 읽겠다(비동기 실행)
	// (data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAA...)
	reader.readAsDataURL(profile);
}

function changeProfile() {
  const formData = new FormData();
  const profile = $('#profile')[0].files[0];
  if(!profile) {
    alert("프로필 사진을 변경하세요")
    return;
  }
  formData.append('profile', profile);

  try {
    axios.patch('/api/member/profile', formData);
    // 파일 입력 필드 초기화
    $('#profile').val('');
    alert("프로필 사진을 변경했습니다");
  } catch(err) {
    alert("프로필 사진을 변경하지 못 했습니다");
    console.log(err);
  }
}