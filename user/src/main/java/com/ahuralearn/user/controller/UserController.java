package com.ahuralearn.user.controller;


import com.ahuralearn.user.domain.po.User;
import com.ahuralearn.user.domain.vo.UserSimpleInfoVO;
import com.ahuralearn.user.service.IUserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * user controller
 * </p>
 *
 * @author Yorina
 * @since 2026-06-10
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@Tag(name = "userController")
public class UserController {

    private final IUserService userService;

    //    @Operation(summary = "query the user by username")
//    @GetMapping("/users")
//    public User queryByUsernameAndPwd(@RequestParam(value = "name") String name,
//                                @RequestParam(value = "password") String pwd) {
//        return userService.queryByUsernameAndPwd(name, pwd);
//    }
    @Operation(summary = "Retrieve current user basic information")
    @GetMapping("/simpleInfo")
    public UserSimpleInfoVO getSimpleInfo() {
        return userService.getSimpleInfo();
    }
}
