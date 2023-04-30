$(document).ready(function () {
    function getImageCaptcha(){
        doGet('/wchatgpt-be/api/auth/getCaptcha',function (data) {
            if (data.success) {
               $("img[name='captcha_img']").attr("src", data.data.captchaPath);
               $("input[name='captcha_key']").val(data.data.captchaKey);
            } else {
                alert("注册失败," + data.errorMsg);
            }
        });
    }

    $("img[name='captcha_img']").click(function () {
        console.log("222");
        getImageCaptcha();
    });

    getImageCaptcha();
});
