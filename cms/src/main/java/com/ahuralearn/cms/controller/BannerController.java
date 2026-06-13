package com.ahuralearn.cms.controller;


import com.ahuralearn.cms.domain.vo.BannerVO;
import com.ahuralearn.cms.service.IBannerService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * Homepage promotional banners 前端控制器
 * </p>
 *
 * @author Yorina
 * @since 2026-06-13
 */
@RestController
@RequestMapping("/cms/banners")
@RequiredArgsConstructor
public class BannerController {

    private final IBannerService bannerService;

    @Operation(summary = "Retrieve banner list")
    @GetMapping
    public List<BannerVO> getBanners() {
        return bannerService.getBanners();
    }
}
