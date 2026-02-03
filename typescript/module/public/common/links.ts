/**
 * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
 */
$(() => {

    const outerLinkJQuery: JQuery = $("#outer-link");

    $(".external-link").on('click', function() {
        let link = $(this).attr('data-link');
        
        if(link != undefined) {
            outerLinkJQuery.attr('href', link);
        }
    });
});