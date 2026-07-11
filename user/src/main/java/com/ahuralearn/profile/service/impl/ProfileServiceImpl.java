package com.ahuralearn.profile.service.impl;

import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.BeanUtils;
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
import org.springframework.stereotype.Service;

/**
 * <p>
 * User profile table service impl
 * </p>
 * Backs the "My Information" page. It reads the {@link User} row (the shared
 * {@code user} table) and splits it into the two sections the UI expects
 * (basic profile + learning profile), and saves the edited page back to that
 * same row. Fields are stored exactly as the page sends them, so an edit always
 * round-trips unchanged.
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
        // basic profile: two fields are renamed (headline->role, bio->description), so build explicitly
        ProfileVO profileVO = new ProfileVO(
                profile.getName(),
                profile.getHeadline(),
                profile.getBio(),
                profile.getAvatar()
        );

        // learning profile: every field name matches User, so copy by name
        LearningProfileVO learningProfileVO = BeanUtils.copyBean(profile, LearningProfileVO.class);
        return new MyInformationVO(profileVO, learningProfileVO);
    }

    @Override
    public void updateMyInformation(Long userId, ProfileUpdateDTO dto) {
        requireOwnership(userId);
        // map the page payload back onto the user row. Only profile fields are set,
        // so updateById leaves the auth-owned columns (username/password/role…) untouched.
        User profile = new User()
                .setId(userId)
                .setName(dto.getName())
                .setHeadline(dto.getRole())
                .setBio(dto.getDescription())
                .setAvatar(dto.getAvatar())
                .setAge(dto.getAge())
                .setGender(dto.getGender())
                .setRegion(dto.getRegion())
                .setBirthday(dto.getBirthday())
                .setEducation(dto.getEducation())
                .setOccupation(dto.getOccupation())
                .setSkills(dto.getSkills());
        saveOrUpdate(profile);
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
