function doPost(url, data, callback) {
    let headers = {}
    let token = localStorage.getItem("token")
    if (token) {
        headers.token = token
    }
    $.ajax({
        url: url,
        method: "POST",
        dataType: "json",
        headers: headers,
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
    let headers = {}
    let token = localStorage.getItem("token")
    if (token) {
        headers.token = token
    }
    $.ajax({
        type: "GET",
        url: url,
        dataType: 'json',
        headers: headers,
        contentType: 'application/json',
        success: function (data) {
            callback(data)
        }
    });
}
