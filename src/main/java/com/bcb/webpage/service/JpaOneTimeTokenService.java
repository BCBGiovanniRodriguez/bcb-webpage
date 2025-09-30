package com.bcb.webpage.service;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.CronTrigger;
import org.springframework.security.authentication.ott.DefaultOneTimeToken;
import org.springframework.security.authentication.ott.GenerateOneTimeTokenRequest;
import org.springframework.security.authentication.ott.OneTimeToken;
import org.springframework.security.authentication.ott.OneTimeTokenAuthenticationToken;
import org.springframework.security.authentication.ott.OneTimeTokenService;

import com.bcb.webpage.model.webpage.entity.OneTimeTokenEntity;
import com.bcb.webpage.model.webpage.repository.OneTimeTokenRepository;

import jakarta.transaction.Transactional;

public class JpaOneTimeTokenService implements OneTimeTokenService, DisposableBean, InitializingBean {

    private final OneTimeTokenRepository repository;

    private final DatabaseUserDetailsService userDetailsService;

    private ThreadPoolTaskScheduler taskScheduler;

    private Clock clock = Clock.systemUTC();

    private static final String DEFAULT_CLEANUP_CRON = "@hourly";

    private Duration expiry = Duration.ofMinutes(10);

    public JpaOneTimeTokenService(OneTimeTokenRepository repository, DatabaseUserDetailsService userDetailsService) {
        this.repository = repository;
        this.userDetailsService = userDetailsService;
        this.createTaskScheduler(DEFAULT_CLEANUP_CRON);
    }

    public void setExpiryTime(Duration expiryTime) {
		this.expiry = expiryTime;
	}

	public void setCleanupCron(String cleanupCron) {
		this.taskScheduler = createTaskScheduler(cleanupCron);
	}

    @Override
    @NonNull
    public OneTimeToken generate(GenerateOneTimeTokenRequest request) {
        DefaultOneTimeToken oneTimeToken = null;

        try {
            var user = userDetailsService.loadUserByUsernameForOtt(request.getUsername());

            oneTimeToken = new DefaultOneTimeToken(UUID.randomUUID().toString(), user.getUsername(), this.clock.instant().plus(expiry));

            saveOneTimeToken(oneTimeToken, request.getUsername());
        } catch (Exception e) {
            System.out.println("" + e.getLocalizedMessage());
        }

        return oneTimeToken;
    }

    private void saveOneTimeToken(OneTimeToken oneTimeToken, String contractNumber) {
        OneTimeTokenEntity oneTimeTokenEntity = new OneTimeTokenEntity();
        oneTimeTokenEntity.setId(oneTimeToken.getTokenValue());
        oneTimeTokenEntity.setEmail(oneTimeToken.getUsername());
        oneTimeTokenEntity.setExpiresAt(oneTimeToken.getExpiresAt());
        oneTimeTokenEntity.setContractNumber(contractNumber);
        oneTimeTokenEntity.setCreated(Instant.now());

        repository.saveAndFlush(oneTimeTokenEntity);
    }

    @Override
    @Nullable
    public OneTimeToken consume(OneTimeTokenAuthenticationToken authenticationToken) {
        
        Optional<OneTimeTokenEntity> oneTimeTokenEntityOptional = repository.findById(authenticationToken.getTokenValue());

        if (!oneTimeTokenEntityOptional.isPresent()) {
            return null;
        }

        OneTimeTokenEntity oneTimeTokenEntity = oneTimeTokenEntityOptional.get();
        DefaultOneTimeToken defaultOneTimeToken = new DefaultOneTimeToken(oneTimeTokenEntity.getId(), oneTimeTokenEntity.getContractNumber(), oneTimeTokenEntity.getExpiresAt());
        repository.deleteById(defaultOneTimeToken.getTokenValue());

        if (isExpired(defaultOneTimeToken)) {
            return null;
        }

        return defaultOneTimeToken;
    }

    private boolean isExpired(OneTimeToken ott) {
		return this.clock.instant().isAfter(ott.getExpiresAt());
	}

    @Transactional
    protected ThreadPoolTaskScheduler createTaskScheduler(String cleanupCron) {
        if (cleanupCron == null) {
            return null;
        }

        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setThreadNamePrefix("bcb-customers-otts-");
        taskScheduler.initialize();
        taskScheduler.schedule(this::cleanupExpiredTokens, new CronTrigger(cleanupCron));

        return taskScheduler;
    }

    @Transactional
    public void cleanupExpiredTokens() {
        int deletedCount = repository.deleteAllByExpiresAtBefore(Instant.now()).size();

        System.out.println("Tokens eliminados: " + deletedCount);
    }

    @Override
    public void destroy() throws Exception {
        if (this.taskScheduler != null) {
            this.taskScheduler.shutdown();
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        //this.taskScheduler.afterPropertiesSet();
    }

    private void setClock(Clock clock) {
        this.clock = clock;
    }

}
