package org.xlrnet.metadict.web.auth.db.entities;

import org.xlrnet.metadict.api.auth.Role;
import org.xlrnet.metadict.web.auth.entities.UserRole;

import javax.persistence.*;
import java.util.Set;

/**
 * Persisted user in the database.
 */
@Entity
@Table(name = "user")
public class PersistedUser {

    @Id
    @Column(name = "user_id", unique = true, nullable = false, length = 36)
    private String id;

    @Column(name = "user_name", unique = true, nullable = false, length = 32)
    private String name;

    @Column(name = "user_password", unique = true, nullable = false, length = 64)
    private String password;

    @Column(name = "user_salt", unique = true, nullable = false, length = 64)
    private String salt;

    @Enumerated(EnumType.STRING)
    @ElementCollection(fetch = FetchType.EAGER, targetClass = UserRole.class)
    @CollectionTable(name = "user_role", joinColumns = @JoinColumn(name = "role_user_id"))
    @Column(name = "role_name", nullable = false)
    private Set<Role> roles;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getSalt() {
        return salt;
    }

    public void setSalt(String salt) {
        this.salt = salt;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }
}
