package com.organization.taskManagement.security;

import com.organization.taskManagement.Model.EmployeeRegisterModel;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class UserInfoDetails implements UserDetails {

    private EmployeeRegisterModel employee;

    public UserInfoDetails(EmployeeRegisterModel employee) {
        this.employee = employee;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if(employee != null && employee.getRole() != null) {
            return Collections.singleton(new SimpleGrantedAuthority(employee.getEmployeeId()));

        }
        else return Collections.emptyList();
    }

    @Override
    public @Nullable String getPassword() {
        return employee.getPassword();
    }

    @Override
    public String getUsername() {
        return employee.getEmployeeId();
    }

    @Override
    public boolean isAccountNonExpired() {
        return UserDetails.super.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return UserDetails.super.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return UserDetails.super.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return UserDetails.super.isEnabled();
    }
}
