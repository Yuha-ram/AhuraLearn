package com.ahuralearn.ai.controller;

import com.ahuralearn.ai.service.CourseVectorSyncService;
import com.ahuralearn.course.domain.dto.CourseVectorDTO;
import com.ahuralearn.course.service.ICourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class storeController {

    private final CourseVectorSyncService courseVectorSyncService;
    private final ICourseService courseService;

    @PostMapping
    public void storeData(){
        courseVectorSyncService.syncCoursesToVectorDb();
    }

    @GetMapping
    public List<CourseVectorDTO> retrieveCourses(){
        return courseService.getCourseMetadata();
    }
}
