package com.ahuralearn.profile.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Extended "learning profile" details shown on the My Information page. Age is
 * derived from the birthday and the birthday arrives pre-formatted for display.
 *
 * @author Dariush
 * @since 2026-06-21
 */
@Data
@AllArgsConstructor
@Schema(description = "Learning profile section")
public class LearningProfileVO {

    /** age shown on the page */
    private String age;

    /** gender label */
    private String gender;

    /** country or region */
    private String region;

    /** birthday, formatted for display, e.g. "May 18, 2002" */
    private String birthday;

    /** highest education */
    private String education;

    /** current occupation */
    private String occupation;

    /** comma-separated skills */
    private String skills;
}
