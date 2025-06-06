package com.training.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import com.training.model.User;
import com.training.service.UserService;
import com.training.util.CookieUtils;

/**
 * 登录拦截器
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        // 从cookie中获取用户ID
        String userId = CookieUtils.getCookie(request, "userId");
        
        // 如果用户ID为空，则未登录
        if (StringUtils.isBlank(userId)) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"用户未登录\",\"data\":null}");
            return false;
        }
        
        // 根据用户ID查询用户
        User user = userService.getUserById(Integer.parseInt(userId));
        if (user == null) {
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"code\":401,\"msg\":\"用户不存在\",\"data\":null}");
            return false;
        }
        
        // 将用户信息存入请求属性
        request.setAttribute("user", user);
        return true;
    }
} 