package com.desen.desenmall.order.interceptor;


import com.desen.common.constant.AuthServerConstant;
import com.desen.common.vo.MemberRsepVo;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Component
public class LoginUserInterceptor implements HandlerInterceptor {

    public static ThreadLocal<MemberRsepVo> loginUser = new ThreadLocal<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        HttpSession session = request.getSession();
        MemberRsepVo user = (MemberRsepVo) session.getAttribute(AuthServerConstant.LOGIN_USER);
        if (user != null){
            loginUser.set(user);
            return true;
        }
        else {
            request.getSession().setAttribute("msg","请先进行登录");
            response.sendRedirect("http://auth.desenmall.com/login.html");
            return false;
        }
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {

        loginUser.remove();//用完移除，避免内存泄露
    }
}

