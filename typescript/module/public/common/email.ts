/**
 * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
 */
$(() => {
    const NO_OPTION = 0, PRODUCT_INFO = 1, SPONSOR_CONTACT = 2, OTHER = 3,
        successNotificationJQuery: JQuery = $("#successNotification"),
        btnShowModalJQuery: JQuery = $(".btn-send-email"),
        btnSendEmailJQuery: JQuery = $("#btnSendEmail"),
        senderinterestJQuery: JQuery = $("#senderinterest"),
        sendertypeJQuery: JQuery = $("#sendertype"),
        sendernameJQuery: JQuery = $("#sendername"),
        emailaddressJQuery: JQuery = $("#emailaddress"),
        telephoneJQuery: JQuery = $("#telephone"),
        privacyNoticeJQuery: JQuery = $("#privacy-notice"),
        sendermessageJQuery: JQuery = $("#sendermessage"),
        errorDetailsJQuery: JQuery = $("#errorDetails"),
        modalJQuery: JQuery = $("#MainMenu-BM-contacto");

    let errors: string[] = [];
        
    type Person = {
        type: string | undefined,
        interested: string | undefined,
        name: string | undefined,
        email: string | undefined,
        phone: string | undefined,
        message: string | undefined
    };

    btnShowModalJQuery.on('click', function() {
        let self = $(this),
            type = self.attr('data-origin');

        if(type != undefined) {
            sendertypeJQuery.val(type);
        }
    });

    privacyNoticeJQuery.on('change', function() {
        errors = [];

        if($(this).is(':checked') && validateFields() && validateElements()) {
            btnSendEmailJQuery.removeClass('hidden');
            errorDetailsJQuery.addClass('hidden');
            errorDetailsJQuery.html('');

        } else {
            btnSendEmailJQuery.addClass('hidden');
            errorDetailsJQuery.removeClass('hidden');
            errorDetailsJQuery.html(errors.join('<br />'));
        }
    });

    function isValidEmail(email: any) {
        if (typeof email !== 'string') return false;

        email = email.trim();
        const emailPattern = /^[^\s@]+@[^\s@]+\.[^\s@]{2,}$/;

        return emailPattern.test(email);
    }

    $(".gui-input, .gui-textarea").on('change', function() {
        errors = [];

        if(!validateFields()) {
            errorDetailsJQuery.removeClass('hidden');
            errorDetailsJQuery.html(errors.join('<br />'));
        } else {
            errorDetailsJQuery.html('');
            errorDetailsJQuery.addClass('hidden');
        }
    });

    senderinterestJQuery.on('change', function(){
        
    });

    function validateElements() {
        let valid: boolean = true,
        senderinterestNumber = Number(senderinterestJQuery.val());

        if(senderinterestNumber == undefined || senderinterestNumber == 0) {
            errors.push("Se debe seleccionar una opción de interés");
            return false;
        }

        return valid;
    }

    function validateFields() {
        let valid: boolean = true,
            sendernameString = String(sendernameJQuery.val()),
            emailaddressString = String(emailaddressJQuery.val()),
            telephoneString = String(telephoneJQuery.val()),
            sendermessageString = String(sendermessageJQuery.val());

        if(sendernameString == undefined || sendernameString.length == 0) {
            errors.push("Nombre de cliente no cumple con validación");
            return false;
        }

        if(!isValidEmail(emailaddressString)) {
            errors.push("Correo electrónico no cumple con validación");
            return false;
        }

        if(telephoneString == undefined || telephoneString.length == 0) {
            errors.push("Número telefónico no cumple con validación");
            return false;
        }

        if(sendermessageString == undefined || sendermessageString.length == 0) {
            errors.push("Es necesario proporcionar un detalle");
            return false;
        }

        return valid;
    }

    btnSendEmailJQuery.on('click', function() {
        let endpointUrl: string = `/api/send-email`,
            person = {} as Person;

        btnSendEmailJQuery.addClass('hidden');

        person.name = sendernameJQuery.val()?.toString();
        person.type = sendertypeJQuery.val()?.toString();
        person.email = emailaddressJQuery.val()?.toString();
        person.phone = telephoneJQuery.val()?.toString();
        person.message = sendermessageJQuery.val()?.toString();

        $.ajax({
            method: 'POST',
            contentType: 'application/json',
            url: endpointUrl,
            data: JSON.stringify(person)
        }).fail((jqXHR, textStatus, error) => {
            console.dir(jqXHR);
            console.dir(textStatus);
            console.dir(error);
            if(textStatus == "timeout") {
                $("#errorMessage").html("Timeout: Sin conexión al endpoint <strong>BCB</strong>");
            } else {
                $("#errorMessage").text("Error desconocido");
            }
        }).then((result, textStatus, jqXHR) => {
            if(result != undefined) {
                let resultJson = JSON.parse(result);

                if(resultJson.status == 1) {

                    sendernameJQuery.val("");
                    sendertypeJQuery.val("");
                    emailaddressJQuery.val("");
                    telephoneJQuery.val("");
                    sendermessageJQuery.val("");
                    privacyNoticeJQuery.prop('checked', false);
                    senderinterestJQuery.val(0);
                    
                    successNotificationJQuery.removeClass('hidden');
                    
                    setTimeout(() => {
                        successNotificationJQuery.addClass('hidden');
                        // @ts-ignore
                        modalJQuery.modal('hide');
                    }, 5000);
                } else if(resultJson.status == 0) {
                    console.log(result.message);
                }
            }
        });
    });


    $('#MainMenu-BM-contacto').on('hidden.bs.modal', function (e) {
        successNotificationJQuery.attr("hidden", "hidden");
    });

});