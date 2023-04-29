function req_msg(msg) {
    if (!msg) {
        return
    }
    $("#loadDlg").modal('toggle')

    let token = localStorage.getItem("token")
    if (!token) {
        alert("用户已经过期,请登录")
        return;
    }
    //$(".left chat-message-content")
    let userDivs = $(".chat-history .left .chat-message-content");
    let assistantDivs = $(".chat-history .right .chat-message-content");
    let messages = []
    for (let i = 0; i < userDivs.length; i++) {
        messages.push({ content: userDivs[i].innerHTML.trim(), role: "user" })
        messages.push({ content: assistantDivs[i].innerHTML.trim(), role: "assistant" })
    }
    messages.push({ content: msg, role: "user" })
    doPost("/wchatgpt-be/api/openai/chat", { messages: messages }, function (data) {
        if (data.success) {
            let htmlText;
            if (msg.indexOf("图片") >= 0) {
                htmlText = data.data.msg;
            } else {
                htmlText = marked(data.data.msg);
                // 对代码进行高亮显示
                $('pre code').each(function (i, block) {
                    hljs.highlightBlock(block);
                });
            }


            //let highlightedText = hljs.highlightAuto(htmlText).value;


            $(".chat-history").append("<div class=\"chat-message left\">\n" + "    <div class=\"chat-message-content\">\n" + msg + "</div>\n" + "</div>\n" + "<div class=\"chat-message right\">\n" + "    <div class=\"chat-message-content\">\n" + htmlText + "</div>\n" + "</div>");

            $(".my_input").val("")
            $("#loadDlg").modal('toggle')
        } else {
            $("#loadDlg").modal('toggle')
            alert(data.errorMsg);
        }
    })
}

function resizeChatBox() {
    $('.chat-box').height($(window).height());
    //$('.chat-history').height($('.chat-box').height() - $(".chat-input").outerHeight() - $(".chat-top").outerHeight());
}

function resizeUI() {
    // adjust chat box height on window resize
    $(window).resize(function () {
        resizeChatBox();
    });

// adjust chat box height on initial load
    $(document).ready(function () {
        resizeChatBox();
    });

// adjust chat box height on input focus
    $('.chat-input textarea').focus(function () {
        resizeChatBox();
    });

// adjust chat box height on input blur
    $('.chat-input textarea').blur(function () {
        resizeChatBox();
    });
}

$(function () {
    resizeUI();

    $("#sendBtn").click(function () {
        req_msg($(".my_input").val())
    });

    $('body').keydown(function (e) {
        if (e.keyCode === 13) {
            $("#sendBtn").click()
        }
    });


    $("#logoutBtn").click(function () {
        doPost('/wchatgpt-be/api/auth/logout', { token: localStorage.getItem("token") }, function (data) {
            if (data.success) {
                localStorage.removeItem("userInfo", data.data.username);
                localStorage.removeItem("token", data.data.token);
                window.location.href = "/wchatgpt-be/login.html"; // 跳转到登录页面
            } else {
                alert(data.errorMsg)
            }
        })
    });

    $("#resetPasswordBtn").click(function () {
        window.location.href = "/wchatgpt-be/resetPassword.html";
    });

    doPost('/wchatgpt-be/api/auth/checkLogin', { token: localStorage.getItem("token") }, function (data) {
        if (data.success) {
            localStorage.setItem("userInfo", data.data.username);
            localStorage.setItem("token", data.data.token);
        } else {
            localStorage.removeItem("userInfo");
            localStorage.removeItem("token");
            alert("您的登录会话已经过期，请重新登录！")
        }
    })
    if (localStorage.getItem("userInfo") == null) { // 判断本地存储中是否有用户登录信息
        window.location.href = "/wchatgpt-be/login.html"; // 跳转到登录页面
    }
    $(".chat-username").html(localStorage.getItem("userInfo"))
    //$("#userInfo").text(localStorage.getItem("userInfo"));

});
