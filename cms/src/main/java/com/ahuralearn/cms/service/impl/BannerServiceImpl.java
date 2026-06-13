package com.ahuralearn.cms.service.impl;

import com.ahuralearn.cms.domain.po.Banner;
import com.ahuralearn.cms.domain.vo.BannerVO;
import com.ahuralearn.cms.mapper.BannerMapper;
import com.ahuralearn.cms.service.IBannerService;
import com.ahuralearn.common.utils.BeanUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * Homepage promotional banners 服务实现类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-13
 */
@Service
public class BannerServiceImpl extends ServiceImpl<BannerMapper, Banner> implements IBannerService {

    @Override
    public List<BannerVO> getBanners() {
        // filter those unactive
        List<Banner> banners = lambdaQuery().eq(Banner::getIsActive, true).list();
        List<BannerVO> bannerVOList = BeanUtils.copyList(banners, BannerVO.class);
        return bannerVOList;
    }
}
