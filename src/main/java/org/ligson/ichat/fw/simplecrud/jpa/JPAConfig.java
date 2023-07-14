package org.ligson.ichat.fw.simplecrud.jpa;

import org.ligson.ichat.fw.context.SessionContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import java.util.Optional;

@Configuration
@EnableJpaAuditing
public class JPAConfig {
    @Bean
    @ConditionalOnMissingBean(AuditorAware.class)
    AuditorAware<String> auditorProvider(SessionContext sessionContext) {
        return () -> (sessionContext.getCurrentUser() != null) ? Optional.of(sessionContext.getCurrentUser().getId()) : Optional.empty();
    }
}
