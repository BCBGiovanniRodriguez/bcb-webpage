package com.bcb.webpage.model.webpage.entity.interfaces;

/**
 * @author Giovanni Rodríguez <grodriguez@bcbcasadebolsa.com>
 */
public interface EmailInterface {

    public static final int TYPE_SYSTEM_NOTIFICATION = 1;

    public static final int TYPE_LEGAL_NOTIFICATION = 2;

    public static final int TYPE_CUSTOMER_NOTIFICATION = 3;

    public static final int TARGET_SYSTEM_NOTIFICATION = 1;

    public static final int TARGET_LEGAL_NOTIFICATION = 2;

    public static final int TARGET_CUSTOMER_NOTIFICATION = 3;

    public static final int MODE_SHIPMENT = 1;

    public static final int MODE_RECEPTION = 2;

    public static final String ROUTE_TEMPLATE_OTT = "email/template/ott.html";

    public static final String ROUTE_TEMPLATE_RECOVER_PASSWORD = "email/template/recover-password.html";

    public boolean isShipmentMode();

    public boolean isReceptionMode();

}
