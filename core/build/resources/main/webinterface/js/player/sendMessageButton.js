var playerId = $("#playerId").html();
var message = $("#messageText").val();
$.get("backend/sendplayermessage/" + playerId + "/" + message);
$("#messageText").val("");