function req_msg(msg) {
    if (!msg) {
        return
    }
    $("#loadDlg").modal('toggle')
    $.ajax({
        url: "/chat",
        method: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({ messages: [{ content: msg, role: "user" }] }),
        success: function (data) {
            if (data.success) {
                let htmlText = marked(data.msg);
                // 对代码进行高亮显示
                $('pre code').each(function (i, block) {
                    hljs.highlightBlock(block);
                });

                //let highlightedText = hljs.highlightAuto(htmlText).value;


                $(".chat-history").append("<div class=\"chat-message left\">\n" +
                    "                <div class=\"chat-message-content\">\n" +
                    msg +
                    "                </div>\n" +
                    "            </div>\n" +
                    "            <div class=\"chat-message right\">\n" +
                    "                <div class=\"chat-message-content\">\n" +
                    htmlText +
                    "                </div>\n" +
                    "            </div>");

                $(".my_input").val("")
                $("#loadDlg").modal('toggle')
            } else {
                alert("失败");
            }
        }
    })
}

$(function () {
    // adjust chat box height on window resize
    $(window).resize(function () {
        $('.chat-box').height($(window).height());
    });

// adjust chat box height on initial load
    $(document).ready(function () {
        $('.chat-box').height($(window).height());
    });

// adjust chat box height on input focus
    $('.chat-input textarea').focus(function () {
        $('.chat-box').height($(window).height());
    });

// adjust chat box height on input blur
    $('.chat-input textarea').blur(function () {
        $('.chat-box').height($(window).height());
    });

    $("#sendBtn").click(function () {
        req_msg($(".my_input").val())
    });
});
