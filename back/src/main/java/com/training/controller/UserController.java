package com.training.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.training.model.User;
import com.training.service.UserService;
import com.training.util.CookieUtils;
import com.training.util.Result;

/**
 * 用户控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<User> login(@RequestBody User user, HttpServletResponse response) {
        User loginUser = userService.login(user.getUsername(), user.getPassword());
        if (loginUser != null) {
            // 设置cookie，过期时间3天
            CookieUtils.setCookie(response, "userId", loginUser.getId().toString(), 60 * 60 * 24 * 3);
            return Result.success("登录成功", loginUser);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<Integer> register(@RequestBody User user) {
        int result = userService.register(user);
        if (result > 0) {
            return Result.success("注册成功", user.getId());
        } else {
            return Result.error("注册失败，用户名已存在");
        }
    }

    /**
     * 用户退出登录
     */
    @GetMapping("/logout")
    public Result<Void> logout(HttpServletResponse response) {
        CookieUtils.deleteCookie(response, "userId");
        return Result.success("退出成功");
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/info")
    public Result<User> getUserInfo(HttpServletRequest request) {
        // 从cookie中获取用户ID
        String userIdStr = CookieUtils.getCookie(request, "userId");
        if (userIdStr == null || userIdStr.isEmpty()) {
            return Result.error("用户未登录");
        }
        
        try {
            Integer userId = Integer.parseInt(userIdStr);
            User user = userService.getUserById(userId);
            if (user != null) {
                // 不返回敏感信息
                user.setPassword(null);
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (NumberFormatException e) {
            return Result.error("用户ID格式错误");
        }
    }

    /**
     * 修改用户信息
     */
    @PutMapping
    public Result<Void> updateUser(@RequestBody User user) {
        int result = userService.updateUser(user);
        if (result > 0) {
            return Result.success("修改成功");
        } else {
            return Result.error("修改失败");
        }
    }

    /**
     * 删除用户
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Integer id) {
        int result = userService.deleteUser(id);
        if (result > 0) {
            return Result.success("删除成功");
        } else {
            return Result.error("删除失败");
        }
    }
} 