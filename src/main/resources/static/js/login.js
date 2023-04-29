function getCustomInfo() {
    doGet("/wchatgpt-be/api/sys/customerInfo", function (data) {
        if (data.success) {
            $("#wx_id").html(data.data.wxId);
            $("#wx_qrcode").attr("src", data.data.wxQrCode);
            if (data.data.wxId !== 'abroket') {
                $(".user_register").html("登录");
            }
        }
    });
}

function checkToken() {
    let token = localStorage.getItem("token");
    if (token) {
        doPost("/wchatgpt-be/api/auth/checkLogin", { token: token }, function (data) {
            if (data.success) {
                localStorage.setItem("userInfo", data.data.username);
                localStorage.setItem("token", data.data.token);
                window.location.href = "/wchatgpt-be/index.html"; // 跳转到登录页面
            } else {
                localStorage.removeItem("userInfo");
                localStorage.removeItem("token");
                //alert(data.errorMsg);
                //alert("您的登录会话已经过期，请重新登录！")
            }
        })
    }
}

$(document).ready(function () {
    checkToken();

    getCustomInfo();

    $("#loginButton").click(function () {
        console.log("111");
        let username = $("input[name='username']").val();
        let password = $("input[name='password']").val();
        if (!username || !password) {
            alert("请输入用户名或者密码")
            return
        }
        localStorage.removeItem("token");
        doPost("/wchatgpt-be/api/auth/login", { username: username, password: password }, function (data) {
            if (data.success) {
                localStorage.setItem("userInfo", data.data.username);
                localStorage.setItem("token", data.data.token);
                window.location = "/wchatgpt-be/index.html";
            } else {
                localStorage.removeItem("userInfo");
                localStorage.removeItem("token");
                alert(data.errorMsg);
            }
        })
    });
});
