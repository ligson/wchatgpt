function doPost(url, data, callback) {
    $.ajax({
        url: url,
        method: "POST",
        dataType: "json",
        headers: getHeaders(),
        contentType: "application/json",
        data: JSON.stringify(data),
        success: function (data) {
            callback(data)
        },
        error: function (jqXHR, textStatus, errorThrown) {
            /*错误信息处理*/
            console.log(textStatus + ":" + errorThrown);
            alert("网络错误，请稍候再试....")
        }
    });
}

function doGet(url, callback) {
    $.ajax({
        type: "GET",
        url: url,
        dataType: 'json',
        headers: getHeaders(),
        contentType: 'application/json',
        success: function (data) {
            callback(data)
        }
    });
}

function getHeaders(){
    let headers = {}
    let token = localStorage.getItem("token")
    if (token) {
        headers.token = token
    }
    headers.winWidth = document.body.clientWidth
    headers.winHeight = document.body.clientHeight
    return headers;
}