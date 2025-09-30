package com.bcb.webpage.model.webpage.entity;

import java.time.LocalDateTime;

import com.bcb.webpage.model.webpage.entity.interfaces.EmailInterface;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "configuration_email_accounts")
public class ConfigurationEmailAccountEntity implements EmailInterface {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = true)
    private Long emailId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String access;

    @Column(nullable = false)
    private int mode;

    @Column(nullable = false)
    private int type;

    @Column(nullable = false)
    private int target;

    @Column(nullable = false)
    private String serverAddress;
    
    @Column(nullable = false)
    private String serverPort;

    @Column( columnDefinition = "boolean default true")
    private int status;

    @Column(columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime created;

    public ConfigurationEmailAccountEntity() {
    }

    public Long getEmailId() {
        return emailId;
    }

    public void setEmailId(Long emailId) {
        this.emailId = emailId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccess() {
        return access;
    }

    public void setAccess(String access) {
        this.access = access;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getTarget() {
        return target;
    }

    public void setTarget(int target) {
        this.target = target;
    }

    public String getServerAddress() {
        return serverAddress;
    }

    public void setServerAddress(String serverAddress) {
        this.serverAddress = serverAddress;
    }

    public String getServerPort() {
        return serverPort;
    }

    public void setServerPort(String serverPort) {
        this.serverPort = serverPort;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    @Override
    public boolean isShipmentMode() {
        return EmailInterface.MODE_SHIPMENT == this.mode;
    }

    @Override
    public boolean isReceptionMode() {
        return EmailInterface.MODE_RECEPTION == this.mode;
    }

}
