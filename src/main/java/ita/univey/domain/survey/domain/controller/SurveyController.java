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
import ita.univey.global.ErrorCode;
import ita.univey.global.SuccessCode;
import ita.univey.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.Sort;
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

    @GetMapping("/create")
    public BaseResponse<SuccessCode> loginCheck() {
        return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS);
    }

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
            log.info("이거 왜안돼... =>{}", userQuestion.isRequired());
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
    public BaseResponse<Map<String, Object>> getSurveyDetail(Authentication authentication, @PathVariable(value = "surveyId") Long surveyId) {
        String userEmail = authentication.getName();
        if (surveyService.getDuplicationCheck(userEmail, surveyId)) {
            return BaseResponse.error(ErrorCode.DUPLICATE_PARTICIPATION, "중복 참여입니다.");
        } else {
            Map<String, Object> map = new HashMap<>();
            map.put("surveyData", surveyService.getSurveyDetail(surveyId));
            BaseResponse<Map<String, Object>> response = BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, map);

            return new BaseResponse<>(response.getStatus(), response.getMessage(), map);
        }
    }

    //답변 등록
    @Transactional
    @PostMapping("/answerSubmit/{surveyId}")
    public BaseResponse<Integer> participateSurvey(
            Authentication authentication, @RequestBody ParticipationReqDto participationReqDto, @PathVariable("surveyId") Long surveyId) {
        String userEmail = authentication.getName();

        List<ParticipationAnswerDto> answerDtoList = participationReqDto.getAnswers();
        Integer updatedPoint = participationService.participateSurvey(userEmail, surveyId, answerDtoList);
        // 설문 참여 시 획득한 포인트 포함해서 다시 전달!
        return BaseResponse.success(SuccessCode.CUSTOM_CREATED_SUCCESS, updatedPoint);
    }

    //리스트 조회
    @RequestMapping(value = "/list/{pageNumber}", method = RequestMethod.GET)
    public BaseResponse<Slice<SurveyListDto>> getSurveyList(Authentication authentication,
                                                            @RequestParam(value = "category", required = false, defaultValue = "all") String category,
                                                            @RequestParam(value = "postType", required = false, defaultValue = "all") String postType,
                                                            @RequestParam(value = "orderType", required = false, defaultValue = "createdAt") String orderType,
                                                            @PathVariable(value = "pageNumber") Integer pageNumber) {

        PageReqDto reqDto = new PageReqDto(pageNumber, 10);
        Pageable pageable = reqDto.getPageable(Sort.by(orderType).descending());

        Slice<SurveyListDto> list = surveyService.getSurveyList2(authentication, category, postType, orderType, pageable);

//        // 참여한 설문은 status participated로 수정
//        if (authentication != null && postType.equals("participated")) {
//            for (SurveyListDto surveyListDto : list) {
//                surveyListDto.setStatus("participated");
//            }
//        }

        BaseResponse<Slice<SurveyListDto>> response = BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, list);
        log.info("=====>{},{},{}", category, postType, orderType);
        return new BaseResponse<>(response.getStatus(), response.getMessage(), list);
    }

    //검색 조회
    @RequestMapping(value = "/search/{pageNumber}", method = RequestMethod.GET)
    public BaseResponse<Slice<SurveyListDto>> getSearchList(@RequestParam(value = "keyword") String keyword,
                                                            @RequestParam(value = "orderType", required = false, defaultValue = "createdAt") String orderType,
                                                            @PathVariable(value = "pageNumber") Integer pageNumber) {

        PageReqDto reqDto = new PageReqDto(pageNumber, 10);
        Pageable pageable = reqDto.getPageable(Sort.by(orderType).descending());

        Slice<SurveyListDto> list = surveyService.getSearchList(keyword, orderType, pageable);
        BaseResponse<Slice<SurveyListDto>> response = BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, list);

        return new BaseResponse<>(response.getStatus(), response.getMessage(), list);
    }

    //설문 결과 보기
    @Transactional
    @GetMapping(value = "/result/{surveyId}")
    public BaseResponse<Map<String, Object>> getSurveyResult(@PathVariable(value = "surveyId") Long surveyId) {

        Map<String, Object> map = new HashMap<>();
        map.put("resultData", surveyService.getSurveyResult(surveyId));
        BaseResponse<Map<String, Object>> response = BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, map);

        return new BaseResponse<>(response.getStatus(), response.getMessage(), map);
    }
}
