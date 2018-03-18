$(function() {
    getContentAjax($("#playerAmount"), "playerAmount");
    getContentAjax($("#serverAmount"), "serverAmount");
    getContentAjax($("#proxyAmount"), "proxyAmount");
    getContentAjax($("#wrapperAmount"), "wrapperAmount");
});

function getContentAjax(element, request) {
    $.get( "backend/" + request, function(data) {
      element.html(data);
    });
}
