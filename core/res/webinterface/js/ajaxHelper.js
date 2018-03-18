function ajaxButton(buttonId) {
	$("#" + buttonId).click(function() {
		$.get("/backend/ajaxButton?id=" + buttonId);
	});
}