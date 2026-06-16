package com.ahuralearn.course.service;

import com.ahuralearn.course.domain.po.Category;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 * Course Category Table 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-15
 */
public interface ICategoryService extends IService<Category> {
    String getCategoryNameById(Long id);
}
