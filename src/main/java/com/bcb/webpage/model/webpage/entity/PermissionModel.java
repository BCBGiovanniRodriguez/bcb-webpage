package com.bcb.webpage.model.webpage.entity;

import java.time.LocalDateTime;

import com.bcb.webpage.model.webpage.entity.interfaces.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_permission")
public class PermissionModel extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long PermissionId;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "boolean default true")
    private int status;

    @Column(columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime created;

    public static final String CODE_SEARCH = "PERM01";

    public static final String CODE_ADD = "PERM02";
    
    public static final String CODE_UPDATE = "PERM03";

    public static final String CODE_ENDISABLE = "PERM04";

    public PermissionModel() { }

    public PermissionModel(Long permissionId) {
        this.PermissionId = permissionId;
    }

    /**
     * 
     * @return
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public Long getPermissionId() {
        return PermissionId;
    }

    /**
     * 
     * @param permissionId
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void setPermissionId(Long permissionId) {
        PermissionId = permissionId;
    }

    /**
     * 
     * @return
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public String getCode() {
        return code;
    }

    /**
     * 
     * @param code
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * 
     * @return
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public String getName() {
        return name;
    }

    /**
     * 
     * @param name
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * 
     * @return
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public int getStatus() {
        return status;
    }

    /**
     * 
     * @param status
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void setStatus(int status) {
        this.status = status;
    }

    /**
     * 
     * @return
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public LocalDateTime getCreated() {
        return created;
    }

    /**
     * 
     * @param created
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void setCreated(LocalDateTime created) {
        this.created = created;
    }

    /**
     * 
     * @return
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    @Override
    public String getStatusAsString() {
        return this.statuses.get(this.getStatus());
    }

    @Override
    public boolean isStatusEnabled() {
        return AbstractEntity.STATUS_ENABLE == this.status;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((PermissionId == null) ? 0 : PermissionId.hashCode());
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
        PermissionModel other = (PermissionModel) obj;
        if (PermissionId == null) {
            if (other.PermissionId != null)
                return false;
        } else if (!PermissionId.equals(other.PermissionId))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PermissionModel [PermissionId=" + PermissionId + ", name=" + name + "]";
    }
}
