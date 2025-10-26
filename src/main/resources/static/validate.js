const patterns = {
  username: /^[a-z0-9]{6,10}$/,
  password: /^[A-Za-z0-9]{6,10}$/,
  email: /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i
};

const errors = {
  username: "아이디는 소문자와 숫자 6~10자입니다",
  password: "비밀번호는 영숫자 6~10자입니다",
  email:"정확한 이메일을 입력하세요"
};

function check(name, kor,  $input, $errorSpan) {
  const value = $input.val();
  $errorSpan.text("");
  if(value=="") {
  	$errorSpan.text(kor + " : 필수 입력입니다");
  	return false;
  }
  if(!patterns[name].test(value)) {
    $errorSpan.text(errors[name]);
    return false;
  }
  return true;
}

function checkUsername() {
  return check("username", "아이디", $("#username"), $("#username-msg"));
}

async function checkUsernameWithAvailability() {
  const result = checkUsername();
  if(!result)
    return false;
	try {
		await axios.get('/api/member/check-username?username=' + $('#username').val());
		return true;
	} catch(err) {
		$("#username-msg").text('사용중인 아이디입니다');
		return false;
	}
}

function checkPassword($input) {
  return check("password", "비밀번호", $input, $input.next());
}

function checkPassword2() {
	const password = $('#password').val();
	const password2 = $('#password2').val();
	$('#password2-msg').text('');
	if(password2==='') {
		$('#password2-msg').text('새 비밀번호를 입력하세요');
		return false;
	}
	if(password!=password2) {
		$('#password2-msg').text('새 비밀번호가 일치하지 않습니다');
		return false;
	}
	return true;
}

function checkEmail() {
	return check("email", "이메일", $("#email"), $("#email-msg"));
}