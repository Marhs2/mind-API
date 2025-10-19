-- Drop existing tables and sequences
DROP TABLE questions;
DROP TABLE surveys;
DROP TABLE survey_results;
DROP TABLE consultations;
DROP TABLE users;

-- Create tables
CREATE TABLE users (
    id NUMBER PRIMARY KEY,
    email VARCHAR2(255) NOT NULL UNIQUE,
    password VARCHAR2(255) NOT NULL,
    name VARCHAR2(255) NOT NULL,
    role VARCHAR2(255) NOT NULL,
    enabled NUMBER(1) DEFAULT 1 NOT NULL
);

CREATE TABLE consultations (
    id NUMBER PRIMARY KEY,
    user_id NUMBER NOT NULL,
    consultation_date TIMESTAMP,
    status VARCHAR2(255),
    CONSTRAINT fk_consultations_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE survey_results (
    id NUMBER PRIMARY KEY,
    consultation_id NUMBER,
    user_id NUMBER NOT NULL,
    survey_type VARCHAR2(255),
    depression NUMBER,
    anxiety NUMBER,
    joy NUMBER,
    anger NUMBER,
    fatigue NUMBER,
    loneliness NUMBER,
    stability NUMBER,
    self_satisfaction NUMBER,
    comments VARCHAR2(4000),
    submission_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_survey_results_consultation FOREIGN KEY (consultation_id) REFERENCES consultations(id),
    CONSTRAINT fk_survey_results_user FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE surveys (
    id NUMBER PRIMARY KEY,
    title VARCHAR2(255) NOT NULL,
    created_by NUMBER,
    creation_date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_surveys_user FOREIGN KEY (created_by) REFERENCES users(id)
);

CREATE TABLE questions (
    id NUMBER PRIMARY KEY,
    survey_id NUMBER NOT NULL,
    text VARCHAR2(1000) NOT NULL,
    min_score NUMBER,
    max_score NUMBER,
    CONSTRAINT fk_questions_survey FOREIGN KEY (survey_id) REFERENCES surveys(id) ON DELETE CASCADE
);

-- Create sequences
CREATE SEQUENCE users_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE consultations_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE survey_results_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE surveys_seq START WITH 1 INCREMENT BY 1;
CREATE SEQUENCE questions_seq START WITH 1 INCREMENT BY 1;
