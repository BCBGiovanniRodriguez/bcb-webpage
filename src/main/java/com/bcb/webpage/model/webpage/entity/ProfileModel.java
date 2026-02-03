package com.bcb.webpage.model.webpage.entity;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

import com.bcb.webpage.model.webpage.entity.interfaces.AbstractEntity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_profile")
public class ProfileModel extends AbstractEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long profileId;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "boolean default true")
    private int status;

    @Column(nullable = false, columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime created;

    @OneToMany(mappedBy = "profile", fetch = FetchType.EAGER)
    private Set<UserModel> users = new HashSet<>();

    @ManyToMany(cascade = CascadeType.PERSIST, fetch = FetchType.EAGER)
    @JoinTable(name = "profile_permissions")
    private Set<PermissionModel> permissions = new HashSet<>();

    public static final String CODE_SEARCH = "PERF01";

    public static final String CODE_ADD = "PERF02";
    
    public static final String CODE_UPDATE = "PERF03";

    public static final String CODE_ENDISABLE = "PERF04";

    /**
     * 
     * @return
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public Long getProfileId() {
        return this.profileId;
    }

    /**
     * 
     * @param profileId
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void setProfileId(Long profileId) {
        this.profileId = profileId;
    }

    /**
     * 
     * @return
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public String getName() {
        return this.name;
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
        return this.status;
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
        return this.created;
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
    public Set<UserModel> getUsers() {
        return this.users;
    }

    /**
     * 
     * @param users
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void setUsers(Set<UserModel> users) {
        this.users = users;
    }

    /**
     * 
     * @return
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public Set<PermissionModel> getPermissions() {
        return this.permissions;
    }

    /**
     * 
     * @param permissions
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void setPermissions(Set<PermissionModel> permissions) {
        this.permissions = permissions;
    }

    /**
     * 
     * @param permission
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void addPermission(PermissionModel permission) {
        this.permissions.add(permission);
    }

    /**
     * 
     * @param permission
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void removePermission(PermissionModel permission) {
        this.permissions.remove(permission);
    }

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
        result = prime * result + ((profileId == null) ? 0 : profileId.hashCode());
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
        ProfileModel other = (ProfileModel) obj;
        if (profileId == null) {
            if (other.profileId != null)
                return false;
        } else if (!profileId.equals(other.profileId))
            return false;
        return true;
    }

}
