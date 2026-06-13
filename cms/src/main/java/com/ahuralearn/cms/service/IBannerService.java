package com.ahuralearn.cms.service;

import com.ahuralearn.cms.domain.po.Banner;
import com.ahuralearn.cms.domain.vo.BannerVO;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

/**
 * <p>
 * Homepage promotional banners 服务类
 * </p>
 *
 * @author Yorina
 * @since 2026-06-13
 */
public interface IBannerService extends IService<Banner> {

    List<BannerVO> getBanners();
}
