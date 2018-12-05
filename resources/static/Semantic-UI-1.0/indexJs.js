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

    scrape.amazonPrice();
});

var scrape = {

    amazonPrice: function () {
        $('#message').removeAttr('hidden');

        var e = {};
        e["code"] = $.session.get("code");
        e["name"] = $.session.get("name");
        e["price"] = $.session.get("price");

        var d = JSON.stringify(e);
        console.log("HO: " + d);

        $.ajax({
            url: 'rest/scraper/scrape/amazon/price',
            dataType: 'text',
            contentType: "application/json",
            type: 'POST',
            data: d,
            success: function (data, textStatus, jqXHR) {
                var price = data.split("  ")[1];
                var link = data.split("  ")[0];
                var image = data.split("  ")[2];

                $.session.set("LINK", link);
                $.session.set("PRICE", price);
                $.session.set("IMAGE", image);

                $("#price").val(price);

                setTimeout(function () {

                    window.location.replace("/results");

                }, 5000);

            },
            error: function (jqXHR, textStatus, errorThrown) {

            },
            beforeSend: function (xhr) {

            }
        });
    },
}