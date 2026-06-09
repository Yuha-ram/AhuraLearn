package com.ahuralearn.featuretest.service;

import com.ahuralearn.featuretest.domain.po.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-02
 */
public interface IUserService extends IService<User> {

    void saveUser(User user);

    User login(User user);
}
