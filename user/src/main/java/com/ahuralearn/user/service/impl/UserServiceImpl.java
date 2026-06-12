package com.ahuralearn.user.service.impl;

import cn.hutool.crypto.digest.BCrypt;
import com.ahuralearn.common.enums.ResultCode;
import com.ahuralearn.common.exceptions.BusinessException;
import com.ahuralearn.common.utils.BeanUtils;
import com.ahuralearn.common.utils.StringUtils;
import com.ahuralearn.common.utils.UserContext;
import com.ahuralearn.user.domain.po.User;
import com.ahuralearn.user.domain.vo.UserSimpleInfoVO;
import com.ahuralearn.user.enums.UserStatus;
import com.ahuralearn.user.mapper.UserMapper;
import com.ahuralearn.user.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * user table service impl
 * </p>
 *
 * @author Yorina
 * @since 2026-06-10
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public User queryByUsernameAndPwd(String name, String pwd) {
        if (StringUtils.isBlank(name) || StringUtils.isBlank(pwd))
            throw new BusinessException(ResultCode.PARAM_MISSING);

        User user = lambdaQuery().eq(User::getUsername, name).one();
        if (user == null)
            return null;

        // verify pwd
        if (!BCrypt.checkpw(pwd, user.getPassword())) {
            // not matched
            return null;
        }
        // for safe
        user.setPassword(null);
        return user;
    }

    @Override
    public void checkUsernameIsAvailable(String username) {
        if (StringUtils.isBlank(username))
            throw new BusinessException(ResultCode.PARAM_MISSING);

        User user = lambdaQuery().eq(User::getUsername, username).one();
        //duplicated
        if (user != null)
            throw new BusinessException("username already been duplicated");
    }

    @Override
    public void checkEmailIsAvailable(String email) {
        if (StringUtils.isBlank(email))
            throw new BusinessException(ResultCode.PARAM_MISSING);

        User user = lambdaQuery().eq(User::getEmail, email).one();
        //duplicated
        if (user != null)
            throw new BusinessException("email already been duplicated");
    }

    @Override
    public User checkStatus(Long userId) {
        User user = getById(userId);
        if (user == null)
            throw new BusinessException("User does not exist");
        if (user.getStatus() == UserStatus.DISABLED) // if disabled, not allowed
            throw new BusinessException("User has been disabled");
        return user;
    }

    @Override
    public UserSimpleInfoVO getSimpleInfo() {
        Long userId = UserContext.getUser();
        User user = lambdaQuery().eq(User::getId, userId).one();
        if (user == null)
            throw new BusinessException("User does not exist");

        UserSimpleInfoVO vo = BeanUtils.copyBean(user, UserSimpleInfoVO.class);
        //TODO 记得写好learning表后 回来完善该业务: 还需将userId作为参数查询已报名的课程

        return vo;
    }
}
