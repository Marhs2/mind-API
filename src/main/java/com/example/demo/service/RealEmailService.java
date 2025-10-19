package com.example.demo.service;

import com.example.demo.domain.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * 실제 이메일 전송을 위한 서비스
 * Spring Boot Mail 의존성이 정상적으로 로드된 경우에만 사용
 */
@Service
public class RealEmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    @Autowired
    private UserRepository userRepository;

    @Value("${spring.mail.username:admin@example.com}")
    private String fromEmail;

    @Value("${app.base-url:http://localhost:8081}")
    private String baseUrl;

    /**
     * 실제 이메일 발송이 가능한지 확인
     */
    public boolean isEmailAvailable() {
        return mailSender != null;
    }

    /**
     * 설문 요청 이메일 발송
     */
    public void sendSurveyRequestEmail(String userId, String surveyType) {
        if (!isEmailAvailable()) {
            throw new RuntimeException("이메일 서비스가 사용할 수 없습니다. JavaMailSender가 초기화되지 않았습니다.");
        }

        try {
            // 사용자 정보 조회
            Optional<User> userOptional = userRepository.findById(Long.parseLong(userId));
            if (userOptional.isEmpty()) {
                throw new RuntimeException("사용자를 찾을 수 없습니다: " + userId);
            }

            User user = userOptional.get();
            String toEmail = user.getEmail();
            String userName = user.getName();

            // 이메일 제목 및 내용 생성
            String subject = generateSurveyEmailSubject(surveyType);
            String content = generateSurveyEmailContent(userName, surveyType);

            // 실제 이메일 발송
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            
            System.out.println("실제 이메일 발송 성공: " + toEmail + " - " + subject);
            
        } catch (MailAuthenticationException e) {
            System.err.println("실제 이메일 발송 실패: 인증 오류. application.properties의 이메일 설정을 확인하세요.");
            throw new RuntimeException("이메일 인증에 실패했습니다. 사용자 이름과 비밀번호를 확인하세요.", e);
        } catch (Exception e) {
            System.err.println("실제 이메일 발송 실패: " + e.getMessage());
            throw new RuntimeException("이메일 발송 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }

    /**
     * 환영 이메일 발송
     */
    public void sendWelcomeEmail(String userEmail, String userName) {
        if (!isEmailAvailable()) {
            System.err.println("이메일 서비스가 사용할 수 없습니다. 환영 이메일을 발송하지 않습니다.");
            return;
        }

        try {
            String subject = "[심리 상담] 회원가입을 환영합니다!";
            String content = generateWelcomeEmailContent(userName);

            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(userEmail);
            message.setSubject(subject);
            message.setText(content);

            mailSender.send(message);
            
            System.out.println("실제 환영 이메일 발송 성공: " + userEmail);
            
        } catch (MailAuthenticationException e) {
            System.err.println("실제 환영 이메일 발송 실패: 인증 오류. application.properties의 이메일 설정을 확인하세요.");
            // 환영 이메일은 실패해도 회원가입은 진행되도록 예외를 던지지 않음
        } catch (Exception e) {
            System.err.println("실제 환영 이메일 발송 실패: " + e.getMessage());
            // 환영 이메일은 실패해도 회원가입은 진행되도록 예외를 던지지 않음
        }
    }

    /**
     * 설문 이메일 제목 생성
     */
    private String generateSurveyEmailSubject(String surveyType) {
        if ("Before".equals(surveyType)) {
            return "[심리 상담] 상담 전 설문 요청";
        } else if ("After".equals(surveyType)) {
            return "[심리 상담] 상담 후 설문 요청";
        } else {
            return "[심리 상담] 설문 요청";
        }
    }

    /**
     * 설문 이메일 내용 생성
     */
    private String generateSurveyEmailContent(String userName, String surveyType) {
        StringBuilder content = new StringBuilder();
        
        content.append("안녕하세요, ").append(userName).append("님!\n\n");
        
        if ("Before".equals(surveyType)) {
            content.append("상담 전 심리 상태를 파악하기 위한 설문을 요청드립니다.\n");
            content.append("상담 효과를 정확히 측정하기 위해 상담 시작 전에 설문을 완료해 주시기 바랍니다.\n\n");
        } else if ("After".equals(surveyType)) {
            content.append("상담 후 심리 상태 변화를 측정하기 위한 설문을 요청드립니다.\n");
            content.append("상담의 효과를 분석하기 위해 상담 완료 후에 설문을 완료해 주시기 바랍니다.\n\n");
        } else {
            content.append("심리 상태 모니터링을 위한 설문을 요청드립니다.\n\n");
        }
        
        content.append("설문 참여 방법:\n");
        content.append("1. 아래 링크를 클릭하여 설문 페이지로 이동하세요.\n");
        content.append("2. 각 감정에 대해 1점(매우 낮음)부터 10점(매우 높음)까지 점수를 선택하세요.\n");
        content.append("3. 추가 코멘트가 있으시면 자유롭게 작성해 주세요.\n");
        content.append("4. 설문을 완료한 후 제출 버튼을 클릭하세요.\n\n");
        
        content.append("설문 링크: ").append(baseUrl).append("/survey-form\n\n");
        
        content.append("설문 소요 시간: 약 5-10분\n");
        content.append("설문 완료 후 상담사와 함께 결과를 검토할 예정입니다.\n\n");
        
        content.append("궁금한 점이 있으시면 언제든지 연락 주시기 바랍니다.\n\n");
        content.append("감사합니다.\n");
        content.append("심리 상담팀 드림");
        
        return content.toString();
    }

    /**
     * 환영 이메일 내용 생성
     */
    private String generateWelcomeEmailContent(String userName) {
        StringBuilder content = new StringBuilder();
        
        content.append("안녕하세요, ").append(userName).append("님!\n\n");
        content.append("심리 상담 서비스에 가입해 주셔서 감사합니다.\n\n");
        content.append("저희 서비스에서는 다음과 같은 기능을 제공합니다:\n");
        content.append("• 정기적인 심리 상태 모니터링\n");
        content.append("• 전문 상담사와의 1:1 상담\n");
        content.append("• 개인별 맞춤 통계 및 분석\n");
        content.append("• 상담 전후 심리 상태 변화 추적\n\n");
        content.append("서비스 이용 방법:\n");
        content.append("1. 로그인 후 '내 통계'에서 심리 상태를 확인하세요.\n");
        content.append("2. '설문 제출'을 통해 정기적으로 심리 상태를 기록하세요.\n");
        content.append("3. 필요시 상담 예약을 통해 전문가와 상담하세요.\n\n");
        content.append("궁금한 점이 있으시면 언제든지 연락 주시기 바랍니다.\n\n");
        content.append("건강한 마음으로 함께해요!\n");
        content.append("심리 상담팀 드림");
        
        return content.toString();
    }
}
