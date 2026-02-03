package com.bcb.webpage.model.webpage.entity;

import java.time.LocalDateTime;

public class SisburValueTypes {

    private Long valueTypeId;

    private String value;

    private LocalDateTime created;

    public SisburValueTypes() {
    }

    public Long getValueTypeId() {
        return valueTypeId;
    }

    public void setValueTypeId(Long valueTypeId) {
        this.valueTypeId = valueTypeId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }
}
