package com.ahuralearn.profile.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Full payload for the My Information page: the basic profile plus the learning
 * profile. Returned as-is and wrapped by GlobalResponseAdvice into the shared
 * {@code Result {code,msg,data}} envelope.
 *
 * @author Dariush
 * @since 2026-06-21
 */
@Data
@AllArgsConstructor
@Schema(description = "My Information page payload")
public class MyInformationVO {

    /** top-of-page basic profile */
    private ProfileVO profile;

    /** extended learning profile */
    private LearningProfileVO learningProfile;
}
