var scrape = {

    setAmazonInfo: function () {
        var link = $.session.get("LINK");
        var price = $.session.get("PRICE");
        var image = $.session.get("IMAGE");
        document.getElementById("link").innerText = "Amazon Link - " + link;
        document.getElementById("price").innerText = "Amazon Price - " + price;
        var t = $.session.get("IMAGE");

    },
    scrapeHomeDeport: function () {

        var e = {};
        e["asin"] = $.session.get("asin");
        e["code"] = $.session.get("code");
        e["name"] = $.session.get("name");
        e["price"] = $.session.get("PRICE");
        e["image"] = $.session.get("IMAGE");


        var d = JSON.stringify(e);
        console.log("HO: " + d);

        $.ajax({
            url: 'rest/scraper/scrape/homedeport',
            dataType: 'json',
            contentType: "application/json",
            type: 'POST',
            data: d,
            success: function (data, textStatus, jqXHR) {
                $('#homedepot tbody tr td').remove();
                if (data.length <= 0) {
                    $('#homedepot').append('<tr>\n\
                               <td colspan=9><p align="center">No records found in this site\n\
                               </p></td>\n\
                               </tr>');
                } else {
                    for (var i = 0; i < data.length; i++) {
                        $('#homedepot').append('<tr>\n\
                                    <td style="font-size: x-small;font-weight: bold;">' + data[i].productlink + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].vendorprice + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].shippingcost + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].cogs + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].profit + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].margin + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].roi + '</td>\n\
                                    </tr>');

                    }
                    scrape.scrapeOverStock();
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {

            },
            beforeSend: function (xhr) {

            }
        });
    },
    scrapeOverStock: function () {
        var e = {};
        e["asin"] = $.session.get("asin");
        e["code"] = $.session.get("code");
        e["name"] = $.session.get("name");
        e["price"] = $.session.get("PRICE");
        e["image"] = $.session.get("IMAGE");

        var d = JSON.stringify(e);
        console.log("HO: " + d);

        $.ajax({
            url: 'rest/scraper/scrape/overstock',
            dataType: 'json',
            contentType: "application/json",
            type: 'POST',
            data: d,
            success: function (data, textStatus, jqXHR) {
                $('#overstock tbody tr td').remove();
                if (data.length <= 0) {
                    $('#overstock').append('<tr>\n\
                               <td colspan=9><p align="center">No records found in this site\n\
                               </p></td>\n\
                               </tr>');
                } else {
                    for (var i = 0; i < data.length; i++) {
                        $('#overstock').append('<tr>\n\
                                    <td style="font-size: x-small;font-weight: bold;">' + data[i].productlink + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].vendorprice + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].shippingcost + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].cogs + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].profit + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].margin + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].roi + '</td>\n\
                                    </tr>');

                    }
                    scrape.scrapeBedBath();
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {

            },
            beforeSend: function (xhr) {

            }
        });
    },
    scrapeBedBath: function () {
        var e = {};
        e["asin"] = $.session.get("asin");
        e["code"] = $.session.get("code");
        e["name"] = $.session.get("name");
        e["price"] = $.session.get("PRICE");
        e["image"] = $.session.get("IMAGE");

        var d = JSON.stringify(e);
        console.log("HO: " + d);

        $.ajax({
            url: 'rest/scraper/scrape/bedbath',
            dataType: 'json',
            contentType: "application/json",
            type: 'POST',
            data: d,
            success: function (data, textStatus, jqXHR) {
                $('#bedbat tbody tr td').remove();
                if (data.length <= 0) {
                    $('#bedbat').append('<tr>\n\
                               <td colspan=9><p align="center">No records found in this site\n\
                               </p></td>\n\
                               </tr>');
                } else {
                    for (var i = 0; i < data.length; i++) {
                        $('#bedbat').append('<tr>\n\
                                    <td style="font-size: x-small;font-weight: bold;">' + data[i].productlink + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].vendorprice + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].shippingcost + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].cogs + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].profit + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].margin + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].roi + '</td>\n\
                                    </tr>');

                    }
                }
                scrape.scrapeWalMart();
            },
            error: function (jqXHR, textStatus, errorThrown) {

            },
            beforeSend: function (xhr) {

            }
        });
    },
    scrapeWalMart: function () {
        var e = {};
        e["asin"] = $.session.get("asin");
        e["code"] = $.session.get("code");
        e["name"] = $.session.get("name");
        e["price"] = $.session.get("PRICE");
        e["image"] = $.session.get("IMAGE");
        
        var d = JSON.stringify(e);
        console.log("HO: " + d);

        $.ajax({
            url: 'rest/scraper/scrape/walmart',
            dataType: 'json',
            contentType: "application/json",
            type: 'POST',
            data: d,
            success: function (data, textStatus, jqXHR) {
                $('#walmart tbody tr td').remove();
                if (data.length <= 0) {
                    $('#walmart').append('<tr>\n\
                               <td colspan=9><p align="center">No records found in this site\n\
                               </p></td>\n\
                               </tr>');
                } else {
                    for (var i = 0; i < data.length; i++) {
                        $('#walmart').append('<tr>\n\
                                    <td style="font-size: x-small;font-weight: bold;">' + data[i].productlink + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].vendorprice + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].shippingcost + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].cogs + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].profit + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].margin + '</td>\n\
                                    <td style="font-size: x-small;font-weight: bold;text-align: center">' + data[i].roi + '</td>\n\
                                    </tr>');

                    }
                }
            },
            error: function (jqXHR, textStatus, errorThrown) {

            },
            beforeSend: function (xhr) {

            }
        });
    }
}