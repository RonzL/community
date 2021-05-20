$(function(){
	$("#sendBtn").click(send_letter);
	$(".close").click(delete_msg);
});

function send_letter() {
	var to_id = $("#to_id").val();
	var content = $("#message-text").val();

	$.ajax({
		type: 'POST',
		url: CONTEXT_PATH + "/message/add/" + to_id,
		data: {"to_id":to_id, "content": content},
		success: function (data) {
			$("#sendModal").modal("hide");
			var json = $.parseJSON(data);
			$("#hintBody").text(json.msg);
			$("#hintModal").modal("show");
			setTimeout(function(){
				$("#hintModal").modal("hide");
				window.location.reload();
			}, 2000);

		}
	});

}

function delete_msg() {
	// TODO 删除数据
	$(this).parents(".media").remove();
}