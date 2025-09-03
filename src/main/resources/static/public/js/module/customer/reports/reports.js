"use strict";
$(() => {
    const generalButtonJQuery = $("#generalButton"), TYPE_GENERAL = 1, TYPE_CASH = 2, TYPE_STOCK_MARKET = 3, TYPE_MONEY_MARKET = 4, TYPE_INVESTMENT_FUNDS = 5, moneyMarketButtonJQuery = $("#moneyMarketButton"), stockMarketButtonJQuery = $("#stockMarketButton"), cashButtonJQuery = $("#cashButton"), localApiCustomer = "/portal-clientes/api/customer", currentCustomerContractJQuery = $("#currentCustomerContract");
    generalButtonJQuery.on('click', function () {
        let self = $(this), currentCustomerContract = Number(currentCustomerContractJQuery.text()), generalJson = sessionStorage.getItem('generalJson'), endpointReport = `${localApiCustomer}/report?type=${TYPE_GENERAL}`;
        if (generalJson != undefined) {
            $.ajax({
                method: 'POST',
                contentType: 'application/json',
                cache: false,
                url: endpointReport,
                data: generalJson
            }).fail((jqXHR, textStatus, error) => {
                console.log(jqXHR);
                console.log(textStatus);
                console.log(error);
            })
                .then((result, textStatus, jqXHR) => {
                if (result != undefined) {
                    const resultJson = JSON.parse(result);
                    if (result.status == 1) {
                        console.log("Registrado!");
                    }
                    else if (result.status == 0) {
                        console.log(result.message);
                    }
                }
            });
        }
    });
    moneyMarketButtonJQuery.on('click', function () {
        let self = $(this), currentCustomerContract = Number(currentCustomerContractJQuery.text()), moneyMarketJson = sessionStorage.getItem('moneyMarketJson'), endpointReport = `${localApiCustomer}/report`;
        if (moneyMarketJson != undefined) {
            $.ajax({
                method: 'POST',
                contentType: 'application/json',
                url: endpointReport,
                data: moneyMarketJson
            }).fail((jqXHR, textStatus, error) => {
                console.log(jqXHR);
                console.log(textStatus);
                console.log(error);
            }).then((result, textStatus, jqXHR) => {
                if (result != undefined) {
                    const resultJson = JSON.parse(result);
                    if (resultJson.status == 1) {
                        console.log("Registrado!");
                    }
                    else if (resultJson.status == 0) {
                        console.log(result.message);
                    }
                }
            });
        }
    });
    stockMarketButtonJQuery.on('click', function () {
        let self = $(this), currentCustomerContract = Number(currentCustomerContractJQuery.text()), stockMarketJson = sessionStorage.getItem('stockMarketJson'), endpointReport = `${localApiCustomer}/report?type=${TYPE_STOCK_MARKET}`;
        if (stockMarketJson != undefined) {
            $.ajax({
                method: 'POST',
                contentType: 'application/json',
                cache: false,
                url: endpointReport,
                data: stockMarketJson
            }).then((result, textStatus, jqXHR) => {
                if (result != undefined) {
                    const resultJson = JSON.parse(result);
                    if (resultJson.status == 1) {
                        console.log("Registrado!");
                    }
                    else if (resultJson.status == 0) {
                        console.log(result.message);
                    }
                }
            });
        }
    });
    cashButtonJQuery.on('click', function () {
        let self = $(this), currentCustomerContract = Number(currentCustomerContractJQuery.text()), cashJson = sessionStorage.getItem('cashJson'), endpointReport = `${localApiCustomer}/report?type=${TYPE_CASH}`;
        if (cashJson != undefined) {
            $.ajax({
                method: 'POST',
                contentType: 'application/json',
                cache: false,
                url: endpointReport,
                data: cashJson
            }).then((result, textStatus, jqXHR) => {
                if (result != undefined) {
                    const resultJson = JSON.parse(result);
                    if (resultJson.status == 1) {
                        console.log("Registrado!");
                    }
                    else if (resultJson.status == 0) {
                        console.log(result.message);
                    }
                }
            });
        }
    });
});
