package com.bcb.webpage.model.webpage.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.bcb.webpage.model.webpage.entity.interfaces.AbstractEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "system_user")
public class UserModel extends AbstractEntity implements UserDetails, CredentialsContainer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(unique = true, nullable = false)
    private Long UserId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "profile_id", nullable = false)
    private ProfileModel profile;

    @Column(nullable = false)
    private String nickname;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String access;

    @Column( columnDefinition = "boolean default true")
    private int status;

    @Column(columnDefinition = "timestamp default current_timestamp")
    private LocalDateTime created;

    private Collection<? extends GrantedAuthority> authorities;
    
    @OneToMany(mappedBy = "userModel", fetch = FetchType.EAGER)
    List<FinantialStatementEntity> finantialStatementList = new ArrayList<>();

    @OneToMany(mappedBy = "userModel", fetch = FetchType.EAGER)
    List<InvestmentServicesEntity> investmentServicesList = new ArrayList<>();

    @OneToMany(mappedBy = "userModel", fetch = FetchType.EAGER)
    List<RiskManagementEntity> riskManagementList = new ArrayList<>();

    public static final String CODE_SEARCH = "USER01";

    public static final String CODE_ADD = "USER02";
    
    public static final String CODE_UPDATE = "USER03";

    public static final String CODE_ENDISABLE = "USER04";

    public UserModel() { }

    public UserModel(String nickname, String access, Collection<? extends GrantedAuthority> authorities) {
        this.nickname = nickname;
        this.access = access;
        this.authorities = authorities;
    }

    /**
     * 
     * @return
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public Long getUserId() {
        return UserId;
    }

    /**
     * 
     * @param userId
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void setUserId(Long userId) {
        UserId = userId;
    }

    
    /**
     * 
     */
    public ProfileModel getProfile() {
        return profile;
    }

    /**
     * 
     * @param profile
     */
    public void setProfile(ProfileModel profile) {
        this.profile = profile;
    }

    /**
     * 
     * @return
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public String getNickname() {
        return nickname;
    }

    /**
     * 
     * @param nickname
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    /**
     * 
     * @return
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public String getAccess() {
        return access;
    }

    /**
     * 
     * @param access
     * @author Giovanni Rodriguez <grodriguez@bcbcasadebolsa.com>
     */
    public void setAccess(String access) {
        this.access = access;
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

    @Override
    public String getStatusAsString() {
        return this.statuses.get(this.getStatus());
    }

    @Override
    public boolean isStatusEnabled() {
        return AbstractEntity.STATUS_ENABLE == this.status;
    }

    @Override
    public String toString() {
        return "UserModel [UserId=" + UserId + ", nickname=" + nickname + "]";
    }

    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Collection<? extends GrantedAuthority> authorities) {
        this.authorities = authorities;
    }

    @Override
    public String getPassword() {
        return this.access;
    }

    @Override
    public String getUsername() {
        return this.nickname;
    }

    @Override
    public void eraseCredentials() {
        this.access = null;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
