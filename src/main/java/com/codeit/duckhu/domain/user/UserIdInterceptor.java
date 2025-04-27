package com.codeit.duckhu.domain.user;

import com.codeit.duckhu.domain.user.entity.User;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

@Component
@RequiredArgsConstructor
public class UserIdInterceptor implements HandlerInterceptor {
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Object userObj = request.getAttribute("authenticatedUser");
        if (userObj != null && userObj instanceof User user) {
            response.setHeader("Deokhugam-Request-User-Id", user.getId().toString());
        }    }
}
