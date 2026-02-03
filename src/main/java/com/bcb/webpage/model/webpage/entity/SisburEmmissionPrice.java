package com.bcb.webpage.model.webpage.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "sisbur_emmision_prices")
public class SisburEmmissionPrice {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long emmisionPriceId;

    private Double dirtyPrice;

    private Double cleanPrice;

    private Double dirtyPrice24;

    private Double cleanPrice24;

    private LocalDateTime created;

    @ManyToOne
    @JoinColumn(name = "emmissionId", nullable = false)
    private SisburEmmission emmissionId;

    public SisburEmmissionPrice() {
    }

    public Long getEmmisionPriceId() {
        return emmisionPriceId;
    }

    public void setEmmisionPriceId(Long emmisionPriceId) {
        this.emmisionPriceId = emmisionPriceId;
    }

    public Double getDirtyPrice() {
        return dirtyPrice;
    }

    public void setDirtyPrice(Double dirtyPrice) {
        this.dirtyPrice = dirtyPrice;
    }

    public Double getCleanPrice() {
        return cleanPrice;
    }

    public void setCleanPrice(Double cleanPrice) {
        this.cleanPrice = cleanPrice;
    }

    public Double getDirtyPrice24() {
        return dirtyPrice24;
    }

    public void setDirtyPrice24(Double dirtyPrice24) {
        this.dirtyPrice24 = dirtyPrice24;
    }

    public Double getCleanPrice24() {
        return cleanPrice24;
    }

    public void setCleanPrice24(Double cleanPrice24) {
        this.cleanPrice24 = cleanPrice24;
    }

    public LocalDateTime getCreated() {
        return created;
    }

    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    public SisburEmmission getEmmissionId() {
        return emmissionId;
    }

    public void setEmmissionId(SisburEmmission emmissionId) {
        this.emmissionId = emmissionId;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((dirtyPrice == null) ? 0 : dirtyPrice.hashCode());
        result = prime * result + ((cleanPrice == null) ? 0 : cleanPrice.hashCode());
        result = prime * result + ((dirtyPrice24 == null) ? 0 : dirtyPrice24.hashCode());
        result = prime * result + ((cleanPrice24 == null) ? 0 : cleanPrice24.hashCode());
        result = prime * result + ((emmissionId == null) ? 0 : emmissionId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SisburEmmissionPrice other = (SisburEmmissionPrice) obj;
        if (dirtyPrice == null) {
            if (other.dirtyPrice != null)
                return false;
        } else if (!dirtyPrice.equals(other.dirtyPrice))
            return false;
        if (cleanPrice == null) {
            if (other.cleanPrice != null)
                return false;
        } else if (!cleanPrice.equals(other.cleanPrice))
            return false;
        if (dirtyPrice24 == null) {
            if (other.dirtyPrice24 != null)
                return false;
        } else if (!dirtyPrice24.equals(other.dirtyPrice24))
            return false;
        if (cleanPrice24 == null) {
            if (other.cleanPrice24 != null)
                return false;
        } else if (!cleanPrice24.equals(other.cleanPrice24))
            return false;
        if (emmissionId == null) {
            if (other.emmissionId != null)
                return false;
        } else if (!emmissionId.equals(other.emmissionId))
            return false;
        return true;
    }

    

}
