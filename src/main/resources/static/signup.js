function showProfile() {
	const profile = $('#profile')[0].files[0];
	const maxSize = 1024*1024;
	if(profile.size>maxSize) {
		alert('사진 크기는 1MB이하여야 합니다');
		$('#profile')[0].files[0] = '';
	}
	// 파일리더 객체의 기본 작업대상은 this
	let reader = new FileReader();
	// 파일을 로딩했을 때 동작할 콜백함수 지정
	reader.onload = function() {
		$('#show-profile').css('display','inline').attr('src', reader.result);
	}
	// 파일을 로딩한다
	reader.readAsDataURL(profile);
}

async function doSignup() {
  const formData = new FormData($("#signup-form")[0]);

	for(const key of formData.keys())
		console.log(key);
	for(const value of formData.values())
		console.log(value);

  const r1 = await checkUsernameWithAvailability();
	const r2 = checkPassword($('#password'));
	const r3 = checkPassword2();
	const r4 = checkEmail();
	if((r1 && r2 && r3 && r4) == false)
  	return false;
  try {
    axios.post('/api/member/new', formData);
    alert("회원으로 가입되었습니다");
    location.href = "/member/login"
  } catch(err) {
    alert("회원 가입에 실패했습니다");
    console.log(err);
  }
}