package com.linkedme.profile.service;

import com.linkedme.profile.dto.CreateOrUpdateProfileRequest;
import com.linkedme.profile.dto.ProfileResponse;
import com.linkedme.profile.entity.Profile;
import com.linkedme.profile.repository.ProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final ProfileRepository profileRepository;

    @Transactional(readOnly = true)
    public ProfileResponse getByUserIdOrNull(Long userId) {
        return profileRepository.findByUserId(userId)
                .map(this::toResponse)
                .orElse(null);
    }

    @Transactional
    public ProfileResponse createOrUpdate(Long userId, CreateOrUpdateProfileRequest request) {
        Profile profile = profileRepository.findByUserId(userId).orElse(null);
        if (profile == null) {
            profile = Profile.builder()
                    .userId(userId)
                    .headline(request.getHeadline())
                    .about(request.getAbout())
                    .build();
        } else {
            if (request.getHeadline() != null) profile.setHeadline(request.getHeadline());
            if (request.getAbout() != null) profile.setAbout(request.getAbout());
        }
        profile = profileRepository.save(profile);
        return toResponse(profile);
    }

    private ProfileResponse toResponse(Profile p) {
        return ProfileResponse.builder()
                .id(p.getId())
                .userId(p.getUserId())
                .headline(p.getHeadline())
                .about(p.getAbout())
                .createdAt(p.getCreatedAt())
                .updatedAt(p.getUpdatedAt())
                .build();
    }
}
