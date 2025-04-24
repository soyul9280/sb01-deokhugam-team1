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

    @Scheduled(cron = "0 0 0 * * *")
    public void dailySchedule() {
        log.info("PowerUser DAILY 배치 시작");
        userService.savePowerUser(PeriodType.DAILY);
        log.info("PowerUser DAILY 배치 완료");
    }

    @Scheduled(cron = "0 0 0 * * 0")
    public void weeklySchedule() {
        log.info("PowerUser Weekly 배치 시작");
        userService.savePowerUser(PeriodType.WEEKLY);
        log.info("PowerUser Weekly 배치 완료");
    }

    @Scheduled(cron = "0 0 0 1 * *")
    public void monthlySchedule() {
        log.info("PowerUser Monthly 배치 시작");
        userService.savePowerUser(PeriodType.MONTHLY);
        log.info("PowerUser Monthly 배치 완료");
    }
}
