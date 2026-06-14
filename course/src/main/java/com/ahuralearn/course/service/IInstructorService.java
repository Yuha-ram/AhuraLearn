package com.ahuralearn.course.service;

import com.ahuralearn.course.domain.po.Instructor;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 * Instructor table 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-13
 */
public interface IInstructorService extends IService<Instructor> {

    Map<Long,String> getInstructorNamesByIds(Set<Long> instructorIds);

}
