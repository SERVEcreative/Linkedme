package com.linkedme.profile.controller;

import com.linkedme.profile.dto.CreateOrUpdateProfileRequest;
import com.linkedme.profile.dto.ProfileResponse;
import com.linkedme.profile.service.ProfileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profiles")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse> getMyProfile(@RequestHeader("X-User-Id") Long userId) {
        return toResponse(profileService.getByUserIdOrNull(userId));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ProfileResponse> getProfile(@PathVariable Long userId) {
        return toResponse(profileService.getByUserIdOrNull(userId));
    }

    @PutMapping("/me")
    public ResponseEntity<ProfileResponse> createOrUpdateProfile(
            @RequestHeader("X-User-Id") Long userId,
            @RequestBody CreateOrUpdateProfileRequest request) {
        return ResponseEntity.ok(profileService.createOrUpdate(userId, request));
    }

    private ResponseEntity<ProfileResponse> toResponse(ProfileResponse profile) {
        return profile == null
                ? ResponseEntity.notFound().build()
                : ResponseEntity.ok(profile);
    }
}
