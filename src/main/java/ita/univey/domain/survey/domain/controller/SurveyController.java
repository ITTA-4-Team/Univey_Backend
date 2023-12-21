package ita.univey.domain.survey.domain.controller;

import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.survey.domain.SurveyQuestion;
import ita.univey.domain.survey.domain.SurveyQuestionAnswer;
import ita.univey.domain.survey.domain.dto.SurveyCreateDto;
import ita.univey.domain.survey.domain.dto.SurveyDetailsResponse;
import ita.univey.domain.survey.domain.dto.SurveyQuestionsCreateDto;
import ita.univey.domain.survey.domain.repository.QuestionType;
import ita.univey.domain.survey.domain.repository.SurveyQuestionRepository;
import ita.univey.domain.survey.domain.service.SurveyService;
import ita.univey.global.BaseResponse;
import ita.univey.global.SuccessCode;
import ita.univey.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/surveys")
public class SurveyController {
    private final SurveyService surveyService;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final JwtProvider jwtProvider;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<Long>> createSurvey(@Valid @RequestBody SurveyCreateDto surveyCreateDto, Authentication authentication) {
        String userEmail = authentication.getName();
        Long surveyId = surveyService.createSurvey(surveyCreateDto, userEmail);

        // 성공 응답을 생성 , gpt 추천 질문 생성해서 보내야 함.
        BaseResponse<Long> successResponse = BaseResponse.success(SuccessCode.CUSTOM_CREATED_SUCCESS, surveyId);

        return ResponseEntity.ok(successResponse);
    }

    @GetMapping("/create/details/{surveyId}")
    public ResponseEntity<BaseResponse<SurveyDetailsResponse>> surveyDetails(@PathVariable Long surveyId) {
        Survey survey = surveyService.findSurvey(surveyId);

        SurveyDetailsResponse detailsResponse = SurveyDetailsResponse.builder()
                .id(surveyId)
                .topic(survey.getTopic())
                .description(survey.getDescription())
                .build();

        BaseResponse<SurveyDetailsResponse> successResponse = BaseResponse.success(SuccessCode.SURVEY_RETRIEVED_SUCCESS, detailsResponse);
        return ResponseEntity.ok(successResponse);

    }

    @PostMapping("/create/details/{surveyId}")
    public ResponseEntity<BaseResponse<Long>> createQuestions(@Valid @RequestBody SurveyQuestionsCreateDto questionsCreateDto, @PathVariable Long surveyId) {
        Survey survey = surveyService.findSurvey(surveyId);
        List<SurveyQuestionsCreateDto.UserQuestions> userQuestions = questionsCreateDto.getUserQuestions();

        log.info("questionCreatDto => {}", questionsCreateDto);
        for (SurveyQuestionsCreateDto.UserQuestions userQuestion : userQuestions) {
            List<String> answers = userQuestion.getAnswer();
            List<SurveyQuestionAnswer> createAnswers = new ArrayList<>();
            QuestionType questionType;
            if (userQuestion.getQuestionType().equals("multipleChoice")) {
                questionType = QuestionType.MULTIPLE_CHOICE;
            } else {
                questionType = QuestionType.SHORT_ANSWER;
            }
            log.info("question Type =>{}", userQuestion.getQuestionType());
            SurveyQuestion newSurveyQuestion = SurveyQuestion.builder()
                    .survey(survey)
                    .questionNum(userQuestion.getQuestionNum())
                    .question(userQuestion.getQuestion())
                    .questionType(questionType)
                    .surveyQuestionAnswers(new ArrayList<>())
                    .isRequried(userQuestion.isRequired())
                    .build();
            survey.getSurveyQuestions().add(newSurveyQuestion);

            for (String answer : answers) {
                SurveyQuestionAnswer surveyQuestionAnswer = SurveyQuestionAnswer.builder()
                        .answer(answer)
                        .question(newSurveyQuestion)
                        .build();
                createAnswers.add(surveyQuestionAnswer);
            }
            newSurveyQuestion.getSurveyQuestionAnswers().addAll(createAnswers);
            surveyQuestionRepository.save(newSurveyQuestion);

        }

        // 성공 응답을 생성 , gpt 추천 질문 생성해서 보내야 함.
        BaseResponse<Long> successResponse = BaseResponse.success(SuccessCode.CUSTOM_CREATED_SUCCESS, survey.getId());

        return ResponseEntity.ok(successResponse);
    }
}
