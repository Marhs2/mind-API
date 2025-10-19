# 📖 온라인 상담 설문 시스템 API 명세서 (v3)

**Base URL**:
```
http://localhost:8080
```

**인증 방식**: 모든 인증 필요 API는 HTTP Header에 `Authorization: Bearer <JWT>` 토큰을 포함해야 함

**공통 응답 형식**:
```json
{
  "success": true,
  "message": "string",
  "data": "object | array | null",
  "errorCode": "string (optional)"
}
```

---

## 🔐 인증 및 사용자 관리 (Auth & User)

### 📌 이메일 회원가입

- **Method**: `POST`
- **Endpoint**: `/auth/register`
- **권한**: Public

**Request**

```json
{
  "email": "string",
  "password": "string",
  "name": "string"
}

```

**Response (200 OK)**

```json
{
  "success": true,
  "message": "회원가입이 성공적으로 완료되었습니다.",
  "data": {
    "id": 1,
    "name": "string",
    "email": "string",
    "role": "USER",
    "enabled": true,
    "createdDate": "timestamp"
  }
}

```

---

### 📌 이메일 로그인

- **Method**: `POST`
- **Endpoint**: `/auth/login`
- **권한**: Public

**Request**

```json
{
  "email": "string",
  "password": "string"
}

```

**Response (200 OK)**

```json
{
  "success": true,
  "message": "로그인이 성공적으로 완료되었습니다.",
  "data": {
    "accessToken": "string (JWT)",
    "user": {
      "id": 1,
      "name": "string",
      "email": "string",
      "role": "USER" | "ADMIN"
    }
  }
}

```

---

### 📌 전체 사용자 목록 조회

- **Method**: `GET`
- **Endpoint**: `/admin/users`
- **권한**: Admin

**Query Params**

- search (optional) - 사용자 검색

**Response (200 OK)**

```json
{
  "success": true,
  "message": "사용자 목록을 성공적으로 조회했습니다.",
  "data": [
    {
      "id": 1,
      "name": "string",
      "email": "string",
      "role": "USER" | "ADMIN",
      "enabled": true,
      "createdDate": "timestamp"
    }
  ]
}

```

---

### 📌 특정 사용자 정보 조회

- **Method**: `GET`
- **Endpoint**: `/admin/users/{userId}`
- **권한**: Admin

**Response (200 OK)**

```json
{
  "success": true,
  "message": "사용자 정보를 성공적으로 조회했습니다.",
  "data": {
    "id": 1,
    "name": "string",
    "email": "string",
    "role": "USER" | "ADMIN",
    "enabled": true,
    "createdDate": "timestamp"
  }
}

```

---

### 📌 내 설문 목록 조회

- **Method**: `GET`
- **Endpoint**: `/surveys/my-surveys`
- **권한**: User

**Response (200 OK)**

```json
{
  "success": true,
  "message": "내 설문 목록을 성공적으로 조회했습니다.",
  "data": [
    {
      "id": 1,
      "surveyType": "BEFORE" | "AFTER",
      "depression": 7,
      "anxiety": 8,
      "joy": 2,
      "anger": 6,
      "fatigue": 9,
      "loneliness": 8,
      "stability": 3,
      "selfSatisfaction": 3,
      "comments": "string",
      "submissionDate": "timestamp",
      "userId": 1
    }
  ]
}

```

---

## ✍️ 설문 데이터 관리 (Survey)

### 📌 상담 설문 제출

- **Method**: `POST`
- **Endpoint**: `/surveys`
- **권한**: User

**Request**

```json
{
  "surveyType": "Before 또는 After",
   "depression": 7,
  "anxiety": 8,
  "joy": 2,
  "anger": 6,
  "fatigue": 9,
  "loneliness": 8,
  "stability": 3,
  "selfSatisfaction": 3,
  "comments": "string (optional)"
}

```

**Response (201 Created)**

```json
{
  "success": true,
  "message": "설문 제출이 완료되었습니다.",
  "data": {
    "message": "설문이 성공적으로 제출되었습니다.",
    "surveyId": "string"
  }
}

```

---

### 📌 내 상담 이력 조회

- **Method**: `GET`

```jsx
/consultations/my-history
```

- **권한**: User

**Response (200 OK)**

```json
{
  "success": true,
  "message": "상담 이력을 성공적으로 조회했습니다.",
  "data": [
    {
      "consultationId": "string",
      "status": "COMPLETED" | "PENDING" | "IN_PROGRESS",
      "createdDate": "timestamp",
      "hasBeforeSurvey": true,
      "hasAfterSurvey": false
    }
  ]
}

```

---

### 📌 특정 상담 상세 조회

- **Method**: `GET`
- **Endpoint**: `/consultations/{consultationId}`
- **권한**: User

**Response (200 OK)**

```json
{
  "success": true,
  "message": "상담 상세 정보를 성공적으로 조회했습니다.",
  "data": {
    "consultationId": 1,
    "consultationDate": "timestamp",
    "status": "COMPLETED" | "PENDING" | "IN_PROGRESS",
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
      "comments": "string",
      "submissionDate": "timestamp"
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
      "comments": "string",
      "submissionDate": "timestamp"
    }
  }
}

```

---

## 📊 통계 및 분석 (Statistics)

### 📌 내 감정 변화 추이

- **Method**: `GET`
- **Endpoint**: `/stats/me/trends`
- **권한**: User

**Response (200 OK)**

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

### 📌 전체 사용자 트렌드 분석

- **Method**: `GET`
- **Endpoint**: `/admin/statistics/summary`
- **권한**: Admin

**Query Params**

- emotions (optional, comma-separated string, e.g., "depression,anxiety")
- filterBy (optional, e.g., "role")
- filterValue (optional, e.g., "USER")

**Response (200 OK)**

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
      },
      {
        "label": "기쁨",
        "data": [0, 0, 0, 2]
      },
      {
        "label": "분노",
        "data": [0, 0, 0, 6]
      },
      {
        "label": "피로감",
        "data": [0, 0, 0, 9]
      },
      {
        "label": "외로움",
        "data": [0, 0, 0, 8]
      },
      {
        "label": "안정감",
        "data": [0, 0, 0, 3]
      },
      {
        "label": "자기만족도",
        "data": [0, 0, 0, 3]
      }
    ]
  }
}
```



---

## ⚙️ 관리자 부가 기능 (Admin)

### 📌 설문 요청 이메일 발송

- **Method**: `POST`
- **Endpoint**: `/admin/users/{userId}/send-survey-email`
- **권한**: Admin

**Request**

```json
{
  "surveyType": "Before" | "After"
}

```

**Response (200 OK)**

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

### 📌 사용자 정보 수정

- **Method**: `PATCH`
- **Endpoint**: `/admin/users/{userId}`
- **권한**: Admin

**Request**

```json
{
  "name": "string",
  "role": "USER" | "ADMIN"
}
```

**Response (200 OK)**

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

### 📌 사용자 활성화 상태 수정

- **Method**: `PATCH`
- **Endpoint**: `/admin/users/{userId}/status`
- **권한**: Admin

**Request**

```json
{
  "enabled": true | false
}
```

**Response (200 OK)**

```json
{
  "success": true,
  "message": "사용자 상태가 성공적으로 변경되었습니다.",
  "data": {
    "message": "사용자 계정이 성공적으로 활성화되었습니다." // 또는 "비활성화되었습니다."
  }
}
```

---

### 📌 특정 사용자의 모든 설문 조회

- **Method**: `GET`
- **Endpoint**: `/admin/users/{userId}/surveys`
- **권한**: Admin

**Response (200 OK)**

```json
{
  "success": true,
  "message": "사용자 설문 목록을 성공적으로 조회했습니다.",
  "data": [
    {
      "id": 1,
      "surveyType": "BEFORE" | "AFTER",
      "depression": 7,
      "anxiety": 8,
      "joy": 2,
      "anger": 6,
      "fatigue": 9,
      "loneliness": 8,
      "stability": 3,
      "selfSatisfaction": 3,
      "comments": "string",
      "submissionDate": "timestamp",
      "userId": 1,
      "userName": "string"
    }
  ]
}
```

---

### 📌 선택적 통계 계산

- **Method**: `POST`
- **Endpoint**: `/admin/statistics/selective-average`
- **권한**: Admin

**Request**

```json
{
  "emotionKeys": ["string"],
  "period": {
    "start": "YYYY-MM-DD",
    "end": "YYYY-MM-DD"
  }
}
```

**Response (200 OK)**

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

### 📌 커스텀 설문 목록 조회

- **Method**: `GET`
- **Endpoint**: `/admin/custom-surveys`
- **권한**: Admin

**Response (200 OK)**

```json
{
  "success": true,
  "message": "커스텀 설문 목록을 성공적으로 조회했습니다.",
  "data": [
    {
      "id": 1,
      "title": "string",
      "createdBy": "string",
      "creationDate": "timestamp",
      "questions": [
        {
          "text": "string",
          "scoreRange": [1, 10]
        }
      ]
    }
  ]
}
```

---

### 📌 커스텀 설문 생성

- **Method**: `POST`
- **Endpoint**: `/admin/custom-surveys`
- **권한**: Admin

**Request**

```json
{
  "title": "string",
  "questions": [
    {
      "text": "string",
      "scoreRange": ["integer", "integer"]
    }
  ]
}
```

**Response (201 Created)**

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

### 📌 커스텀 설문 수정

- **Method**: `PUT`
- **Endpoint**: `/admin/custom-surveys/{surveyId}`
- **권한**: Admin

**Request**

```json
{
  "title": "string",
  "questions": [
    {
      "text": "string",
      "scoreRange": ["integer", "integer"]
    }
  ]
}
```

**Response (200 OK)**

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

### 📌 커스텀 설문 삭제

- **Method**: `DELETE`
- **Endpoint**: `/admin/custom-surveys/{surveyId}`
- **권한**: Admin

**Response (200 OK)**

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

### 📌 감정 상관관계 매트릭스 조회

- **Method**: `GET`
- **Endpoint**: `/admin/statistics/correlation`
- **권한**: Admin

**Response (200 OK)**

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

## 👤 사용자 정보 (User Profile)

### 📌 내 정보 조회

- **Method**: `GET`
- **Endpoint**: `/users/me`
- **권한**: User

**Response (200 OK)**

```json
{
  "success": true,
  "message": "사용자 정보를 성공적으로 조회했습니다.",
  "data": {
    "id": 1,
    "name": "string",
    "email": "string",
    "role": "USER" | "ADMIN",
    "enabled": true
  }
}
```

---

### 📌 내 통계 조회

- **Method**: `GET`
- **Endpoint**: `/users/me/statistics`
- **권한**: User

**Response (200 OK)**

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
      // ... 총 8개 감정 데이터 ...
    ]
  }
}
```

---

### 📌 상담별 내 통계 조회

- **Method**: `GET`
- **Endpoint**: `/users/me/statistics/by-consultation`
- **권한**: User

**Response (200 OK)**

```json
{
  "success": true,
  "message": "상담별 감정 변화 통계를 성공적으로 조회했습니다.",
  "data": [
    {
      "consultationId": 1,
      "consultationDate": "timestamp",
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
        // ... 총 8개 감정 데이터 ...
      ]
    }
  ]
}
```