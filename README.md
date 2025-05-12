# 📚 1조 / DeokHuGam 커뮤니티 서비스
<div align ="center">
  
  ![macbook](https://github.com/user-attachments/assets/a872a790-0bc6-4b23-b18c-699eea792e9c)
  
**DeokHuGam [🔗 지금 이용해보러가기](http://www.deokhugam.store/)**
</div>

#  프로젝트 소개

- 책 읽는 즐거움을 공유하고, 지식과 감상을 나누는 책 덕후들의 커뮤니티 서비스
- 프로젝트 기간 : 2025.04.16 ~ 2025. 05. 13

## 🧑‍💻 팀원 구성

|             민기                |                     이민주                     |                  김준우                          |                강소율                       |                      변희재         |
| :--------------------------------------: | :--------------------------------------------: | :----------------------------------------------: | :----------------------------------------------: | :--------------------------------------------: |
|<img width="160px" src="https://github.com/user-attachments/assets/a6b8709d-13ed-45dc-b093-96948db20a91"/>|<img width="160px" src="https://github.com/user-attachments/assets/a7781d01-fea9-4454-97e7-c7c51415f283"/>|<img width="160px" src="https://github.com/user-attachments/assets/438f7157-943b-4dcc-bed0-210247c1e50e"/>|<img width="160px" src="https://github.com/user-attachments/assets/b0c2314f-8c9e-4dab-8caa-a6b4c29bce44">|<img width="160px" src="https://github.com/user-attachments/assets/d4c6dbc7-392a-4dcf-bdb6-3408c8268638"/>|
| [@GiMin0123](https://github.com/GiMin0123) | [@m0276](https://github.com/m0276) |[@normaldeve](https://github.com/normaldeve)| [@soyul9280](https://github.com/soyul9280) |[@Heyaaz](https://github.com/Heyaaz)|
------

## 🔧 기술 스택

<h2 align="center">✨Backend</h2>

<div align="center">
	<img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
    <img src="https://img.shields.io/badge/spring data jpa-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
    <img src="https://img.shields.io/badge/java-000000?style=for-the-badge&logo=openjdk&logoColor=white">
    <img src="https://img.shields.io/badge/gradle-02303A?style=for-the-badge&logo=gradle&logoColor=white">
</div>


<h2 align="center">📦️Database</h2>

<div align="center">
	<img src="https://img.shields.io/badge/PostgresQL-4169E1?style=for-the-badge&logo=postgresql&logoColor=white">
</div>

<h2 align="center">🔨Tools</h2>

<div align="center">
	<img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white">
	<img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white">
    <img src="https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">
    <img src="https://img.shields.io/badge/IntelliJ IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
</div>


------

## 🧪 테스트 및 커버리지

| 항목              | 상태                                                                                             |
|------------------|--------------------------------------------------------------------------------------------------|
| **CI/CD**        | [![CI/CD](https://github.com/sb01-deokhugam-team1/sb01-deokhugam-team1/actions/workflows/cicd.yml/badge.svg)](https://github.com/sb01-deokhugam-team1/sb01-deokhugam-team1/actions/workflows/cicd.yml) |
| **Test Coverage**| [![codecov](https://codecov.io/gh/sb01-deokhugam-team1/sb01-deokhugam-team1/branch/main/graph/badge.svg)](https://codecov.io/gh/sb01-deokhugam-team1/sb01-deokhugam-team1) |

---

## 📝 팀원별 구현 기능 상세

### 민기
알림 관련 도메인

### 이민주
댓글 관련 도메인


### 김준우
도서 관련 도메인
OCR
대시보드 인기 도서

### 강소율
사용자 관련 도메인
세션
대시보드 파워 유저

### 변희재
리뷰 관련 도메인 구현
- 리뷰 정보 CRUD
- 리뷰 목록 조회 (다양한 조건 검색, 정렬 및 커서 페이지네이션)

Spring Batch를 이용한 대시보드 인기 리뷰 구현
- 인기 리뷰 계산 후 정렬
- 인기 리뷰 목록 조회 (기간 별 페이지네이션)

------

## 🗃️ 파일 구조

```
sb01-duckhugam-team1/
├── src/
   └── main/
       ├── java/
       │   └── duckhu/
       │       ├── config/             # 전역 설정 파일
       │       ├── domain/             # 도메인 모듈
       │       │   ├── book/           # 도서 관련 모듈
       │       │   │   ├── controller/ # 도서 관련 컨트롤러
       │       │   │   ├── dto/        # 도서 관련 DTO
       │       │   │   ├── entity/     # 도서 엔티티 클래스
       │       │   │   ├── exception/  # 도서 예외 처리
       │       │   │   ├── mapper/     # 도서 매핑 인터페이스
       │       │   │   ├── naver/      # 네이버 API 연동 모듈
       │       │   │   ├── ocr/        # OCR 관련 모듈
       │       │   │   ├── repository/ # 도서 JPA 레포지토리
       │       │   │   └── service/    # 도서 비즈니스 로직
       │       │   ├── comment/        # 댓글 관련 모듈
       │       │   │   ├── controller/ # 댓글 관련 컨트롤러
       │       │   │   ├── domain/     # 댓글 엔티티 클래스
       │       │   │   ├── dto/        # 댓글 관련 DTO
       │       │   │   ├── exception/  # 댓글 예외 처리
       │       │   │   ├── repository/ # 댓글 JPA 레포지토리
       │       │   │   └── service/    # 댓글 비즈니스 로직
       │       │   ├── notification/   # 알림 관련 모듈
       │       │   │   ├── controller/ # 알림 컨트롤러
       │       │   │   ├── dto/        # 알림 관련 DTO
       │       │   │   ├── entity/     # 알림 엔티티 클래스
       │       │   │   ├── exception/  # 알림 예외 처리
       │       │   │   ├── mapper/     # 알림 매퍼 인터페이스
       │       │   │   ├── repository/ # 알림 JPA 레포지토리
       │       │   │   └── service/    # 알림 비즈니스 로직
       │       │   ├── review/         # 리뷰 관련 모듈
       │       │   │   ├── batch/      # 리뷰 배치 작업
       │       │   │   ├── controller/ # 리뷰 관련 컨트롤러
       │       │   │   ├── dto/        # 리뷰 관련 DTO
       │       │   │   ├── entity/     # 리뷰 엔티티 클래스
       │       │   │   ├── mapper/     # 리뷰 매핑 인터페이스
       │       │   │   ├── repository/ # 리뷰 JPA 레포지토리
       │       │   │   └── service/    # 리뷰 비즈니스 로직
       │       │   └── user/           # 사용자 관련 모듈
       │       │       ├── controller/ # 사용자 컨트롤러
       │       │       ├── dto/        # 사용자 관련 DTO
       │       │       ├── entity/     # 사용자 엔티티 클래스
       │       │       ├── exception/  # 사용자 예외 처리
       │       │       ├── mapper/     # 사용자 매핑 인터페이스
       │       │       ├── repository/ # 사용자 JPA 레포지토리
       │       │       └── service/    # 사용자 비즈니스 로직
       │       │
       │       ├── global/             # 전역 공통 모듈
       │       │   ├── exception/      # 전역 예외 처리
       │       │   ├── logging/        # 로깅 설정
       │       │   ├── response/       # 공통 응답 객체
       │       │   └── type/           # 공통 Enum 클래스
       │       │
       │       ├── storage/            # 파일 저장 관련 모듈
       │       │   ├── S3Service.java
       │       │   └── FileStorageService.java
       │       │
       │       └── UserAuthenticationFilter.java # 사용자 인증 필터
       │
       └── resources/
           └── application.yml       # Spring Boot 애플리케이션 설정 파일
```
------

## ERD
![duckhu](https://github.com/user-attachments/assets/a194d299-9f43-4ce4-96c5-f875705b1393)

------

**회고 문서 [🔗 notion]()**


