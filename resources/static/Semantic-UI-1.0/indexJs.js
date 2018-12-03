
$("#search").click(function () {
    var code = $("#code").val();
    var name = $("#name").val();
    var price = $("#price").val();
    $.session.remove("code");
    $.session.remove("name");
    $.session.remove("price");
    $.session.set("code", code);
    $.session.set("name", name);
    $.session.set("price", price);
    window.location.replace("/results");
});