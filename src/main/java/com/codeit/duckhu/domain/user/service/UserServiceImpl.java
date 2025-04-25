package com.codeit.duckhu.domain.user.service;

import com.codeit.duckhu.domain.user.dto.CursorPageResponsePowerUserDto;
import com.codeit.duckhu.domain.user.dto.PowerUserDto;
import com.codeit.duckhu.domain.user.dto.PowerUserStatsDto;
import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.dto.UserLoginRequest;
import com.codeit.duckhu.domain.user.dto.UserRegisterRequest;
import com.codeit.duckhu.domain.user.dto.UserUpdateRequest;
import com.codeit.duckhu.domain.user.entity.PowerUser;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.exception.EmailDuplicateException;
import com.codeit.duckhu.domain.user.exception.UserException;
import com.codeit.duckhu.domain.user.mapper.PowerUserMapper;
import com.codeit.duckhu.domain.user.mapper.UserMapper;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import com.codeit.duckhu.domain.user.repository.poweruser.PowerUserRepository;
import com.codeit.duckhu.global.exception.ErrorCode;
import com.codeit.duckhu.global.type.Direction;
import com.codeit.duckhu.global.type.PeriodType;
import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
  private final UserRepository userRepository;
  private final PowerUserRepository powerUserRepository;
  private final UserMapper userMapper;
  private final PowerUserMapper powerUserMapper;

  @Override
  public UserDto create(UserRegisterRequest request) {
    if (userRepository.existsByEmail(request.getEmail())) {
      throw new EmailDuplicateException(request.getEmail());
    }

    User user = new User(request.getEmail(), request.getNickname(), request.getPassword());

    User savedUser = userRepository.save(user);

    return userMapper.toDto(savedUser);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto login(UserLoginRequest userLoginRequest) {
    String email = userLoginRequest.getEmail();
    String password = userLoginRequest.getPassword();
    User user =
        userRepository
            .findByEmail(email)
            .orElseThrow(() -> new UserException(ErrorCode.LOGIN_INPUT_INVALID));
    if (!user.getPassword().equals(password)) {
      throw new UserException(ErrorCode.LOGIN_INPUT_INVALID);
    }
    return userMapper.toDto(user);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDto findById(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
    if (user.isDeleted()) {
      throw new UserException(ErrorCode.NOT_FOUND_USER);
    }
    return userMapper.toDto(user);
  }

  @Override
  public UserDto update(UUID id, UserUpdateRequest userUpdateRequest) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
    if (user.isDeleted()) {
      throw new UserException(ErrorCode.NOT_FOUND_USER);
    }
    user.update(userUpdateRequest);
    return userMapper.toDto(user);
  }

  public User findByIdEntityReturn(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
    if (user.isDeleted()) {
      throw new UserException(ErrorCode.NOT_FOUND_USER);
    }

    return user;
  }

  @Override
  public void softDelete(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
    user.softDelete();
  }

  @Override
  public void hardDelete(UUID id) {
    User user =
        userRepository
            .findById(id)
            .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));
    userRepository.deleteById(user.getId());
  }

  @Override
  public void savePowerUser(PeriodType period) {
    Instant now = Instant.now();
    Instant start = period.toStartInstant(now);
    Instant end = now;

    // 계산에 필요한 요소들 갖고오기
    List<PowerUserStatsDto> stats = powerUserRepository.findPowerUserStatsBetween(start, end);

    //유저목록가져오기
    Set<UUID> userIds = stats.stream().map(PowerUserStatsDto::userId).collect(Collectors.toSet());
    Map<UUID, User> userMap = userRepository.findAllById(userIds).stream()
            .collect(Collectors.toMap(User::getId, Function.identity()));

    //활동점수 계산
    List<PowerUser> powerUsers = stats.stream()
            .map(dto -> {
              Double score = dto.reviewScoreSum() * 0.5
                      + dto.likedCount() * 0.2
                      + dto.commentCount() * 0.3;
              User user = Optional.ofNullable(userMap.get(dto.userId()))
                      .orElseThrow(() -> new UserException(ErrorCode.NOT_FOUND_USER));

              return PowerUser.builder()
                      .user(user)
                      .reviewScoreSum(dto.reviewScoreSum())
                      .likeCount(dto.likedCount())
                      .commentCount(dto.commentCount())
                      .score(score)
                      .period(period)
                      .build();
            })
            .sorted(Comparator.comparingDouble(PowerUser::getScore).reversed())
            .toList();

    //순위 부여-동시성제어
    AtomicInteger rankCount = new AtomicInteger(1);
    //순위 설정
    powerUsers.forEach(powerUser -> powerUser.setRank(rankCount.getAndIncrement()));
    //기존 데이터 삭제(for 배치)
    powerUserRepository.deleteByPeriod(period);
    //PowerUser에 저장
    powerUserRepository.saveAll(powerUsers);
  }

  @Override
  @Transactional(readOnly = true)
  public CursorPageResponsePowerUserDto findPowerUsers(
      PeriodType period, Direction direction, String cursor, Instant after, int limit) {

    List<PowerUser> powerUsers = powerUserRepository.searchByPeriodWithCursorPaging(
            period, direction, cursor, after, limit + 1
    );
    boolean hasNext = powerUsers.size() > limit;

    //저장된 PowerUser 커서페이지로 갖고오기
    List<PowerUser> pageContent = hasNext ? powerUsers.subList(0, limit) : powerUsers;
    List<PowerUserDto> list = pageContent.stream()
            .map(powerUserMapper::toDto)
            .toList();

    String nextCursor=null;
    Instant nextAfter=null;
    if(hasNext&&!pageContent.isEmpty()) {
      PowerUser last = pageContent.get(pageContent.size() - 1);
      nextCursor=last.getUser().getId().toString();
      nextAfter = last.getCreatedAt();
    }

    return CursorPageResponsePowerUserDto.builder()
            .content(list)
            .nextCursor(nextCursor)
            .nextAfter(nextAfter)
            .size(list.size())
            .totalElements(list.size())
            .hasNext(hasNext)
            .build();
  }



}
