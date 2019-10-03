function syncAllData() {
    $.ajax({
        data:{},
        dataType:"json",
        type:"post",
        url:appName + "stock/",
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