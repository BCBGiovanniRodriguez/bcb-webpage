/**
 * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
 */
$(() => {
    const spanYearlyJsonJQuery = $("#spanYearlyJson"),
        spanQuarterlyJsonJQuery = $("#spanQuarterlyJson");

    console.log(spanYearlyJsonJQuery.text());
    console.log(spanQuarterlyJsonJQuery.text());
    let yearlyJson = JSON.parse(spanYearlyJsonJQuery.text());
    let quarterlyJson = JSON.parse(spanQuarterlyJsonJQuery.text());

    //let yearlyJson = JSON.parse([[${yearlyJsonData}]]);
    //let quarterlyJson = JSON.parse([[${quarterlyJsonData}]]);

    $("#quarterlyYear").on('change', function() {
        $("#quarterlyFinantialStatements").empty();
        let year = Number($(this).val());

        let elementContainer = $('<div>', {'class': 'container-fluid'});
            
        $.each(quarterlyJson, function(i, item) {

            if(year == Number(item.year)) {
                let elementRow = $('<div>', {'class': 'row'});
                let elementImageContainer = $('<div>', {'class': 'col-md-2'});
                let elementImage = $('<img>', {'src': 'public/images/iconos-genericos/icono-gen-02a.svg'});

                let elementInfoContainer = $('<div>', {'class': 'col-md-8'});
                let elementHeader = $('<h4>',  {'text': item.periodAsString + ' ' + item.year });
                let elementInfoLabel = $('<label>',  {'text': 'Indicadores Financieros' });
                let elementInfoAnchor = $('<a>', {'class': 'btn btn-white btn-border', 'href': '/estados-financieros/descargar?t='+item.type+'&p='+item.period+'&y='+item.year, 'text': 'Descargar'});

                elementImageContainer.append(elementImage);
                elementInfoContainer.append(elementHeader).append(elementInfoLabel).append(elementInfoAnchor);

                elementRow.append(elementImageContainer).append(elementInfoContainer);
                elementContainer.append(elementRow);
            }
        });
        
        $("#quarterlyFinantialStatements").append(elementContainer);
    });


    $("#yearlyYear").on('change', function() {
        $("#yearlyFinantialStatements").empty();
        let year = Number($(this).val());
        
        let elementContainer = $('<div>', {'class': 'container-fluid'});

        $.each(yearlyJson, function(i, item) {
            console.log(item);
            if(year == Number(item.year)) {
                let elementRow = $('<div>', {'class': 'row'});
                let elementImageContainer = $('<div>', {'class': 'col-md-2'});
                let elementImage = $('<img>', {'src': 'public/images/iconos-genericos/icono-gen-02a.svg'});

                let elementInfoContainer = $('<div>', {'class': 'col-md-8'});
                let elementHeader = $('<h4>',  {'text': 'DICTAMEN FINANCIERO ' + item.year });
                let elementInfoAnchor = $('<a>', {'class': 'btn btn-white btn-border', 'href': '/estados-financieros/descargar?t='+item.type+'&y='+item.year, 'text': 'Descargar'});

                elementImageContainer.append(elementImage);
                elementInfoContainer.append(elementHeader).append(elementInfoAnchor);
                elementRow.append(elementImageContainer).append(elementInfoContainer);
                elementContainer.append(elementRow);
            }
        });

        $("#yearlyFinantialStatements").append(elementContainer);
    });

});
