package com.ahuralearn.learning.controller;


import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
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

}
