function req_msg(msg) {
    $.ajax({
        url: "/chat",
        method: "POST",
        dataType: "json",
        contentType: "application/json",
        data: JSON.stringify({ prompt: msg }),
        success: function (data) {
            if (data.success) {
                let converter = new showdown.Converter();
                let html = converter.makeHtml(data.msg);

                $(".content").append("<div class=\"msg_item\">\n" +
                    "            <div class=\"msg_me\">\n" +
                    "                <div class=\"msg_me_icon\">\n" +
                    "                    <img src=\"img/user.jpeg\"/>\n" +
                    "                </div>\n" +
                    "                <div class=\"msg_me_msg\">" + msg + "</div>\n" +
                    "            </div>\n" +
                    "            <div class=\"msg_chat\">\n" +
                    "                <div class=\"msg_chat_icon\">\n" +
                    "                    <img src=\"img/chatgpt.png\"/>\n" +
                    "                </div>\n" +
                    "                <div class=\"msg_chat_msg\">" + html + "</div>\n" +
                    "            </div>\n" +
                    "        </div>")
                $(".my_input").val("")
            } else {
                alert("失败");
            }
        }
    })
}

$(function () {
    $("#sendMsgBtn").click(function () {
        let msg = $(".my_input").val()
        if (msg !== "") {
            req_msg(msg)
        }
    });
    $(".my_input").bind('keydown', function (e) {
        let key = e.which;
        if (key === 13) {
            e.preventDefault();
            let msg = $(".my_input").val()
            if (msg !== "") {
                req_msg(msg)
            }
        }
    });

})
