package com.ahuralearn.user.service;

import com.ahuralearn.user.domain.po.User;
import com.ahuralearn.user.domain.vo.UserSimpleInfoVO;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * user table service
 * </p>
 *
 * @author Yorina
 * @since 2026-06-10
 */
public interface IUserService extends IService<User> {

    User queryByUsernameAndPwd(String name, String pwd);

    void checkUsernameIsAvailable(String username);

    void checkEmailIsAvailable(String email);

    User checkStatus(Long userId);

    UserSimpleInfoVO getSimpleInfo();
}
