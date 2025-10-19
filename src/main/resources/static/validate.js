// 오류메시지 출력 함수 : (value, pattern, message, element) -> 입력값, 패턴, 오류 메시지, 오류메시지를 출력할 element
const check = (value, pattern, message, $element)=>{
	if(value=="") {
		$element.text("필수 입력입니다");
		return false;
	}
	if(pattern.test(value)==false) {
		$element.text(message);
		return false;
	}
	return true;
}

async function checkUsernameWithAvailability() {
  const username = $("#username").val().toUpperCase();
  const $usernameMsg = $("#username-msg");
  $usernameMsg.text("");
  const pattern = /^[0-9A-Z]{6,10}$/;

	$("#username").val(username);
	const checkResult = check(username, pattern, "아이디는 대문자와 숫자 6~10자입니다", $usernameMsg);
	if(!checkResult)
	  return false;
	try {
		await axios.get('/api/member/check-username?username=' + username);
		return true;
	} catch(err) {
		$usernameMsg.text('사용중인 아이디입니다');
		return false;
	}
}

function checkUsername() {
  const username = $("#username").val().toUpperCase();
  $("#username").val(username);
  $("#username-msg").text("");
  return check(username, /^[0-9A-Z]{6,10}$/, "아이디는 대문자와 숫자 6~10자입니다", $("#username-msg"));
}

function checkPassword(target) {
  const $password = target
  const $passwordMsg = $password.next();
	$passwordMsg.text("");
	return check($password.val(), /^[A-Za-z0-9]{6,10}$/, "비밀번호는 영숫자 6~10자입니다", $passwordMsg);
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
	const email = $("#email").val();
	$('#email-msg').text('');
	const pattern = /^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*.[a-zA-Z]{2,3}$/i;
	return check(email, pattern, "정확한 이메일을 입력하세요", $("#email-msg"))
}