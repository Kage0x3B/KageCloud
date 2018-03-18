var playerId = $("#playerId").html();
$.get("backend/kickplayer/" + playerId);
window.location = "/players";