package com.ahuralearn.profile.controller;

import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.profile.domain.dto.ProfileUpdateDTO;
import com.ahuralearn.profile.domain.vo.MyInformationVO;
import com.ahuralearn.profile.service.IProfileService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * User profile controller
 * </p>
 * REST endpoint for the "My Information" page. Returns a plain VO which
 * GlobalResponseAdvice wraps into the shared {@code Result {code,msg,data}}
 * envelope. The target user is the current user from the JWT
 * ({@code UserContext}, set by the JWT interceptor) — the frontend only holds
 * a token, never a user id. The row it reads/writes is the shared {@code user}
 * table.
 *
 * @author Dariush
 * @since 2026-06-21
 */
@RestController
@RequestMapping("/api/profile")
@Tag(name = "profileController")
@RequiredArgsConstructor
public class ProfileController {

    private final IProfileService profileService;

    /**
     * Get the My Information page data (basic profile + learning profile) for the current user.
     */
    @Operation(summary = "Retrieve the current user's My Information page data")
    @GetMapping
    public MyInformationVO getMyInformation() {
        return profileService.getMyInformation(UserContext.getUser());
    }

    /**
     * Save the edited My Information page (basic profile + learning profile).
     */
    @Operation(summary = "Update the current user's My Information page data")
    @PutMapping
    public void updateMyInformation(@RequestBody ProfileUpdateDTO dto) {
        profileService.updateMyInformation(UserContext.getUser(), dto);
    }
}
