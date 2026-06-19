package com.ahuralearn.learning.controller;


import com.ahuralearn.learning.domain.dto.LearningRecordFormDTO;
import com.ahuralearn.learning.service.ILearningRecordService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

/**
 * <p>
 * Section learning record table 前端控制器
 * </p>
 *
 * @author Yorina
 * @since 2026-06-17
 */
@RestController
@RequestMapping("/learning-records")
@Tag(name = "learningRecordController")
@RequiredArgsConstructor
public class LearningRecordController {

    private final ILearningRecordService recordService;

    @Operation(summary = "Submit learning record")
    @PostMapping
    public void addLearningRecord(@RequestBody @Validated LearningRecordFormDTO formDTO) {
        recordService.addLearningRecord(formDTO);
    }

}
