package com.ahuralearn.course.controller;

import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * Instructor table 前端控制器
 * </p>
 *
 * @author Yorina
 * @since 2026-06-13
 */
@RestController
@RequestMapping("/instructor")
@RequiredArgsConstructor
@Tag(name = "instructorController")
public class InstructorController {

}
