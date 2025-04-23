package com.codeit.duckhu.domain.user.repository.poweruser;

import com.codeit.duckhu.domain.user.entity.PowerUser;
import com.codeit.duckhu.global.type.PeriodType;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PowerUserRepository
    extends JpaRepository<PowerUser, UUID>, PowerUserRepositoryCustom {

  int countByPeriod(PeriodType period);
}
