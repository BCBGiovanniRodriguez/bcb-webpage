package com.bcb.webpage.model.webpage.entity.interfaces;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Giovanni Rodríguez <grodriguez@bcbcasadebolsa.com>
 */
public abstract class AbstractEntity {

    public static final int STATUS_UNDEFINED = 0;

    public static final int STATUS_ENABLE = 1;

    public static final int STATUS_DISABLE = 2;
    
    public static final String STATUS_STRING_UNDEFINED = "no definido";

    public static final String STATUS_STRING_ENABLE = "habilitado";

    public static final String STATUS_STRING_DISABLE = "deshabilitado";

    public final Map<Integer, String> statuses = new HashMap<>() {{
        put(STATUS_UNDEFINED, STATUS_STRING_UNDEFINED);
        put(STATUS_ENABLE, STATUS_STRING_ENABLE);
        put(STATUS_DISABLE, STATUS_STRING_DISABLE);
    }};


    public abstract String getStatusAsString();

    public abstract boolean isStatusEnabled();

}
