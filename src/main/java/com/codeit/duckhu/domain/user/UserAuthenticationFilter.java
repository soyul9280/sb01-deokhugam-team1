package com.codeit.duckhu.domain.user;

import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.exception.NotFoundUserException;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import com.codeit.duckhu.global.exception.ErrorCode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class UserAuthenticationFilter extends OncePerRequestFilter {
    private final UserRepository userRepository;

    public UserAuthenticationFilter(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String headerId = request.getHeader("Deokhugam-Request-User-ID");
        if(headerId != null) {
            try{
                UUID userId = UUID.fromString(headerId); //헤더에서 UUID파싱
                User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundUserException(ErrorCode.NOT_FOUND_USER));// DB에서 사용자 조회
                request.setAttribute("authenticatedUser", user); //사용자 정보 저장
            }catch (Exception e) {
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                return;
            }
        }else {
            String path=request.getRequestURI();
            if (!path.equals("/") && !path.startsWith("/api/users/login")&& !path.startsWith("/api/users")) {
                response.setStatus(HttpStatus.UNAUTHORIZED.value());
                return;//로그인 없이 접근한 경우 차단
            }
        }
        filterChain.doFilter(request, response);
    }
}
