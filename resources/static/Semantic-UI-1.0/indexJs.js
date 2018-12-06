$("#search").click(function () {
    var code = $("#code").val();
    var name = $("#name").val();
    var price = $("#price").val();
    var asin = $("#asin").val();

    $.session.remove("code");
    $.session.remove("name");
    $.session.remove("price");
    $.session.remove("asin");

    $.session.set("code", code);
    $.session.set("name", name);
    $.session.set("price", price);
    $.session.set("asin", asin);

    $.session.set("LINK", "https://www.amazon.com/dp/" + asin);
    $.session.set("PRICE", price);

    setTimeout(function () {
        window.location.replace("/results");
    }, 1000);

});
