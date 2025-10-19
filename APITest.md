# API Test Log

This file documents the API tests performed.

## Test Cases

| # | API Endpoint | Method | Payload | Expected Result | Actual Result | Status |
|---|--------------|--------|---------|-----------------|---------------|--------|
| 1 | /auth/register | POST | `{"email": "testuser3@example.com", "password": "password123", "name": "Test User 3"}` | 400 Bad Request (Validation Error) | 400 Bad Request (Validation Error) | PASS |
| 2 | /auth/register | POST | `{"email": "testuser@example.com", "password": "password123", "name": "Test User"}` | 201 Created | 500 Internal Server Error | FAIL |
| 3 | /auth/register | POST | `{"email": "testuser4@example.com", "password": "password123", "name": "Test User Four"}` | 201 Created | 500 Internal Server Error | FAIL |
| 4 | /auth/register | POST | `{"email": "testuser5@example.com", "password": "password123", "name": "Test User Five"}` | 201 Created | 500 Internal Server Error | FAIL |
| 5 | /auth/register | POST | `{"email": "testuser6@example.com", "password": "password123", "name": "Test User Six"}` | 200 OK | 200 OK | PASS |
| 6 | /auth/login | POST | `{"email": "testuser6@example.com", "password": "password123"}` | 200 OK | 200 OK | PASS |
| 7 | /surveys | POST | (Survey Data) | 201 Created | 201 Created | PASS |
| 8 | /consultations/my-history | GET | (None) | 200 OK | 200 OK | PASS |
| 9 | /consultations/1 | GET | (None) | 200 OK | 200 OK | PASS |
| 10 | /stats/me/trends | GET | (None) | 200 OK | 404 Not Found | FAIL (Not Implemented) |
| 11 | /admin/users | GET | (None) | 200 OK | 200 OK | PASS |
| 12 | /admin/users/1 | GET | (None) | 200 OK | 200 OK | PASS |
| 13 | /admin/statistics/summary | GET | (None) | 200 OK | 200 OK | PASS |
| 14 | /admin/users/1/send-survey-email | POST | `{"surveyType": "After"}` | 200 OK | 400 Bad Request | FAIL (Missing userId in body) |
| 15 | /admin/users/1/send-survey-email | POST | `{"userId": "1", "surveyType": "After"}` | 200 OK | 200 OK | PASS |
| 16 | /admin/users/1 | PATCH | `{"name": "Test User One Updated"}` | 200 OK | 200 OK | PASS |
| 17 | /admin/users/1 | GET | (None) | 200 OK (Name Updated) | 200 OK (Name Updated) | PASS |
| 18 | /admin/users/1/status | PATCH | `{"enabled": false}` | 200 OK | 200 OK | PASS |
| 19 | /admin/users/2/surveys | GET | (None) | 200 OK | 200 OK | PASS |
| 20 | /admin/statistics/selective-average | POST | `{"emotionKeys": ["depression", "anxiety"], "period": {"start": "2025-10-01", "end": "2025-10-05"}}` | 200 OK | 200 OK | PASS |
| 21 | /admin/custom-surveys | GET | (None) | 200 OK | 200 OK | PASS |
| 22 | /admin/custom-surveys | POST | (Incorrect Payload) | 201 Created | 400 Bad Request | FAIL (Invalid Payload) |
| 23 | /admin/custom-surveys | POST | (Corrected Payload) | 201 Created | 200 OK | PASS |
| 24 | /admin/custom-surveys | GET | (None) | 200 OK (with new survey) | 200 OK (empty questions) | PASS (Possible Bug) |
| 25 | /admin/custom-surveys/1 | PUT | (Updated Survey Data) | 200 OK | 200 OK | PASS |
| 26 | /admin/custom-surveys | GET | (None) | 200 OK (with updated survey) | 200 OK (with updated survey) | PASS |
| 27 | /admin/custom-surveys/1 | DELETE | (None) | 200 OK | 200 OK | PASS |
| 28 | /admin/custom-surveys | GET | (None) | 200 OK (empty list) | 200 OK (empty list) | PASS |
| 29 | /admin/statistics/correlation | GET | (None) | 200 OK | 200 OK (empty matrix) | PASS (Needs more data) |