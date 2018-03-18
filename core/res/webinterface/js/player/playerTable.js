var playerTable;

$(function() {
    playerTable = $('#playerTable').DataTable({
        "processing": true,
        "serverSide": true,
        "ajax": "backend/playerDataTable/",
        "columnDefs": [
            { "orderable": false, "targets": 2 }
          ]
    });

    $('#playerTable tbody').on('click', 'tr', onPlayerClick);

    setInterval(function () {
        playerTable.ajax.reload(null, false);
    }, 30000);
});

function onPlayerClick() {
    var playerId = playerTable.row(this).data()[2];
	
	window.location = "player?id=" + playerId;
}