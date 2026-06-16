package com.ahuralearn.course.service.impl;

import com.ahuralearn.course.domain.po.Category;
import com.ahuralearn.course.mapper.CategoryMapper;
import com.ahuralearn.course.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * <p>
 * Course Category Table 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-15
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements ICategoryService {

    @Override
    public String getCategoryNameById(Long id) {
        Category category = getById(id);
        return category != null ? category.getName() : "Uncategorized";
    }
}
