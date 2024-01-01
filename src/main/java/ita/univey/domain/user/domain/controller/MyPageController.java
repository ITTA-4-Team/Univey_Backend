package ita.univey.domain.user.domain.controller;

import ita.univey.domain.point.domain.PointTransaction;
import ita.univey.domain.point.domain.service.PointTransactionService;
import ita.univey.domain.point.domain.PointType;
import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.survey.domain.service.SurveyService;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.dto.UserInfoResponse;
import ita.univey.domain.user.domain.dto.UserLoginResponseDto;
import ita.univey.domain.user.domain.dto.UserPointHistoryResponse;
import ita.univey.domain.user.domain.dto.UserSurveyResponse;
import ita.univey.domain.user.domain.service.UserService;
import ita.univey.global.BaseResponse;
import ita.univey.global.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final UserService userService;
    private final SurveyService surveyService;
    private final PointTransactionService pointTransactionService;

    @GetMapping
    public BaseResponse<UserLoginResponseDto> getMyPage(Authentication authentication) {
        String userEmail = authentication.getName();
        User userByEmail = userService.getUserByEmail(userEmail);

        UserLoginResponseDto userNameEmail = UserLoginResponseDto.builder()
                .userName(userByEmail.getName())
                .email(userByEmail.getEmail())
                .build();

        return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, userNameEmail);
    }

    @GetMapping("/info")
    public BaseResponse<UserInfoResponse> getUserInfo(Authentication authentication) {
        String userEmail = authentication.getName();
        User userByEmail = userService.getUserByEmail(userEmail);

        UserInfoResponse userInfo = UserInfoResponse.builder()
                .name(userByEmail.getName())
                .email(userByEmail.getEmail())
                .build();

        return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, userInfo);
    }

    @GetMapping("/surveys")
    public BaseResponse<List<UserSurveyResponse>> getUserSurveys(@RequestParam String type, Authentication authentication) {
        String userEmail = authentication.getName();
        //User userByEmail = userService.getUserByEmail(userEmail);

        List<Survey> surveyList = new ArrayList<>();
        List<UserSurveyResponse> response = new ArrayList<>();

        if (type.equals("created")) {

            surveyList = surveyService.getCreateSurveyByUserEmail(userEmail);

        } else if (type.equals("participated")) {
            surveyList = surveyService.getParticipatedSurveyByUserEmail(userEmail);
        }
        for (Survey survey : surveyList) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
            String createdDate = survey.getCreatedAt().format(formatter);
            String deadline = survey.getDeadline().format(formatter);
            UserSurveyResponse userSurveyResponse = UserSurveyResponse.builder()
                    .surveyId(survey.getId())
                    .topic(survey.getTopic())
                    .description(survey.getDescription())
                    .deadline(survey.getDescription())
                    .category(survey.getCategory().getCategory())
                    .createdDay(createdDate)
                    .deadline(deadline)
                    .point(survey.getPoint())
                    .build();
            response.add(userSurveyResponse);

        }
        return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, response);
    }

    @GetMapping("/surveys/{surveyId}/close")
    public BaseResponse<Object> terminateSurvey(@PathVariable Long surveyId) {
        Survey survey = surveyService.findSurvey(surveyId);
        surveyService.closeSurvey(survey);
        return BaseResponse.success(SuccessCode.SURVEY_TERMINATED_SUCCESS);
    }

    @GetMapping("/point")
    public BaseResponse<List<UserPointHistoryResponse>> pointHistory(@RequestParam String type, Authentication authentication) {
        String userEmail = authentication.getName();
        User userByEmail = userService.getUserByEmail(userEmail);

        List<UserPointHistoryResponse> pointHistoryResponse = new ArrayList();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");

        if (type.equals("purchase")) {
            List<PointTransaction> userPointHistory = pointTransactionService
                    .getUserTransactionsByTypeOrderedByTime(userByEmail, PointType.POINT_PURCHASE);
            for (PointTransaction pointTransaction : userPointHistory) {

                String formattedCreatedDay = pointTransaction.getCreatedAt().format(formatter);
                UserPointHistoryResponse history = UserPointHistoryResponse.builder()
                        .createdDay(formattedCreatedDay)
                        .topic(pointTransaction.getSurvey().getTopic())
                        .pointType(PointType.POINT_PURCHASE)
                        .point(pointTransaction.getPointAmount())
                        .remainingPoint(pointTransaction.getRemainingPoints())
                        .build();
                pointHistoryResponse.add(history);
            }
        } else if (type.equals("usage")) {
            List<PointTransaction> userPointHistory = pointTransactionService
                    .getUserTransactionsByTypeOrderedByTime(userByEmail, PointType.POINT_USAGE);
            for (PointTransaction pointTransaction : userPointHistory) {

                String formattedCreatedDay = pointTransaction.getCreatedAt().format(formatter);
                UserPointHistoryResponse history = UserPointHistoryResponse.builder()
                        .createdDay(formattedCreatedDay)
                        .topic(pointTransaction.getSurvey().getTopic())
                        .pointType(PointType.POINT_USAGE)
                        .point(pointTransaction.getPointAmount())
                        .remainingPoint(pointTransaction.getRemainingPoints())
                        .build();
                pointHistoryResponse.add(history);
                log.info("history=>{}", history);
            }
        } else if (type.equals("acquisition")) {
            List<PointTransaction> userPointHistory = pointTransactionService
                    .getUserTransactionsByTypeOrderedByTime(userByEmail, PointType.POINT_GAIN);
            for (PointTransaction pointTransaction : userPointHistory) {

                String formattedCreatedDay = pointTransaction.getCreatedAt().format(formatter);
                UserPointHistoryResponse history = UserPointHistoryResponse.builder()
                        .createdDay(formattedCreatedDay)
                        .topic(pointTransaction.getSurvey().getTopic())
                        .pointType(PointType.POINT_GAIN)
                        .point(pointTransaction.getPointAmount())
                        .remainingPoint(pointTransaction.getRemainingPoints())
                        .build();
                pointHistoryResponse.add(history);
            }

        }
        return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, pointHistoryResponse);
    }
}