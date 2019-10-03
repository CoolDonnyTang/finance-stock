function dataSync() {
    var code = $("#code").val();
    var startDate = $("#startDate").val();
    var endDate = $("#endDate").val();
    if(!checkStockCode(code)) {
        alert("股票代码格式错误");
        return;
    }
    if(startDate.trim() === "") {
        alert("请输入开始日期");
        return;
    }
    if(endDate.trim() === "") {
        alert("请输入结束日期");
        return;
    }

    $.ajax({
        data:{"startDate":new Date(startDate), "endDate":new Date(endDate)},
        dataType:"json",
        type:"post",
        url:appName + "stock/" + code ,
        error:function() {
            alert("连接服务器失败");
        },
        success:function(result) {
            if(result.status == 200) {
                alert(result.message);
            } else {
                alert(result.message);
            }
        }
    });

}