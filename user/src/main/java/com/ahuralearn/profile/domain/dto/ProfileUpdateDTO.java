package com.ahuralearn.profile.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/**
 * Request body for updating the My Information page. The fields mirror exactly
 * what the page edits and are stored as-is, so an edit round-trips unchanged.
 *
 * @author Dariush
 * @since 2026-06-21
 */
@Data
@Schema(description = "My Information update request")
public class ProfileUpdateDTO {

    /** display name — stored as the login {@code username} on the consolidated table */
    private String name;

    /** professional title / role line — stored in the {@code title} column */
    private String role;

    /** about-me text */
    private String description;

    /** avatar image URL */
    private String avatar;

    /** age shown on the page (numeric column; blank/non-numeric is stored as null) */
    private String age;

    /** gender label */
    private String gender;

    /** country or region */
    private String region;

    /** birthday, ISO yyyy-MM-dd (DATE column) */
    private String birthday;

    /** highest education */
    private String education;

    /** current occupation */
    private String occupation;

    /** comma-separated skills */
    private String skills;
}
