package com.help.community.security.config;

import com.help.community.security.jwt.JwtTokenFilter;
import com.help.community.security.jwt.JwtTokenUtil;
import com.help.community.security.services.UserDetailsServiceImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JwtSecurityBeansConfig {

    @Bean
    public JwtTokenFilter jwtTokenFilter(JwtTokenUtil jwtTokenUtil,
                                         UserDetailsServiceImpl userDetailsService) {
        return new JwtTokenFilter(jwtTokenUtil, userDetailsService);
    }
}
