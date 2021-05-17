$(function(){
	$("#publishBtn").click(publish);
});

function publish() {
	// 隐藏发布框
	$("#publishModal").modal("hide");

	// 1.获取文章标题和内容
	var title = $("#recipient-name").val();
	var content = $("#message-text").val();

	// 2.发送 ajax 请求
	$.ajax({
		type: 'POST',
		url: CONTEXT_PATH + "/discuss/add",
		data: {title:title, content:content},
		success: function (data) {
			var json = $.parseJSON(data);
			if(json.code === 0){
				$("#hintBody").text(json.msg);
			}else{
				$("#hintBody").text(json.code + ": " + json.msg);
			}

			// 显示提示框
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				// 重新加载当前页面
				window.location.reload();
			}, 2000);
		}
	});
}