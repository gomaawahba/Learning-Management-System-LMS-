package com.gomaa.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gomaa.dto.request.QuizGenerateRequest;
import com.gomaa.dto.request.QuizSubmitRequest;
import com.gomaa.dto.response.QuestionResponse;
import com.gomaa.dto.response.QuizAttemptResponse;
import com.gomaa.dto.response.QuizResponse;
import com.gomaa.entity.*;
import com.gomaa.exception.BusinessException;
import com.gomaa.exception.ResourceNotFoundException;
import com.gomaa.repository.*;
import com.gomaa.service.QuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class QuizServiceImpl implements QuizService {

    private final QuizRepository quizRepository;
    private final CourseRepository courseRepository;
    private final QuizAttemptRepository quizAttemptRepository;
    private final ChatClient chatClient;
    private final ObjectMapper objectMapper;

    @Override
    public QuizResponse generateAiQuiz(Long courseId, QuizGenerateRequest request, User instructor) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new ResourceNotFoundException("Course", courseId));

        if (!course.getInstructor().getId().equals(instructor.getId())) {
            throw new BusinessException("Only the course instructor can generate quizzes");
        }

        String prompt = buildQuizPrompt(request);
        log.info("Generating AI quiz for course {} on topic: {}", courseId, request.getTopic());

        String aiResponse = chatClient.prompt()
                .user(prompt)
                .call()
                .content();

        List<Question> questions = parseAiResponse(aiResponse);

        Quiz quiz = Quiz.builder()
                .course(course)
                .title("Quiz: " + request.getTopic())
                .description("AI-generated quiz on " + request.getTopic())
                .passScore(70)
                .aiGenerated(true)
                .build();

        for (int i = 0; i < questions.size(); i++) {
            Question q = questions.get(i);
            q.setQuiz(quiz);
            q.setOrderIndex(i);
            quiz.getQuestions().add(q);
        }

        Quiz saved = quizRepository.save(quiz);
        log.info("AI quiz generated with {} questions for course {}", questions.size(), courseId);
        return mapToQuizResponse(saved);
    }

    @Override
    public QuizAttemptResponse submitQuiz(QuizSubmitRequest request, User student) {
        Quiz quiz = quizRepository.findById(request.getQuizId())
                .orElseThrow(() -> new ResourceNotFoundException("Quiz", request.getQuizId()));

        Map<Long, String> answers = request.getAnswers();
        int correct = 0;

        for (Question question : quiz.getQuestions()) {
            String studentAnswer = answers.get(question.getId());
            if (studentAnswer != null &&
                    studentAnswer.equalsIgnoreCase(question.getCorrectAnswer())) {
                correct++;
            }
        }

        int total = quiz.getQuestions().size();
        int score = total == 0 ? 0 : (int) Math.round((double) correct / total * 100);
        boolean passed = score >= quiz.getPassScore();

        QuizAttempt attempt = QuizAttempt.builder()
                .quiz(quiz)
                .student(student)
                .score(score)
                .passed(passed)
                .build();

        QuizAttempt saved = quizAttemptRepository.save(attempt);
        log.info("Student {} submitted quiz {} with score {}", student.getEmail(), quiz.getId(), score);

        return QuizAttemptResponse.builder()
                .id(saved.getId())
                .quizId(quiz.getId())
                .quizTitle(quiz.getTitle())
                .score(score)
                .passed(passed)
                .passScore(quiz.getPassScore())
                .attemptedAt(saved.getAttemptedAt())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizResponse> getQuizzesByCourse(Long courseId) {
        return quizRepository.findByCourseId(courseId)
                .stream().map(this::mapToQuizResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<QuizAttemptResponse> getMyAttempts(User student) {
        return quizAttemptRepository.findByStudentId(student.getId())
                .stream().map(a -> QuizAttemptResponse.builder()
                        .id(a.getId())
                        .quizId(a.getQuiz().getId())
                        .quizTitle(a.getQuiz().getTitle())
                        .score(a.getScore())
                        .passed(a.isPassed())
                        .passScore(a.getQuiz().getPassScore())
                        .attemptedAt(a.getAttemptedAt())
                        .build())
                .toList();
    }

    private String buildQuizPrompt(QuizGenerateRequest request) {
        return """
            Generate a multiple-choice quiz with EXACTLY %d questions about: %s
            Difficulty: %s
            %s

            Return ONLY a valid JSON array (no markdown, no explanation) in this exact format:
            [
              {
                "questionText": "Question here?",
                "optionA": "First option",
                "optionB": "Second option",
                "optionC": "Third option",
                "optionD": "Fourth option",
                "correctAnswer": "A",
                "explanation": "Brief explanation why A is correct"
              }
            ]
            correctAnswer must be exactly one of: A, B, C, or D.
            """.formatted(
                request.getNumberOfQuestions(),
                request.getTopic(),
                request.getDifficulty(),
                request.getAdditionalContext() != null
                        ? "Additional context: " + request.getAdditionalContext()
                        : ""
        );
    }

    private List<Question> parseAiResponse(String aiResponse) {
        try {
            String cleaned = aiResponse.trim()
                    .replaceAll("```json", "")
                    .replaceAll("```", "")
                    .trim();

            List<Map<String, String>> parsed = objectMapper.readValue(
                    cleaned, new TypeReference<>() {});

            List<Question> questions = new ArrayList<>();
            for (Map<String, String> q : parsed) {
                questions.add(Question.builder()
                        .questionText(q.get("questionText"))
                        .optionA(q.get("optionA"))
                        .optionB(q.get("optionB"))
                        .optionC(q.get("optionC"))
                        .optionD(q.get("optionD"))
                        .correctAnswer(q.get("correctAnswer"))
                        .explanation(q.get("explanation"))
                        .build());
            }
            return questions;
        } catch (Exception e) {
            log.error("Failed to parse AI quiz response: {}", e.getMessage());
            throw new BusinessException("Failed to generate quiz, please try again");
        }
    }

    private QuizResponse mapToQuizResponse(Quiz quiz) {
        List<QuestionResponse> questionResponses = quiz.getQuestions().stream()
                .map(q -> QuestionResponse.builder()
                        .id(q.getId())
                        .questionText(q.getQuestionText())
                        .optionA(q.getOptionA())
                        .optionB(q.getOptionB())
                        .optionC(q.getOptionC())
                        .optionD(q.getOptionD())
                        .orderIndex(q.getOrderIndex())
                        .build())
                .toList();

        return QuizResponse.builder()
                .id(quiz.getId())
                .title(quiz.getTitle())
                .description(quiz.getDescription())
                .passScore(quiz.getPassScore())
                .aiGenerated(quiz.isAiGenerated())
                .questions(questionResponses)
                .build();
    }
}