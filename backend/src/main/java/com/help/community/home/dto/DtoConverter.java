package com.help.community.home.dto;

import com.help.community.core.security.oauth2.model.CustomOAuth2User;
import com.help.community.request.dto.RequestNearbyDTO;
import com.help.community.request.model.Request;
import com.help.community.user.dto.UserDTO;
import com.help.community.user.dto.UserProfileDTO;
import com.help.community.user.model.User;
import org.springframework.stereotype.Service;

import java.util.stream.Collectors;

@Service
public class DtoConverter {

    public UserDTO convertToUserDto(User user) {
        return UserDTO.builder()
                .id(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .provider(user.getProvider())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .build();
    }

    public UserDTO convertToUserDto(CustomOAuth2User oauthUser) {
        User user = oauthUser.getUser();
        return UserDTO.builder()
                .id(user.getUserId())
                .name(user.getName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .provider(user.getProvider())
                .latitude(user.getLatitude())
                .longitude(user.getLongitude())
                .attributes(oauthUser.getSafeAttributes())
                .build();
    }

    public RequestNearbyDTO convertToRequestNearbyDto(Request request, Double distance) {
        return RequestNearbyDTO.builder()
                .id(request.getRequest_id())
                .title(request.getTitle())
                .category(request.getCategory())
                .distance(distance)
                .location(request.getLocation())
                .build();
    }

    public UserProfileDTO convertToUserProfileDto(User user) {
        return UserProfileDTO.builder()
                .id(user.getUserId())
                .email(user.getEmail())
                .name(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .roles(user.getRoles().stream().map(Enum::name).collect(Collectors.toSet()))
                .createdRequestsCount(user.getCreatedRequests() != null ? user.getCreatedRequests().size() : 0)
                .build();
    }
}
