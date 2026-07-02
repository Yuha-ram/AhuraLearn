package com.ahuralearn.ai.utils;

import com.ahuralearn.common.utils.StringUtils;
import com.ahuralearn.course.domain.dto.CourseVectorDTO;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CourseEmbeddingFormatter {

    private static final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * @param dto the course data from db
     * @return Vector-compatible String
     */
    public static String courseFormatter(CourseVectorDTO dto) {
        if (dto == null)
            return "";

        StringBuilder sb = new StringBuilder();

        if (StringUtils.isNotBlank(dto.getName()))
            sb.append("Course Title: ").append(dto.getName()).append("\n");

        if (StringUtils.isNotBlank(dto.getSubtitle()))
            sb.append("Subtitle: ").append(dto.getSubtitle()).append("\n");

        if (StringUtils.isNotBlank(dto.getCategoryName()))
            sb.append("Category: ").append(dto.getCategoryName()).append("\n");

        if (dto.getDifficultyLevel() != null) {
            sb.append("Difficulty Level: ");
            switch (dto.getDifficultyLevel()) {
                case 0:
                    sb.append("Introductory course designed for absolute beginners.\n");
                    break;
                case 1:
                    sb.append("Intermediate course designed for learners with foundational knowledge.\n");
                    break;
                case 2:
                    sb.append("Advanced, in-depth course intended for experienced professionals.\n");
                    break;
                default:
                    sb.append("Comprehensive course suitable for all skill levels.\n");
            }
        }

        if (dto.getHoursRequired() != null) {
            sb.append("Estimated Duration: ");
            int hours = dto.getHoursRequired();
            if (hours < 10) {
                sb.append("A concise crash course, estimated to take ").append(hours).append(" hours to complete.\n");
            } else if (hours < 30) {
                sb.append("A medium-length practical course, requiring approximately ").append(hours)
                        .append(" hours to complete.\n");
            } else {
                sb.append("A comprehensive and in-depth systematic course, requiring ").append(hours)
                        .append(" hours of immersive study.\n");
            }
        }

        if (StringUtils.isNotBlank(dto.getDescription()))
            sb.append("Course Description: ").append(dto.getDescription()).append("\n");

        if (StringUtils.isNotBlank(dto.getOutcomes())) {
            sb.append("Learning Outcomes:\n");
            try {
                List<String> list = objectMapper.readValue(
                        dto.getOutcomes(),
                        new TypeReference<List<String>>() {
                        });
                for (int i = 0; i < list.size(); i++) {
                    sb.append(i + 1).append(". ").append(list.get(i)).append("\n");
                }
            } catch (Exception e) {
                log.warn("Failed to parse outcomes JSON for course: {}", dto.getName(), e);
                String fallbackText = dto.getOutcomes().replace("[", "").replace("]", "").replace("\"", "");
                sb.append(fallbackText).append("\n");
            }
        }

        return sb.toString();
    }
}
