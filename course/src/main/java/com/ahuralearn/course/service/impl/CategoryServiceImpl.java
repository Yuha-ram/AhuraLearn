package com.ahuralearn.course.service.impl;

import com.ahuralearn.course.domain.po.Category;
import com.ahuralearn.course.mapper.CategoryMapper;
import com.ahuralearn.course.service.ICategoryService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override
    public Map<Long, String> getCategoryNamesByIds(Set<Long> ids) {
        List<Category> list = lambdaQuery()
                .in(Category::getId, ids)
                .list();
        return list.stream().collect(Collectors.toMap(Category::getId, Category::getName));
    }
}
