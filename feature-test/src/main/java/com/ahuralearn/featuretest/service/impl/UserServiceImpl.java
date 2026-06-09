package com.ahuralearn.featuretest.service.impl;

import com.ahuralearn.featuretest.domain.po.User;
import com.ahuralearn.featuretest.mapper.UserMapper;
import com.ahuralearn.featuretest.service.IUserService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-02
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Override
    public void saveUser(User u) {
        save(u);
    }

    @Override
    public User login(User user) {
        return lambdaQuery()
                .eq(User::getUsername, user.getUsername())
                .eq(User::getPassword, user.getPassword())
                .one();
    }
}
