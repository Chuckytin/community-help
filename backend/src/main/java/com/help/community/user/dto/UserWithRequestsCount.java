package com.help.community.user.dto;

import com.help.community.user.model.User;
import java.util.Set;

public interface UserWithRequestsCount {
    Long getUserId();
    String getEmail();
    String getName();
    String getPhoneNumber();
    Set<User.Role> getRoles();
    Long getCreatedRequestsCount();
}