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
        let register_code = $("input[name='register_code']").val();
        if (!register_code) {
            alert("请输入注册码!")
            return
        }

        doPost('/wchatgpt-be/api/auth/register',{ username: username, password: password, register_code: register_code },function (data) {
            if (data.success) {
                alert("注册成功!");
            } else {
                alert("注册失败," + data.errorMsg);
            }
        })
        $.ajax({
            type: "POST",
            url: '/wchatgpt-be/api/auth/register',
            contentType: "application/json",
            dataType: 'json',
            data: JSON.stringify({ username: username, password: password, register_code: register_code }),
            success: function (data) {

            },

        });
    });
});
