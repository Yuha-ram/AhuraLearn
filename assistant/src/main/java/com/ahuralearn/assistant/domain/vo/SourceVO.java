package com.ahuralearn.assistant.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * A credible external reference shown as a verification source in the research
 * assistant: a real paper title plus a link to it (a Crossref DOI URL).
 *
 * @author Dariush
 * @since 2026-06-29
 */
@Data
@AllArgsConstructor
@Schema(description = "Verification source (paper title + link)")
public class SourceVO {

    /** paper / article title */
    private String title;

    /** link to the source (DOI resolver URL) */
    private String url;
}
