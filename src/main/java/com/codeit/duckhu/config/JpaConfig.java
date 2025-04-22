package com.codeit.duckhu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@Configuration
//@EnableJpaAuditing 통합테스트에서 중복 등록됨....왜지ㅜㅜ???
public class JpaConfig {
}
