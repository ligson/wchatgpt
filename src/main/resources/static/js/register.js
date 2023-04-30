$(document).ready(function () {
    $("button").click(function () {
        console.log("111");
        let reg = /^[a-z0-9]{6,12}$/
        let username = $("input[name='username']").val();
        if (!reg.test(username)) {
            alert("账号必须时小写英文和数字,长度6到12位")
            return
        }
        let password = $("input[name='password']").val();
        let password2 = $("input[name='password2']").val();
        let pwdReg = /^[a-z0-9_]{8,}$/
        if (!pwdReg.test(password)) {
            alert("密码必须时小写英文和数字,长度至少8位")
            return
        }
        if (password !== password2) {
            alert("两次密码输入不一致")
            return
        }
        let captcha_code = $("input[name='captcha_code']").val();
        if (!captcha_code) {
            alert("请输入验证码!")
            return
        }

        let captcha_key = $("input[name='captcha_key']").val();

        doPost('/wchatgpt-be/api/auth/register',
        { username: username, password: password, captcha_code: captcha_code, captcha_key: captcha_key}
        ,function (data) {
            if (data.success) {
                alert("注册成功!");
            } else {
                alert("注册失败," + data.errorMsg);
            }
        });
    });
});
