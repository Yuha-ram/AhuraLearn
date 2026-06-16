package com.ahuralearn.course.service.impl;

import com.ahuralearn.common.utils.BeanUtils;
import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.course.domain.po.Instructor;
import com.ahuralearn.course.domain.vo.InstructorVO;
import com.ahuralearn.course.mapper.InstructorMapper;
import com.ahuralearn.course.service.IInstructorService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * Instructor table 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-13
 */
@Service
public class InstructorServiceImpl extends ServiceImpl<InstructorMapper, Instructor> implements IInstructorService {

    @Override
    public Map<Long, String> getInstructorNamesByIds(Set<Long> instructorIds) {
        // params validation
        if (CollUtils.isEmpty(instructorIds))
            return CollUtils.emptyMap();

        List<Instructor> list = lambdaQuery().in(Instructor::getId, instructorIds).list();
        if (CollUtils.isEmpty(list))
            return CollUtils.emptyMap();

        return list.stream().collect(Collectors.toMap(Instructor::getId, Instructor::getName));
    }

    @Override
    public InstructorVO getInstructorVOById(Long instructorId) {
        Instructor instructor = getById(instructorId);
        InstructorVO instructorVO = null;
        if (instructor != null) {
            instructorVO = BeanUtils.copyBean(instructor, InstructorVO.class);
        }
        return instructorVO;
    }
}
