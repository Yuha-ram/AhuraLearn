package com.ahuralearn.featuretest.controller;


import com.ahuralearn.featuretest.domain.po.User;
import org.springframework.web.bind.annotation.RequestMapping;

import com.ahuralearn.featuretest.service.IUserService;
import com.ahuralearn.common.utils.JWTUtils;
import com.ahuralearn.common.utils.UserContext;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author Yorina
 * @since 2026-06-02
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "userController", description = "用户相关的后台接口")
public class UserController {

    private final IUserService userService;

    @PostMapping("/save")
    @Operation(summary = "新增/注册用户")
    public void saveUser(@RequestBody User user) {
        userService.saveUser(user);
    }

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User user) {
        Map<String, Object> map = new HashMap<>();
        User u = userService.login(user);
        if (u != null) {
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", u.getId());
            payload.put("name", u.getUsername());
            //generate jwt
            String token = JWTUtils.createToken(payload);
            map.put("accessToken", token);
            map.put("state", true);
            map.put("msg", "认证成功");
        } else {
            map.put("state", false);
            map.put("msg", "认证失败");
        }
        return map;
    }

    @PostMapping("/test")
    public Map<String, Object> test() {
        Long userId = UserContext.getUser();
        log.info("当前的userId是{}", userId);
        Map<String, Object> map = new HashMap<>();
        map.put("team", "H2H");
        map.put("member", "Jiwoo");
        return map;
    }

    @PostMapping(value = "/upload", consumes = "multipart/form-data")
    public void uploadAvatar(@RequestParam("file") MultipartFile file) {
//        return Result.success();
    }

    @GetMapping("/query")
    @Operation(summary = "测试用户")
    public void queryUser(@RequestBody User user) {
        userService.saveUser(user);
    }
}
