$(document).ready(function () {
    $("button").click(function () {
        console.log("111");
        let reg = /^[a-z0-9]{6,12}$/
        let username = $("input[name='username']").val();
        if (!reg.test(username)) {
            alert("账号必须时小写英文和数字,长度6到12位")
            return
        }

        let register_code = $("input[name='register_code']").val();
        if (!register_code) {
            alert("请输入注册码!")
            return
        }
        doPost('/wchatgpt-be/api/user/delete', { username: username, register_code: register_code }, function (data) {
            if (data.success) {
                alert("删除成功!");
            } else {
                alert("删除失败," + data.errorMsg);
            }
        })
    });
});
