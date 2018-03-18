var playerId = $("#playerId").html();
var message = $("#messageText").val();
$.get("backend/messageplayer/" + playerId + "/" + message);
$("#messageText").val("");