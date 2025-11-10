// File: src/main/java/com/example/quizzapp/controller/StudentController.java
package com.example.quizzapp.controller;

import com.example.quizzapp.dto.SubmissionRequest;
import com.example.quizzapp.model.Quiz;
import com.example.quizzapp.model.QuizParticipation;
import com.example.quizzapp.model.QuizResult;
import com.example.quizzapp.model.Question;
import com.example.quizzapp.model.User;
import com.example.quizzapp.service.QuizParticipationService;
import com.example.quizzapp.service.QuizResultService;
import com.example.quizzapp.service.QuizService;
import com.example.quizzapp.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.security.Principal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentController {

    @Autowired
    private QuizService quizService;

    @Autowired
    private QuizResultService resultService;

    @Autowired
    private UserService userService;

    @Autowired
    private QuizParticipationService participationService;

    @GetMapping("/dashboard")
    public String studentDashboard(Model model, Principal principal) {
        String username = principal.getName();
        User student = userService.findByUsername(username);

        List<Quiz> availableQuizzes = quizService.getAllQuizzes();
        Double averageScore = resultService.getAverageScoreByStudent(student);

        model.addAttribute("student", student);
        model.addAttribute("quizzes", availableQuizzes);
        model.addAttribute("averageScore", averageScore != null ? averageScore : 0.0);

        return "student/dashboard";
    }

    @GetMapping("/join-quiz")
    public String showJoinQuizPage() {
        return "student/join-quiz";
    }

    @PostMapping("/join-quiz")
    public String joinQuiz(@RequestParam String quizCode, RedirectAttributes redirectAttributes, Principal principal) {
        try {
            Quiz quiz = quizService.getQuizByCode(quizCode);
            if (quiz != null) {
                return "redirect:/student/attempt-quiz/" + quiz.getId();
            } else {
                redirectAttributes.addFlashAttribute("error", "Invalid quiz code");
                return "redirect:/student/join-quiz";
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Invalid quiz code");
            return "redirect:/student/join-quiz";
        }
    }

    @GetMapping("/attempt-quiz")
    public String showAvailableQuizzes(Model model) {
        List<Quiz> availableQuizzes = quizService.getAllQuizzes();
        model.addAttribute("quizzes", availableQuizzes);
        return "student/attempt-quiz";
    }

    @GetMapping("/attempt-quiz/{quizId}")
    public String attemptQuizById(@PathVariable Long quizId, Model model) {
        Quiz quiz = quizService.getQuizById(quizId).orElse(null);
        if (quiz == null || !quiz.isPublished()) {
            model.addAttribute("error", "Quiz not available");
            return "student/attempt-quiz";
        }
        model.addAttribute("quiz", quiz);
        return "student/quiz-attempt"; // template path
    }

    @PostMapping("/submitQuiz")
    public String submitQuiz(@RequestParam Long quizId,
                             @RequestParam Long score,
                             @RequestParam Integer correctAnswers,
                             @RequestParam Integer totalQuestions,
                             @RequestParam Integer timeTaken,
                             Principal principal,
                             RedirectAttributes redirectAttributes) {
        String username = principal.getName();
        User student = userService.findByUsername(username);

        // Save participation/result
        QuizParticipation participation = participationService.submitQuizResults(quizId, student.getId(), score.intValue(), correctAnswers, timeTaken);

        redirectAttributes.addFlashAttribute("message", "Quiz submitted successfully");
        return "redirect:/student/my-results";
    }

    @GetMapping("/my-results")
    public String showMyResults(Model model, Principal principal) {
        String username = principal.getName();
        User student = userService.findByUsername(username);
        List<QuizResult> results = resultService.getResultsByStudent(student);
        model.addAttribute("results", results);
        return "student/my-results";
    }

    @GetMapping("/leaderboard")
    public String showLeaderboard(Model model) {
        List<Object[]> leaderboard = resultService.getLeaderboard();
        model.addAttribute("leaderboard", leaderboard);
        return "student/leaderboard";
    }

    @GetMapping("/logout")
    public String logout() {
        return "redirect:/login?logout";
    }

    @PostMapping(path = "/api/submit", consumes = "application/json", produces = "application/json")
    public ResponseEntity<?> submitAnswers(@RequestBody SubmissionRequest submission, Principal principal) {
        try {
            String username = principal.getName();
            User student = userService.findByUsername(username);
            Quiz quiz = quizService.getQuizById(submission.getQuizId()).orElseThrow(() -> new RuntimeException("Quiz not found"));

            // Build a map of questions for quick lookup
            Map<Long, Question> questionMap = quiz.getQuestions().stream().collect(Collectors.toMap(Question::getId, q -> q));

            int totalQuestions = quiz.getQuestions().size();
            int correctAnswers = 0;
            int score = 0;
            int totalTime = 0;

            if (submission.getAnswers() != null) {
                for (SubmissionRequest.AnswerDto a : submission.getAnswers()) {
                    Question q = questionMap.get(a.getQuestionId());
                    if (q == null) continue; // skip invalid question id

                    Integer selected = a.getSelectedOptionIndex();
                    if (selected != null && selected == q.getCorrectOptionIndex()) {
                        correctAnswers++;
                        score += q.getPoints();
                    }
                    if (a.getTimeTakenSeconds() != null) totalTime += a.getTimeTakenSeconds();
                }
            }

            // Persist participation/result
            QuizParticipation participation = participationService.submitQuizResults(quiz.getId(), student.getId(), score, correctAnswers, totalTime);

            // Persist QuizResult as well
            QuizResult quizResult = new QuizResult();
            quizResult.setQuiz(quiz);
            quizResult.setStudent(student);
            quizResult.setScore(score);
            quizResult.setCorrectAnswers(correctAnswers);
            quizResult.setTotalQuestions(totalQuestions);
            quizResult.setTimeTaken(totalTime);
            QuizResult saved = resultService.saveQuizResult(quizResult);

            return ResponseEntity.ok(Map.of(
                    "score", score,
                    "correctAnswers", correctAnswers,
                    "totalQuestions", totalQuestions,
                    "timeTaken", totalTime,
                    "resultId", saved.getId()
            ));
        } catch (Exception ex) {
            return ResponseEntity.badRequest().body(Map.of("error", ex.getMessage()));
        }
    }

    @GetMapping("/result/{id}")
    public String showResultDetail(@PathVariable Long id, Model model, Principal principal) {
        QuizResult result = resultService.findById(id).orElseThrow(() -> new RuntimeException("Result not found"));
        String username = principal.getName();
        if (!result.getStudent().getUsername().equals(username)) {
            throw new RuntimeException("Access denied");
        }
        model.addAttribute("result", result);
        return "student/result-detail";
    }
}
