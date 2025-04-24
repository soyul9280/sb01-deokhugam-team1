package com.codeit.duckhu.domain.user.service;

import com.codeit.duckhu.global.type.PeriodType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PowerUserBatchScheduler {
    private final UserService userService;

    @Scheduled(cron = "0 0 12 * * *")
    public void schedule() {
        executePopularBookBatch(PeriodType.DAILY, "[일간 배치 작업]");
        executePopularBookBatch(PeriodType.WEEKLY, "[주간 배치 작업]");
        executePopularBookBatch(PeriodType.MONTHLY, "[월간 배치 작업]");
        executePopularBookBatch(PeriodType.ALL_TIME, "[역대 배치 작업]");

    }
    private void executePopularBookBatch(PeriodType period, String logPrefix) {
        try {
            log.info("{} {} 파워 유저를 갱신합니다.", logPrefix, period);
            userService.savePowerUser(period);
        } catch (Exception e) {
            log.error("{} {} 파워 유저 갱신 중 오류 발생 {}", logPrefix, period, e.getMessage());
        }
    }
}
