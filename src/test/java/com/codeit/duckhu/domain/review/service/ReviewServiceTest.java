package com.codeit.duckhu.domain.review.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.times;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.atLeast;

import com.codeit.duckhu.domain.book.entity.Book;
import com.codeit.duckhu.domain.book.repository.BookRepository;
import com.codeit.duckhu.domain.review.dto.CursorPageResponseReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewCreateRequest;
import com.codeit.duckhu.domain.review.dto.ReviewDto;
import com.codeit.duckhu.domain.review.dto.ReviewLikeDto;
import com.codeit.duckhu.domain.review.dto.ReviewSearchRequestDto;
import com.codeit.duckhu.domain.review.dto.ReviewUpdateRequest;
import com.codeit.duckhu.domain.review.entity.Review;
import com.codeit.duckhu.domain.review.exception.ReviewCustomException;
import com.codeit.duckhu.domain.review.exception.ReviewErrorCode;
import com.codeit.duckhu.domain.review.mapper.ReviewMapper;
import com.codeit.duckhu.domain.review.repository.ReviewRepository;
import com.codeit.duckhu.domain.review.service.impl.ReviewServiceImpl;
import com.codeit.duckhu.domain.user.dto.UserDto;
import com.codeit.duckhu.domain.user.entity.User;
import com.codeit.duckhu.domain.user.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

/**
 * 리뷰 서비스 테스트 클래스
 * TDD 방식으로 구현 예정
 */
@ExtendWith(MockitoExtension.class)
class ReviewServiceTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private BookRepository bookRepository;

  @Mock
  private ReviewRepository reviewRepository;
  
  @Mock
  private ReviewMapper reviewMapper;

  @InjectMocks
  private ReviewServiceImpl reviewService;

  private User testUser;
  private Book testBook;
  private Review testReview;
  private ReviewDto testReviewDto;
  private ReviewCreateRequest testCreateRequest;
  private UUID testReviewId;
  private UUID testUserId;
  private UUID testBookId;
  private ReviewUpdateRequest testreviewUpdateRequest;

  @BeforeEach
  void setUp() {
    // 테스트용 ID 생성
    testReviewId = UUID.randomUUID();
    testUserId = UUID.randomUUID();
    testBookId = UUID.randomUUID();

    // 테스트용 객체 생성 - 스텁 설정 없이 mock 객체만 생성
    testUser = Mockito.mock(User.class);
    testBook = Mockito.mock(Book.class);
    testReview = Mockito.mock(Review.class);

    // 테스트용 DTO 생성
    testReviewDto = ReviewDto.builder()
        .id(testReviewId)
        .content("볼만해요")
        .rating(3)
        .commentCount(0)
        .likeCount(0)
        .likedByMe(false)
        .userId(testUserId)
        .bookId(testBookId)
        .userNickname("테스터")
        .bookTitle("테스트 도서")
        .bookThumbnailUrl("http://example.com/test.jpg")
        .createdAt(LocalDateTime.now())
        .build();
        
    // 테스트용 Create 요청 생성
    testCreateRequest = ReviewCreateRequest.builder()
        .userId(testUserId)
        .bookId(testBookId)
        .content("볼만해요")
        .rating(3)
        .build();

    // 테스트용 Update 요청 생성
    testreviewUpdateRequest = ReviewUpdateRequest.builder()
        .content("재밌어요")
        .rating(5)
        .userId(testUserId)
        .bookId(testBookId)
        .build();
  }

  @Test
  @DisplayName("리뷰 생성 성공")
  void createReview_shouldCreateReview() {
    // Given
    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
    when(bookRepository.findById(testBookId)).thenReturn(Optional.of(testBook));
    when(reviewRepository.findByUserIdAndBookId(testUserId, testBookId)).thenReturn(Optional.empty());
    when(reviewMapper.toEntity(any(), any(), any())).thenReturn(testReview);
    when(reviewRepository.save(any())).thenReturn(testReview);
    when(reviewMapper.toDto(any())).thenReturn(testReviewDto);

    // When
    ReviewDto result = reviewService.createReview(testCreateRequest);

    // Then
    assertThat(result).isNotNull();
    assertThat(result.getRating()).isEqualTo(testReviewDto.getRating());
    assertThat(result.getContent()).isEqualTo(testReviewDto.getContent());
  }

  @Test
  @DisplayName("ID로 리뷰 조회 테스트")
  void getReviewById_shouldReturnReview() {
    // Given
    when(reviewRepository.findById(testReviewId)).thenReturn(Optional.of(testReview));
    when(reviewMapper.toDto(testReview)).thenReturn(testReviewDto);
    
    // When
    ReviewDto result = reviewService.getReviewById(testReviewId);
    
    // Then
    assertThat(result).isNotNull();
    assertThat(result.getContent()).isEqualTo(testReviewDto.getContent());
    assertThat(result.getRating()).isEqualTo(testReviewDto.getRating());
  }

  @Test
  @DisplayName("ID로 리뷰 하드 삭제 테스트")
  void hardDeleteReviewById_shouldReturnSuccess() {
    // Given: findById 리턴과 delete 설정
    doReturn(Optional.of(testReview)).when(reviewRepository).findById(testReviewId);
    willDoNothing().given(reviewRepository).delete(testReview);

    // When : 서비스 호출 시 예외가 나지 않아야 하고
    assertDoesNotThrow(() -> reviewService.hardDeleteReviewById(testReviewId));

    // Then: repository.findById + repository.delete 가 호출됐는지 검증, 불필요한 추가 호출이 없는지도 검증
    verify(reviewRepository).findById(testReviewId);
    verify(reviewRepository).delete(testReview);
  }

  @Test
  @DisplayName("ID로 리뷰 소프트 삭제 테스트")
  void softDeleteReviewById_shouldReturnSuccess() {
    // Given
    Review mockReview = Mockito.mock(Review.class);
    doReturn(Optional.of(mockReview)).when(reviewRepository).findById(testReviewId);
    
    // When
    reviewService.softDeleteReviewById(testReviewId);
    
    // Then
    verify(mockReview).softDelete();
  }

  @Test
  @DisplayName("리뷰 업데이트 테스트")
  void updateReview_shouldReturnUpdateReview() {
    // Given
    when(reviewRepository.findById(testReviewId)).thenReturn(Optional.of(testReview));
    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
    when(reviewRepository.save(testReview)).thenReturn(testReview);
    when(reviewMapper.toDto(testReview)).thenReturn(testReviewDto);
    when(testReview.getUser()).thenReturn(testUser);
    when(testUser.getId()).thenReturn(testUserId);
    
    // When
    ReviewDto result = reviewService.updateReview(testReviewId, testreviewUpdateRequest);
    
    // Then
    assertThat(result).isNotNull();
    assertThat(result).isEqualTo(testReviewDto);
  }

  @Test
  @DisplayName("이미 도서에 리뷰가 존재하는 경우 예외가 발생해야 함")
  void createReview_shouldThrowException_whenReviewAlreadyExists() {
    // Given
    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
    when(bookRepository.findById(testBookId)).thenReturn(Optional.of(testBook));
    when(reviewRepository.findByUserIdAndBookId(testUserId, testBookId)).thenReturn(Optional.of(testReview));
    
    // When & Then
    assertThrows(ReviewCustomException.class, () -> reviewService.createReview(testCreateRequest));
    verify(reviewRepository, never()).save(any());
  }
  
  @Test
  @DisplayName("좋아요가 없는 상태에서 좋아요 누르면 likeCount 증가 및 liked=true 반환")
  void likeReview_firstTime_likeCountIncreased() {
    // Given
    when(reviewRepository.findById(testReviewId)).thenReturn(Optional.of(testReview));
    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
    when(testReview.liked(testUserId)).thenReturn(false);
    when(testReview.getId()).thenReturn(testReviewId);
    
    // When
    ReviewLikeDto result = reviewService.likeReview(testReviewId, testUserId);
    
    // Then
    verify(testReview).increaseLikeCount(testUserId);
  }

  @Test
  @DisplayName("좋아요가 된 상태에서 다시 누르면 언라이크 처리, liked=false 반환")
  void likeReview_toggleOff_likeCountDecreased() {
    // Given
    when(reviewRepository.findById(testReviewId)).thenReturn(Optional.of(testReview));
    when(userRepository.findById(testUserId)).thenReturn(Optional.of(testUser));
    when(testReview.liked(testUserId)).thenReturn(true);
    when(testReview.getId()).thenReturn(testReviewId);
    
    // When
    ReviewLikeDto result = reviewService.likeReview(testReviewId, testUserId);
    
    // Then
    verify(testReview).decreaseLikeCount(testUserId);
  }

  @Test
  @DisplayName("존재하지 않는 리뷰 ID로 호출 시 예외 발생")
  void likeReview_nonexistentReview_throwsNotFound() {
    // Given
    when(reviewRepository.findById(testReviewId)).thenReturn(Optional.empty());
    
    // When & Then
    ReviewCustomException ex = assertThrows(ReviewCustomException.class,
        () -> reviewService.likeReview(testReviewId, testUserId));
    assertThat(ex.getErrorCode()).isEqualTo(ReviewErrorCode.REVIEW_NOT_FOUND);
  }

  @Test
  @DisplayName("존재하지 않는 유저 ID로 호출 시 예외 발생")
  void likeReview_nonexistentUser_throwsNotFound() {
    // Given
    when(reviewRepository.findById(testReviewId)).thenReturn(Optional.of(testReview));
    when(userRepository.findById(testUserId)).thenReturn(Optional.empty());
    
    // When & Then
    ReviewCustomException ex = assertThrows(ReviewCustomException.class,
        () -> reviewService.likeReview(testReviewId, testUserId));
    assertThat(ex.getErrorCode()).isEqualTo(ReviewErrorCode.USER_NOT_FOUND);
  }

  @Test
  @DisplayName("리뷰 커서 페이지네이션 테스트")
  void findReviews_success() {
    // Given
    List<Review> reviewList = new ArrayList<>();
    
    // 정확한 파라미터로 stubbing 설정
    when(reviewRepository.findReviewsWithCursor(
            eq(null), eq("createdAt"), eq("DESC"), 
            eq(null), eq(null), eq(null), 
            eq(null), eq(11)
    )).thenReturn(reviewList);
    
    // When
    CursorPageResponseReviewDto result = reviewService.findReviews(new ReviewSearchRequestDto());
    
    // Then
    assertThat(result).isNotNull();
    assertThat(result.isHasNext()).isFalse();
    assertThat(result.getReviews()).isEmpty();
  }
}


