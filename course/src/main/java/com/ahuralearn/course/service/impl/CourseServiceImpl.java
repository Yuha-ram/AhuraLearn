package com.ahuralearn.course.service.impl;

import com.ahuralearn.common.domain.vo.PageVO;
import com.ahuralearn.common.utils.BeanUtils;
import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.common.utils.StringUtils;
import com.ahuralearn.course.domain.po.Course;
import com.ahuralearn.course.domain.query.CoursePageQuery;
import com.ahuralearn.course.domain.vo.CourseBasicInfoVO;
import com.ahuralearn.course.enums.CourseStatus;
import com.ahuralearn.course.enums.DifficultyLevel;
import com.ahuralearn.course.mapper.CourseMapper;
import com.ahuralearn.course.service.ICourseService;
import com.ahuralearn.course.service.IInstructorService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * Core course information table 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-13
 */
@Service
@AllArgsConstructor
public class CourseServiceImpl extends ServiceImpl<CourseMapper, Course> implements ICourseService {

    private final IInstructorService instructorService;

    @Override
    public List<CourseBasicInfoVO> getTrendingCourses() {
        return getCoursesBasicInfo(1, 8, true, Course::getEnrolledCount, Course::getRating);
    }

    @Override
    public List<CourseBasicInfoVO> getNewCourses() {
        return getCoursesBasicInfo(1, 8, false, Course::getCreateTime, Course::getRating);
    }

    @Override
    public PageVO<CourseBasicInfoVO> queryCoursePage(CoursePageQuery query) {
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<Course>().eq(Course::getStatus, CourseStatus.PUBLISHED);

        // filter field
        // 1.keyword
        String keyword = query.getKeyword();
        if (StringUtils.isNotBlank(keyword)) {
            wrapper.and(w -> w
                    .like(Course::getName, keyword)
                    .or()
                    .like(Course::getSubtitle, keyword)

            );
        }
        // 2.minRating
        Double minRating = query.getMinRating();
        if (minRating != null)
            wrapper.ge(Course::getRating, minRating);

        // 3.difficulty
        String difficulty = query.getDifficulty();
        Integer value = DifficultyLevel.getSafeValue(difficulty);
        if (value != null) {
            // difficulty field is saved by Integer, instead of String
            wrapper.eq(Course::getDifficultyLevel, value);
        }

        // Sort field
        String sortBy = query.getSortBy();
        if ("newest".equals(sortBy)) {
            wrapper.orderByDesc(Course::getCreateTime);
        } else if ("highest_rated".equals(sortBy)) {
            wrapper.orderByDesc(Course::getRating, Course::getEnrolledCount);
        } else { // default or Most Relevant
            wrapper.orderByDesc(Course::getEnrolledCount, Course::getRating);
        }

        Page<Course> page = this.page(new Page<>(query.getPageNo(), query.getPageSize()), wrapper);
        List<Course> list = page.getRecords();
        if (CollUtils.isEmpty(list))
            return PageVO.empty(page);

        // get the instructor ids
        Set<Long> instructorIds = list.stream().map(Course::getInstructorId).collect(Collectors.toSet());

        // shift the instructor id to instructor name
        Map<Long, String> instructorMap = instructorService.getInstructorNamesByIds(instructorIds);

        // iterate the list to get vo
        List<CourseBasicInfoVO> vos = new ArrayList<>(list.size());
        for (Course course : list) {
            CourseBasicInfoVO vo = BeanUtils.copyBean(course, CourseBasicInfoVO.class);
            // special handling for the instructorName field
            vo.setInstructorName(instructorMap.getOrDefault(course.getInstructorId(),
                    "Instructor not detected"));

            vos.add(vo);
        }
        return PageVO.of(page, vos);
    }

    private List<CourseBasicInfoVO> getCoursesBasicInfo(long pageNum, long pageSize, boolean forTrending, SFunction<Course, ?>... orderItems) {
        LocalDateTime now = LocalDateTime.now();
        Page<Course> page = lambdaQuery()
                .eq(Course::getStatus, CourseStatus.PUBLISHED) // make sure the course is available
                .ge(forTrending, Course::getCreateTime, now.minusMonths(1L)) // get the courses from the last month
                .orderByDesc(Arrays.asList(orderItems))
                .page(new Page<>(pageNum, pageSize, false));
        List<Course> list = page.getRecords();
        if (CollUtils.isEmpty(list))
            return CollUtils.emptyList();

        // get the instructor ids
        Set<Long> instructorIds = list.stream().map(Course::getInstructorId).collect(Collectors.toSet());

        // shift the instructor id to instructor name
        Map<Long, String> instructorMap = instructorService.getInstructorNamesByIds(instructorIds);

        // iterate the list to get vo
        List<CourseBasicInfoVO> vos = new ArrayList<>(list.size());
        for (Course course : list) {
            CourseBasicInfoVO vo = BeanUtils.copyBean(course, CourseBasicInfoVO.class);
            // special handling for the instructorName field
            vo.setInstructorName(instructorMap.getOrDefault(course.getInstructorId(),
                    "Instructor not detected"));

            vos.add(vo);
        }
        return vos;
    }

}
