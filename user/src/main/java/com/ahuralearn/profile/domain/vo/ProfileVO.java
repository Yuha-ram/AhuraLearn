package com.ahuralearn.profile.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Basic profile shown at the top of the My Information page.
 *
 * @author Dariush
 * @since 2026-06-21
 */
@Data
@AllArgsConstructor
@Schema(description = "Basic profile section")
public class ProfileVO {

    /** display name */
    private String name;

    /** headline / role line */
    private String role;

    /** about-me text */
    private String description;

    /** avatar image URL */
    private String avatar;
}
