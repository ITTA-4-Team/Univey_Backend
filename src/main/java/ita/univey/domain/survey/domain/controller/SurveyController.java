package ita.univey.domain.survey.domain.controller;

import ita.univey.domain.point.domain.PointTransaction;
import ita.univey.domain.point.domain.PointType;
import ita.univey.domain.point.domain.service.PointTransactionService;
import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.survey.domain.SurveyQuestion;
import ita.univey.domain.survey.domain.SurveyQuestionAnswer;
import ita.univey.domain.survey.domain.dto.*;
import ita.univey.domain.survey.domain.repository.QuestionType;
import ita.univey.domain.survey.domain.repository.SurveyQuestionRepository;
import ita.univey.domain.survey.domain.service.ParticipationService;
import ita.univey.domain.survey.domain.service.SurveyService;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.service.UserService;
import ita.univey.global.BaseResponse;
import ita.univey.global.SuccessCode;
import ita.univey.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.transaction.Transactional;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/surveys")
public class SurveyController {
    private final SurveyService surveyService;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final JwtProvider jwtProvider;
    private final ParticipationService participationService;
    private final PointTransactionService pointTransactionService;
    private final UserService userService;

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


    @PostMapping("/submit/{surveyId}")
    public BaseResponse<Integer> submitQuestions(@Valid @RequestBody SurveyQuestionsCreateDto questionsCreateDto, @PathVariable Long surveyId) {

        Survey survey = surveyService.findSurvey(surveyId);
        List<SurveyQuestionsCreateDto.UserQuestions> userQuestions = questionsCreateDto.getUserQuestions();
        int lenQuestion = userQuestions.size();

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


        int surveyPoint = surveyService.updatePointById(surveyId, lenQuestion);
        Integer updatedUserPoint = userService.updatePointByUser(survey.getUser(), -surveyPoint);
        PointTransaction newPointTransaction = PointTransaction.builder()
                .user(survey.getUser())
                .survey(survey)
                .pointType(PointType.POINT_USAGE)
                .pointAmount(surveyPoint)
                .remainingPoints(survey.getUser().getPoint())
                .build();
        pointTransactionService.savePointTransaction(newPointTransaction);

        // 성공 응답을 생성 , gpt 추천 질문 생성해서 보내야 함.
        return BaseResponse.success(SuccessCode.CUSTOM_CREATED_SUCCESS, updatedUserPoint);


    }

    //설문 상세 및 응답 참여
    @Transactional
    @GetMapping(value = "/participation/{surveyId}")
    public BaseResponse<Map<String, Object>> getSurveyDetail(/*@AuthenticationPrincipal User authUser, */@PathVariable(value = "surveyId") Long surveyId) {

        Map<String, Object> map = new HashMap<>();
        map.put("surveyData", surveyService.getSurveyDetail(surveyId));
        BaseResponse<Map<String, Object>> response = BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, map);

        return new BaseResponse<>(response.getStatus(), response.getMessage(), map);
    }

    //답변 등록
    @Transactional
    @PostMapping("/answerSubmit/{surveyId}")
    public BaseResponse<String> participateSurvey(
            @AuthenticationPrincipal User authUser, @RequestBody ParticipationReqDto participationReqDto, @PathVariable("surveyId") Long surveyId) {
        Long userId = authUser.getId();
        List<ParticipationAnswerDto> answerDtoList = participationReqDto.getAnswers();
        participationService.participateSurvey(userId, surveyId, answerDtoList);

        BaseResponse<String> response = BaseResponse.success(SuccessCode.CUSTOM_CREATED_SUCCESS);

        return new BaseResponse<>(response.getStatus(), response.getMessage());
    }
}
