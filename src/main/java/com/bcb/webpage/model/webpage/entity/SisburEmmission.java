package com.bcb.webpage.model.webpage.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "sisbur_emmissions")
public class SisburEmmission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emmissionId;

    private String valueType;

    private String emmiter;

    private String serie;

    private LocalDateTime created;

    @OneToMany(mappedBy = "emmissionId", fetch = FetchType.EAGER)
    private List<SisburEmmissionPrice> emmisionPrices;

    public SisburEmmission() {
        this.emmisionPrices = new ArrayList<>();
    }

    public Long getEmmissionId() {
        return emmissionId;
    }

    public void setEmmissionId(Long emmisionId) {
        this.emmissionId = emmisionId;
    }

    public String getValueType() {
        return valueType;
    }

    public void setValueType(String valueType) {
        this.valueType = valueType;
    }

    public String getEmmiter() {
        return emmiter;
    }

    public void setEmmiter(String emmiter) {
        this.emmiter = emmiter;
    }

    public String getSerie() {
        return serie;
    }

    public void setSerie(String serie) {
        this.serie = serie;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public List<SisburEmmissionPrice> getEmmisionPrices() {
        return emmisionPrices;
    }

    public void setEmmisionPrices(List<SisburEmmissionPrice> emmisionPrices) {
        this.emmisionPrices = emmisionPrices;
    }

}
