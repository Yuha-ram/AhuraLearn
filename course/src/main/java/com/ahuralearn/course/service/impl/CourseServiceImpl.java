package com.ahuralearn.course.service.impl;

import com.ahuralearn.common.domain.vo.PageVO;
import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.BeanUtils;
import com.ahuralearn.common.utils.CollUtils;
import com.ahuralearn.common.utils.StringUtils;
import com.ahuralearn.common.utils.TimeUtils;
import com.ahuralearn.course.domain.dto.CourseVectorDTO;
import com.ahuralearn.course.domain.po.Course;
import com.ahuralearn.course.domain.po.CourseSection;
import com.ahuralearn.course.domain.query.CoursePageQuery;
import com.ahuralearn.course.domain.vo.*;
import com.ahuralearn.course.enums.CourseStatus;
import com.ahuralearn.course.enums.DifficultyLevel;
import com.ahuralearn.course.mapper.CourseMapper;
import com.ahuralearn.course.service.*;
import com.ahuralearn.media.service.IMediaService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.net.MalformedURLException;
import java.net.URL;
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

    private static final int HOME_PAGE_COURSE_LIMIT = 8;

    private final IInstructorService instructorService;
    private final ICategoryService categoryService;
    private final ICourseChapterService chapterService;
    private final ICourseSectionService sectionService;
    private final IMediaService mediaService;

    @Override
    public List<CourseBasicInfoVO> getTrendingCourses() {
        // 1.construct the wrapper and page
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        LocalDateTime now = LocalDateTime.now();
        wrapper.eq(Course::getStatus, CourseStatus.PUBLISHED) // make sure the course is available
                .ge(Course::getCreateTime, now.minusMonths(1L)) // get the courses from the last month
                .orderByDesc(Course::getEnrolledCount, Course::getRating);

        Page<Course> page = new Page<>(1, HOME_PAGE_COURSE_LIMIT, false);

        // 2.get data
        return buildCoursePageVO(page, wrapper).getList();
    }

    @Override
    public List<CourseBasicInfoVO> getNewCourses() {
        // 1.construct the wrapper and page
        LambdaQueryWrapper<Course> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Course::getStatus, CourseStatus.PUBLISHED) // make sure the course is available
                .orderByDesc(Course::getCreateTime, Course::getRating);

        Page<Course> page = new Page<>(1, HOME_PAGE_COURSE_LIMIT, false);
        // 2.get data
        return buildCoursePageVO(page, wrapper).getList();
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

        Page<Course> page = new Page<>(query.getPageNo(), query.getPageSize());
        return buildCoursePageVO(page, wrapper);
    }

    // obtain courses data
    private PageVO<CourseBasicInfoVO> buildCoursePageVO(Page<Course> page, LambdaQueryWrapper<Course> wrapper) {
        Page<Course> coursePage = this.page(page, wrapper);
        List<Course> list = coursePage.getRecords();
        if (CollUtils.isEmpty(list))
            return PageVO.empty(coursePage);

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
        return PageVO.of(coursePage, vos);
    }

    @Override
    public CourseFullInfoVO getCourseDetail(Long courseId) {
        if (courseId == null)
            throw new BusinessException(ResultCode.PARAM_MISSING);

        // 1.get the course by id
        Course course = getById(courseId);
        if (course == null)
            throw new BusinessException("Course not found");
        if (course.getStatus() != CourseStatus.PUBLISHED)
            throw new BusinessException("Course is not available");

        // 2.Special handling for instructor field
        Long instructorId = course.getInstructorId();
        InstructorVO instructorVO = instructorService.getInstructorVOById(instructorId);

        // 3.Special handling for category field
        Long categoryId = course.getCategoryId();
        String categoryName = categoryService.getCategoryNameById(categoryId);

        // 4.encapsulate to VO
        CourseFullInfoVO vo = BeanUtils.copyBean(course, CourseFullInfoVO.class);
        vo.setInstructor(instructorVO);
        vo.setCategoryName(categoryName);
        return vo;
    }

    @Override
    public CourseSyllabusVO getSyllabus(Long courseId) {
        if (courseId == null)
            throw new BusinessException(ResultCode.PARAM_MISSING);

        // 1.Query chapters, lacking section list
        List<ChapterVO> chapters = chapterService.getChaptersByCourseId(courseId);
        if (CollUtils.isEmpty(chapters)) // return empty list
            return new CourseSyllabusVO(CollUtils.emptyList());

        // 2.Query sections
        // key is chapterId, val is the series of sections within this chapter
        Map<Long, List<CourseSection>> sectionMap = sectionService.getSectionsByCourseId(courseId);

        // 3.Assemble vo
        for (ChapterVO chapter : chapters) {
            Long chapterId = chapter.getId();
            List<CourseSection> sectionList = sectionMap.getOrDefault(chapterId, CollUtils.emptyList());

            if (CollUtils.isNotEmpty(sectionList)) {
                List<SectionVO> sections = BeanUtils.copyList(sectionList, SectionVO.class);
                // Format duration for each section/video (e.g., 4:12)
                sections.forEach(s -> s.setDurationFormat(TimeUtils.formatTime(s.getDuration())));
                chapter.setSections(sections);
            } else {
                chapter.setSections(CollUtils.emptyList());
            }
        }

        return new CourseSyllabusVO(chapters);
    }

    @Override
    public CoursePlayDetailsVO getCoursePlayDetails(Long courseId, Long sectionId) {
        // 1.params validation
        if (courseId == null || sectionId == null)
            throw new BusinessException(ResultCode.PARAM_MISSING);

        Course course = getById(courseId);
        if (course == null) // invalid course
            throw new BusinessException("Course is not found");

        // 2.query chapters & sections
        List<ChapterVO> chapters = chapterService.getChaptersByCourseId(courseId);
        if (CollUtils.isNotEmpty(chapters)) {
            Map<Long, List<CourseSection>> sectionMap = sectionService.getSectionsByCourseId(courseId);
            for (ChapterVO chapter : chapters) {
                List<CourseSection> sectionList = sectionMap.getOrDefault(chapter.getId(), CollUtils.emptyList());
                if (CollUtils.isNotEmpty(sectionList)) {
                    List<SectionVO> sections = BeanUtils.copyList(sectionList, SectionVO.class);
                    sections.forEach(s -> s.setDurationFormat(TimeUtils.formatTime(s.getDuration())));
                    chapter.setSections(sections);
                } else {
                    chapter.setSections(CollUtils.emptyList());
                }
            }
        }
        // 3.retrieve instructor details
        InstructorVO instructor = instructorService.getInstructorVOById(course.getInstructorId());
        // 4.retrieve current section info
        SectionBasicVO currSection = sectionService.getSectionInfoForPlayback(courseId, sectionId);
        // 5.assemble vo
        CoursePlayDetailsVO vo = new CoursePlayDetailsVO();
        vo.setChapters(chapters);
        vo.setInstructor(instructor);
        vo.setCurrentSection(currSection);
        return vo;
    }

    @Override
    public String getPlaybackUrl(Long courseId, Long sectionId) {
        // 1.params validation
        if (courseId == null || sectionId == null)
            throw new BusinessException(ResultCode.PARAM_MISSING);

        // 2.validate section
        CourseSection section = sectionService.getById(sectionId);
        if (section == null || !section.getCourseId().equals(courseId))
            throw new BusinessException("Invalid section access");

        // 3.generate pre-signed url for security
        String videoUrl = section.getVideoUrl();
        if (StringUtils.isBlank(videoUrl))
            throw new BusinessException("No video resources available for this section");

        try {
            // get relative path
            URL url = new URL(videoUrl);
            String objectKey = url.getPath().substring(1);
            // 2 hours later will expire
            Date expiration = new Date(System.currentTimeMillis() + 2 * 3600 * 1000L);
            return mediaService.generateSignedUrl(objectKey, expiration);
        } catch (MalformedURLException e) {
            throw new BusinessException("Invalid video URL format stored in database");
        }
    }

    @Override
    public List<CourseVectorDTO> getCourseMetadata() {
        List<Course> courses = lambdaQuery()
                .eq(Course::getStatus, CourseStatus.PUBLISHED)
                .list();
        if (CollUtils.isEmpty(courses))
            return CollUtils.emptyList();

        // Special handling for difficulty level & categoryName
        Set<Long> categoryIds = courses.stream().map(Course::getCategoryId).collect(Collectors.toSet());
        Map<Long, String> categoryMap = new HashMap<>(categoryIds.size());
        if (CollUtils.isNotEmpty(categoryIds)) {
            categoryMap = categoryService.getCategoryNamesByIds(categoryIds);
        }

        ArrayList<CourseVectorDTO> dtos = new ArrayList<>(courses.size());
        for (Course course : courses) {
            CourseVectorDTO dto = BeanUtils.copyBean(course, CourseVectorDTO.class);
            dto.setDifficultyLevel(course.getDifficultyLevel().getValue());
            dto.setCategoryName(categoryMap.getOrDefault(course.getCategoryId(), "Uncategorized"));
            dtos.add(dto);
        }
        return dtos;
    }
}
