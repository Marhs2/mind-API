# 📖 온라인 상담 설문 시스템 API 명세서

> **Base URL**: `http://localhost:8080`  
> **인증 방식**: `Authorization: Bearer <JWT>`  
> **버전**: v3.0

---

## 📋 목차

| 섹션 | 설명 | 엔드포인트 개수 |
|------|------|----------------|
| 🔐 인증 관리 | 회원가입, 로그인 | 2개 |
| 👥 사용자 관리 | 사용자 조회/수정/상태변경 | 3개 |
| 📝 설문 관리 | 설문 제출 및 조회 | 3개 |
| 📊 통계 분석 | 감정 변화 분석 | 2개 |
| ⚙️ 관리자 기능 | 이메일, 커스텀 설문, 고급 통계 | 10개 |
| 👤 개인 프로필 | 내 정보 및 개인 통계 | 3개 |

---

## 📝 공통 응답 형식

모든 API 응답은 다음과 같은 구조를 따릅니다:

```json
{
  "success": true,
  "message": "작업이 성공적으로 완료되었습니다.",
  "data": {
    // 실제 데이터
  }
}
```

### 응답 필드 설명

| 필드 | 타입 | 설명 |
|------|------|------|
| `success` | boolean | 요청 성공 여부 |
| `message` | string | 서버 메시지 |
| `data` | object/array/null | 실제 응답 데이터 |

---

## 🔐 인증 관리

### POST /auth/register
**회원가입**

#### 요청 정보
| 구분 | 내용 |
|------|------|
| **Method** | POST |
| **Endpoint** | `/auth/register` |
| **권한** | Public |
| **Content-Type** | `application/json` |

#### Request Body
```json
{
  "email": "user@example.com",
  "password": "password123",
  "name": "홍길동"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "회원가입이 성공적으로 완료되었습니다.",
  "data": {
    "id": 1,
    "name": "홍길동",
    "email": "user@example.com",
    "role": "USER",
    "enabled": true,
    "createdDate": "2024-01-01T00:00:00Z"
  }
}
```

---

### POST /auth/login
**로그인**

#### Request Body
```json
{
  "email": "user@example.com",
  "password": "password123"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "로그인이 성공적으로 완료되었습니다.",
  "data": {
    "accessToken": "eyJhbGciOiJIUzI1NiJ9...",
    "user": {
      "id": 1,
      "name": "홍길동",
      "email": "user@example.com",
      "role": "USER"
    }
  }
}
```

---

## 👥 사용자 관리 (Admin Only)

### GET /admin/users
**전체 사용자 목록 조회**

#### Query Parameters
| 매개변수 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `search` | string | ❌ | 사용자 검색 키워드 |

#### Response (200 OK)
```json
{
  "success": true,
  "message": "사용자 목록을 성공적으로 조회했습니다.",
  "data": [
    {
      "id": 1,
      "name": "홍길동",
      "email": "user@example.com",
      "role": "USER",
      "enabled": true,
      "createdDate": "2024-01-01T00:00:00Z"
    }
  ]
}
```

---

### GET /admin/users/{userId}
**특정 사용자 정보 조회**

#### Path Parameters
| 매개변수 | 타입 | 설명 |
|----------|------|------|
| `userId` | integer | 사용자 ID |

#### Response (200 OK)
```json
{
  "success": true,
  "message": "사용자 정보를 성공적으로 조회했습니다.",
  "data": {
    "id": 1,
    "name": "홍길동",
    "email": "user@example.com",
    "role": "USER",
    "enabled": true,
    "createdDate": "2024-01-01T00:00:00Z"
  }
}
```

---

### PATCH /admin/users/{userId}
**사용자 정보 수정**

#### Request Body
```json
{
  "name": "홍길동 (수정됨)",
  "role": "ADMIN"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "사용자 정보가 성공적으로 수정되었습니다.",
  "data": {
    "message": "사용자 정보가 업데이트되었습니다."
  }
}
```

---

### PATCH /admin/users/{userId}/status
**사용자 활성화 상태 수정**

#### Request Body
```json
{
  "enabled": false
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "사용자 상태가 성공적으로 변경되었습니다.",
  "data": {
    "message": "사용자 계정이 성공적으로 비활성화되었습니다."
  }
}
```

---

## 📝 설문 관리

### POST /surveys
**상담 설문 제출**

#### Request Body
``` json
{
  "surveyType": "BEFORE",
  "depression": 7,
  "anxiety": 8,
  "joy": 2,
  "anger": 6,
  "fatigue": 9,
  "loneliness": 8,
  "stability": 3,
  "selfSatisfaction": 3,
  "comments": "설문 완료했습니다"
}
```

#### 감정 점수 가이드
| 감정 | 점수 범위 | 설명 |
|------|-----------|------|
| 우울감 | 1-10 | 1=매우 낮음, 10=매우 높음 |
| 불안감 | 1-10 | 1=매우 낮음, 10=매우 높음 |
| 기쁨 | 1-10 | 1=매우 낮음, 10=매우 높음 |
| 분노 | 1-10 | 1=매우 낮음, 10=매우 높음 |
| 피로감 | 1-10 | 1=매우 낮음, 10=매우 높음 |
| 외로움 | 1-10 | 1=매우 낮음, 10=매우 높음 |
| 안정감 | 1-10 | 1=매우 낮음, 10=매우 높음 |
| 자기만족도 | 1-10 | 1=매우 낮음, 10=매우 높음 |

#### Response (201 Created)
```json
{
  "success": true,
  "message": "설문 제출이 완료되었습니다.",
  "data": {
    "message": "설문이 성공적으로 제출되었습니다.",
    "surveyId": "1"
  }
}
```

---

### GET /surveys/my-surveys
**내 설문 목록 조회**

#### Response (200 OK)
```json
{
  "success": true,
  "message": "내 설문 목록을 성공적으로 조회했습니다.",
  "data": [
    {
      "id": 1,
      "surveyType": "BEFORE",
      "depression": 7,
      "anxiety": 8,
      "joy": 2,
      "anger": 6,
      "fatigue": 9,
      "loneliness": 8,
      "stability": 3,
      "selfSatisfaction": 3,
      "comments": "설문 완료했습니다",
      "submissionDate": "2024-01-01T00:00:00Z",
      "userId": 1
    }
  ]
}
```

---

### GET /consultations/my-history
**내 상담 이력 조회**

#### Response (200 OK)
```json
{
  "success": true,
  "message": "상담 이력을 성공적으로 조회했습니다.",
  "data": [
    {
      "consultationId": "1",
      "status": "COMPLETED",
      "createdDate": "2024-01-01T00:00:00Z",
      "hasBeforeSurvey": true,
      "hasAfterSurvey": false
    }
  ]
}
```

---

### GET /consultations/{consultationId}
**특정 상담 상세 조회**

#### Response (200 OK)
```json
{
  "success": true,
  "message": "상담 상세 정보를 성공적으로 조회했습니다.",
  "data": {
    "consultationId": 1,
    "consultationDate": "2024-01-01T00:00:00Z",
    "status": "COMPLETED",
    "beforeSurvey": {
      "surveyType": "BEFORE",
      "depression": 7,
      "anxiety": 8,
      "joy": 2,
      "anger": 6,
      "fatigue": 9,
      "loneliness": 8,
      "stability": 3,
      "selfSatisfaction": 3,
      "comments": "상담 전 상태",
      "submissionDate": "2024-01-01T00:00:00Z"
    },
    "afterSurvey": {
      "surveyType": "AFTER",
      "depression": 4,
      "anxiety": 5,
      "joy": 7,
      "anger": 2,
      "fatigue": 3,
      "loneliness": 2,
      "stability": 8,
      "selfSatisfaction": 8,
      "comments": "상담 후 개선됨",
      "submissionDate": "2024-01-02T00:00:00Z"
    }
  }
}
```

---

## 📊 통계 분석

### GET /stats/me/trends
**내 감정 변화 추이**

#### Response (200 OK)
```json
{
  "success": true,
  "message": "나의 감정 변화 추이를 성공적으로 조회했습니다.",
  "data": {
    "labels": ["1회차", "2회차"],
    "datasets": [
      {
        "label": "우울감",
        "data": [7, 4]
      },
      {
        "label": "불안감", 
        "data": [8, 6]
      },
      {
        "label": "기쁨",
        "data": [2, 7]
      },
      {
        "label": "분노",
        "data": [6, 2]
      },
      {
        "label": "피로감",
        "data": [9, 3]
      },
      {
        "label": "외로움",
        "data": [8, 2]
      },
      {
        "label": "안정감",
        "data": [3, 8]
      },
      {
        "label": "자기만족도",
        "data": [3, 8]
      }
    ]
  }
}
```

---

### GET /admin/statistics/summary
**전체 사용자 트렌드 분석**

#### Query Parameters
| 매개변수 | 타입 | 필수 | 설명 |
|----------|------|------|------|
| `emotions` | string | ❌ | 감정 필터 (예: "depression,anxiety") |
| `filterBy` | string | ❌ | 필터 기준 (예: "role") |
| `filterValue` | string | ❌ | 필터 값 (예: "USER") |

#### Response (200 OK)
```json
{
  "success": true,
  "message": "통계 요약을 성공적으로 조회했습니다.",
  "data": {
    "labels": ["09/08", "09/15", "09/22", "09/29"],
    "datasets": [
      {
        "label": "우울감",
        "data": [0, 0, 0, 7]
      },
      {
        "label": "불안감",
        "data": [0, 0, 0, 8]
      }
      // ... 다른 감정 데이터들
    ]
  }
}
```

---

## ⚙️ 관리자 기능

### POST /admin/users/{userId}/send-survey-email
**설문 요청 이메일 발송**

#### Request Body
```json
{
  "surveyType": "AFTER"
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "이메일이 성공적으로 발송되었습니다.",
  "data": {
    "message": "설문 요청 이메일이 성공적으로 발송되었습니다."
  }
}
```

---

### GET /admin/users/{userId}/surveys
**특정 사용자의 모든 설문 조회**

#### Response (200 OK)
```json
哪里有{
  "success": true,
  "message": "사용자 설문 목록을 성공적으로 조회했습니다.",
  "data": [
    {
      "id": 1,
      "surveyType": "BEFORE",
      "depression": 7,
      "anxiety": 8,
      "joy": 2,
      "anger": 6,
      "fatigue": 9,
      "loneliness": 8,
      "stability": 3,
      "selfSatisfaction": 3,
      "comments": "설문 완료했습니다",
      "submissionDate": "2024-01-01T00:00:00Z",
      "userId": 1,
      "userName": "홍길동"
    }
  ]
}
```

---

### POST /admin/statistics/selective-average
**선택적 통계 계산**

#### Request Body
```json
{
  "emotionKeys": ["depression", "anxiety"],
  "period": {
    "start": "2024-01-01",
    "end": "2024-01-31"
  }
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "선택적 통계 계산을 성공적으로 완료했습니다.",
  "data": {
    "averageBefore": 7.5,
    "averageAfter": 4.2,
    "improvement": "43.9% 향상"
  }
}
```

---

### GET /admin/custom-surveys
**커스텀 설문 목록 조회**

#### Response (200 OK)
```json
{
  "success": true,
  "message": "커스텀 설문 목록을 성공적으로 조회했습니다.",
  "data": [
    {
      "id": 1,
      "title": "스트레스 관리 설문",
      "createdBy": "관리자",
      "creationDate": "2024-01-01T00:00:00Z",
      "questions": [
        {
          "text": "전반적인 스트레스 수준은 어느 정도입니까?",
          "scoreRange": [1, 10]
        }
      ]
    }
  ]
}
```

---

### POST /admin/custom-surveys
**커스텀 설문 생성**

#### Request Body
```json
{
  "title": "스트레스 관리 설문",
  "questions": [
    {
      "text": "전반적인 스트레스 수준이 어느 정도입니까?",
      "scoreRange": [1, 10]
    },
    {
      "text": "수면의 질은 어떠합니까?",
      "scoreRange": [1, 10]
    }
  ]
}
```

#### Response (201 Created)
```json
{
  "success": true,
  "message": "커스텀 설문이 성공적으로 생성되었습니다.",
  "data": {
    "message": "설문이 성공적으로 생성되었습니다."
  }
}
```

---

### PUT /admin/custom-surveys/{surveyId}
**커스텀 설문 수정**

#### Request Body (POST와 동일)
```json
{
  "title": "수정된 스트레스 관리 설문",
  "questions": [
    {
      "text": "수정된 질문입니다.",
      "scoreRange": [1, 10]
    }
  ]
}
```

#### Response (200 OK)
```json
{
  "success": true,
  "message": "커스텀 설문이 성공적으로 수정되었습니다.",
  "data": {
    "message": "설문이 성공적으로 수정되었습니다."
  }
}
```

---

### DELETE /admin/custom-surveys/{surveyId}
**커스텀 설문 삭제**

#### Response (200 OK)
```json
{
  "success": true,
  "message": "커스텀 설문이 성공적으로 삭제되었습니다.",
  "data": {
    "message": "설문이 성공적으로 삭제되었습니다."
  }
}
```

---

### GET /admin/statistics/correlation
**감정 상관관계 매트릭스 조회**

#### Response (200 OK)
```json
{
  "success": true,
  "message": "감정 상관관계 매트릭스를 성공적으로 조회했습니다.",
  "data": {
    "labels": ["우울감", "불안감", "기쁨", "분노", "피로감", "외로움", "안정감", "자기만족도"],
    "matrix": [
      [1.00, 0.78, -0.45, 0.67, 0.89, 0.82, -0.72, -0.68],
      [0.78, 1.00, -0.52, 0.71, 0.76, 0.85, -0.69, -0.74],
      [-0.45, -0.52, 1.00, -0.41, -0.38, -0.49, 0.58, 0.67],
      [0.67, 0.71, -0.41, 1.00, 0.68, 0.73, -0.61, -0.59],
      [0.89, 0.76, -0.38, 0.68, 1.00, 0.84, -0.67, -0.63],
      [0.82, 0.85, -0.49, 0.73, 0.84, 1.00, -0.71, -0.68],
      [-0.72, -0.69, 0.58, -0.61, -0.67, -0.71, 1.00, 0.75],
      [-0.68, -0.74, 0.67, -0.59, -0.63, -0.68, 0.75, 1.00]
    ]
  }
}
```

---

## 👤 개인 프로필

### GET /users/me
**내 정보 조회**

#### Response (200 OK)
```json
{
  "success": true,
  "message": "사용자 정보를 성공적으로 조회했습니다.",
  "data": {
    "id": 1,
    "name": "홍길동",
    "email": "user@example.com",
    "role": "USER",
    "enabled": true
  }
}
```

---

### GET /users/me/statistics
**내 통계 조회**

#### Response (200 OK)
```json
{
  "success": true,
  "message": "통계 데이터를 성공적으로 조회했습니다.",
  "data": {
    "labels": ["1회차", "2회차"],
    "datasets": [
      {
        "label": "우울감",
        "data": [7, 4]
      },
      {
        "label": "불안감",
        "data": [8, 6]
      }
      // ... 총 8개 감정 데이터
    ]
  }
}
```

---

### GET /users/me/statistics/by-consultation
**상담별 내 통계 조회**

#### Response (200 OK)
```json
{
  "success": true,
  "message": "상담별 감정 변화 통계를 성공적으로 조회했습니다.",
  "data": [
    {
      "consultationId": 1,
      "consultationDate": "2024-01-01T00:00:00Z",
      "emotionChanges": [
        {
          "emotionName": "우울감",
          "beforeScore": 7,
          "afterScore": 4,
          "change": -3
        },
        {
          "emotionName": "불안감",
          "beforeScore": 8,
          "afterScore": 6,
          "change": -2
        }
        // ... 총 8개 감정 데이터
      ]
    }
  ]
}
```

---

## 📋 오류 코드 및 상태

### HTTP 상태 코드
| 코드 | 설명 | 사용 예시 |
|------|------|-----------|
| 200 | OK | 성공적인 요청 |
| 201 | Created | 자원 생성 성공 |
| 400 | Bad Request | 잘못된 요청 |
| 401 | Unauthorized | 인증 실패 |
| 403 | Forbidden | 권한 부족 |
| 404 | Not Found | 자원을 찾을 수 없음 |
| 500 | Internal Server Error | 서버 내부 오류 |

### 응답 오류 형식
```json
{
  "success": false,
  "message": "오류가 발생했습니다.",
  "data": null,
  "errorCode": "VALIDATION_ERROR"
}
```

---

## 🔒 보안 및 권한

### 권한 분류
| 권한 | 설명 | 엔드포인트 예시 |
|------|------|----------------|
| Public | 인증 불필요 | `/auth/register`, `/auth/login` |
| User | 일반 사용자 | `/surveys/*`, `/users/me/*` |
| Admin | 관리자만 | `/admin/*` |

### JWT 토큰 사용법
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9.eyJl...
```

---

## 📊 데이터 모델

### 감정 데이터 구조
```json
{
  "depression": 1-10,    // 우울감
  "anxiety": 1-10,        // 불안감
  "joy": 1-10,           // 기쁨
  "anger": 1-10,         // 분노
  "fatigue": 1-10,       // 피로감
  "loneliness": 1-10,    // 외로움
  "stability": 1-10,     // 안정감
  "selfSatisfaction": 1-10 // 자기만족도
}
```

### 설문 타입
| 값 | 설명 |
|----|------|
| BEFORE | 상담 전 설문 |
| AFTER | 상담 후 설문 |

### 사용자 역할
| 값 | 설명 |
|----|------|
| USER | 일반 사용자 |
| ADMIN | 관리자 |

---

## 🚀 테스트 가이드

### Postman Collection 예시
1. **Environment 설정**:
   - `baseUrl`: `http://localhost:8080`
   - `token`: JWT 토큰 저장

2. **테스트 시나리오**:
   1. 회원가입 → 로그인 → 토큰 저장
   2. 설문 제출 → 설문 조회
   3. 상담 이력 확인 → 상담 상세 보기
   4. 개인 통계 조회

### 주요 테스트 케이스
- ✅ 정상 회원가입/로그인
- ✅ 설문 제출 및 조회
- ✅ 권한별 기능 접근 제한
- ✅ 잘못된 데이터 유효성 검사
- ✅ JWT 토큰 만료 처리

---

*Last Updated: 2024-01-01*
