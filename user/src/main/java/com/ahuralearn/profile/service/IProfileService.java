package com.ahuralearn.profile.service;

import com.ahuralearn.profile.domain.dto.ProfileUpdateDTO;
import com.ahuralearn.profile.domain.po.User;
import com.ahuralearn.profile.domain.vo.MyInformationVO;
import com.ahuralearn.profile.domain.vo.UserSimpleInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * User profile table service
 * </p>
 * Extends MyBatis-Plus {@link IService} (generic CRUD like getById is inherited)
 * and adds the single read used by the "My Information" page.
 *
 * @author Dariush
 * @since 2026-06-21
 */
public interface IProfileService extends IService<User> {

    /** Assemble the My Information page data (basic profile + learning profile) for a user. */
    MyInformationVO getMyInformation(Long userId);

    /** Save the edited My Information page back to the user's profile row. */
    void updateMyInformation(Long userId, ProfileUpdateDTO dto);

    /** Basic info for the top nav (current user from the JWT): username, email, avatar. */
    UserSimpleInfoVO getSimpleInfo();
}
