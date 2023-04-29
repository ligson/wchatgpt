$(document).ready(function () {
    $("button").click(function () {
        console.log("111");
        let reg = /^[a-z0-9]{6,12}$/
        let username = $("input[name='username']").val();
        if (!reg.test(username)) {
            alert("账号必须时小写英文和数字,长度6到12位")
            return
        }
        let oldPassword = $("input[name='oldPassword']").val();
        let newPassword = $("input[name='newPassword']").val();
        let password2 = $("input[name='password2']").val();
        let pwdReg = /^[a-z0-9_]{8,}$/
        if (!pwdReg.test(newPassword)) {
            alert("密码必须时小写英文和数字,长度至少8位")
            return
        }
        if (newPassword !== password2) {
            alert("两次密码输入不一致")
            return
        }

        doPost('/wchatgpt-be/api/user/resetPassword', {
            username: username,
            oldPassword: oldPassword,
            newPassword: newPassword
        }, function (data) {
            if (data.success) {
                alert("成功!");
            } else {
                alert("失败," + data.errorMsg);
            }
        })
    });
});
