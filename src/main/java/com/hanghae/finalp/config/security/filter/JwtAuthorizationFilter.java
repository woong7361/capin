package com.hanghae.finalp.config.security.filter;


import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.hanghae.finalp.config.security.PrincipalDetails;
import com.hanghae.finalp.util.JwtTokenUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.hanghae.finalp.util.JwtTokenUtils.*;


//시큐리티 필터중 BasicAuthenticationFilter가 있다
//권한이나 인증이 필요한 특정 주소를 입력했을 때 위 필터를 무조건 탄다
//만약 권한이나 인증이 필요한 주소가 아니라면 이 필터를 타지 않는다.
@Slf4j
public class JwtAuthorizationFilter extends BasicAuthenticationFilter {

    private final JwtTokenUtils jwtTokenUtils;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager,
                                  AuthenticationEntryPoint authenticationEntryPoint,
                                  JwtTokenUtils jwtTokenUtils) {
        super(authenticationManager, authenticationEntryPoint);
        this.jwtTokenUtils = jwtTokenUtils;
    }

    //인증이나 권한이 필요하면 doFilterInternal 탄다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        log.info("인증 시도중...");
        try {
            String jwtHeader = request.getHeader(TOKEN_HEADER_NAME);

            //헤더가 있는지 확인
            if (jwtHeader == null || !jwtHeader.startsWith(TOKEN_NAME_WITH_SPACE)) {
                throw new IllegalArgumentException("no header request");
            }
            String jwtToken = jwtTokenUtils.getTokenFromHeader(request);

            DecodedJWT decodedJWT = jwtTokenUtils.verifyToken(jwtToken);

            PrincipalDetails principalDetails = new PrincipalDetails(
                    decodedJWT.getClaim(CLAIM_ID).asLong(),
                    ((Claim) decodedJWT.getClaim(CLAIM_USERNAME)).asString()
            );

            Authentication authentication =
                    new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

            //강제로 시큐리티의 세션에 접근하여 Authentication 객체를 저장
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (TokenExpiredException e) {
            request.setAttribute("error", "accessTokenExpire");
        } catch (Exception e) {
            request.setAttribute("error", e);
        } finally { //에러가 발생시 authenticationentrypoint로
            chain.doFilter(request, response);
        }
    }
}
