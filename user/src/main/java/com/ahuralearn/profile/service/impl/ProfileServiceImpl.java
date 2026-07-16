package com.ahuralearn.profile.service.impl;

import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.StringUtils;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.profile.domain.dto.ProfileUpdateDTO;
import com.ahuralearn.profile.domain.po.User;
import com.ahuralearn.profile.domain.vo.LearningProfileVO;
import com.ahuralearn.profile.domain.vo.MyInformationVO;
import com.ahuralearn.profile.domain.vo.ProfileVO;
import com.ahuralearn.profile.domain.vo.UserSimpleInfoVO;
import com.ahuralearn.profile.mapper.UserMapper;
import com.ahuralearn.profile.service.IProfileService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * <p>
 * User profile table service impl
 * </p>
 * Backs the "My Information" page. It reads the {@link User} row (the shared
 * {@code user} table) and splits it into the two sections the UI expects
 * (basic profile + learning profile), and saves the edited page back to that
 * same row. Since the consolidated user table (2026-07-15) the page's "name" is
 * the login {@code username} and "role" is the {@code title} column; {@code age}
 * and {@code birthday} are typed columns, converted to/from the page's strings
 * here so the frontend contract stays unchanged.
 *
 * @author Dariush
 * @since 2026-06-21
 */
@Service
public class ProfileServiceImpl extends ServiceImpl<UserMapper, User> implements IProfileService {

    @Override
    public MyInformationVO getMyInformation(Long userId) {
        requireOwnership(userId);
        User profile = getById(userId);
        if (profile == null)
            throw new BusinessException(ResultCode.NOT_FOUND);

        // split the single row into the two sections the page renders.
        // basic profile: username doubles as the display name, title is the page's "role"
        ProfileVO profileVO = new ProfileVO(
                profile.getUsername(),
                profile.getTitle(),
                profile.getBio(),
                profile.getAvatar()
        );

        // learning profile: age/birthday are typed columns, the page expects strings
        LearningProfileVO learningProfileVO = new LearningProfileVO(
                profile.getAge() == null ? null : String.valueOf(profile.getAge()),
                profile.getGender(),
                profile.getRegion(),
                profile.getBirthday() == null ? null : profile.getBirthday().toString(),
                profile.getEducation(),
                profile.getOccupation(),
                profile.getSkills()
        );
        return new MyInformationVO(profileVO, learningProfileVO);
    }

    @Override
    public void updateMyInformation(Long userId, ProfileUpdateDTO dto) {
        requireOwnership(userId);
        // map the page payload back onto the user row. Only profile fields are set,
        // so updateById leaves the auth-owned columns (password/role/status…) untouched.
        // The page's "name" IS the login username on the consolidated table.
        User profile = new User()
                .setId(userId)
                .setUsername(dto.getName())
                .setTitle(dto.getRole())
                .setBio(dto.getDescription())
                .setAvatar(dto.getAvatar())
                .setAge(parseAge(dto.getAge()))
                .setGender(dto.getGender())
                .setRegion(dto.getRegion())
                .setBirthday(parseBirthday(dto.getBirthday()))
                .setEducation(dto.getEducation())
                .setOccupation(dto.getOccupation())
                .setSkills(dto.getSkills());
        try {
            saveOrUpdate(profile);
        } catch (DuplicateKeyException e) {
            // username is unique on the consolidated table — the chosen name is taken
            throw new BusinessException(ResultCode.DUPLICATE_RESOURCE);
        }
    }

    /** The page shows age as text; the column is numeric. Blank or non-numeric → null. */
    private Integer parseAge(String age) {
        if (StringUtils.isEmpty(age))
            return null;
        try {
            return Integer.valueOf(age.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    /** The page always sends ISO yyyy-MM-dd; anything else is a bad request. */
    private LocalDate parseBirthday(String birthday) {
        if (StringUtils.isEmpty(birthday))
            return null;
        try {
            return LocalDate.parse(birthday.trim());
        } catch (DateTimeParseException e) {
            throw new BusinessException(ResultCode.PARAM_ERROR);
        }
    }

    /**
     * The {@code userId} path variable must be the caller's own id (from the JWT) —
     * a user may only ever read or edit their own profile.
     */
    private void requireOwnership(Long userId) {
        if (userId == null || !userId.equals(UserContext.getUser()))
            throw new BusinessException(ResultCode.FORBIDDEN);
    }

    @Override
    public UserSimpleInfoVO getSimpleInfo() {
        // current user from the JWT (UserContext is set by the JWT interceptor)
        User user = getById(UserContext.getUser());
        if (user == null)
            throw new BusinessException(ResultCode.NOT_FOUND);

        // enrolledCourses stays 0 here — the course/learning domain is the teammates' part
        return new UserSimpleInfoVO(user.getUsername(), user.getEmail(), user.getAvatar(), 0);
    }
}
