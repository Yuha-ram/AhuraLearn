package com.ahuralearn.profile.mapper;

import com.ahuralearn.profile.domain.po.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.springframework.stereotype.Repository;

/**
 * <p>
 * User table Mapper (profile module's view)
 * </p>
 * Inherits MyBatis-Plus {@link BaseMapper} CRUD. The page only needs a
 * primary-key lookup and an update, so no custom statements are declared here.
 *
 * @author Dariush
 * @since 2026-06-29
 */
@Repository("profileUserMapper")
public interface UserMapper extends BaseMapper<User> {

}
