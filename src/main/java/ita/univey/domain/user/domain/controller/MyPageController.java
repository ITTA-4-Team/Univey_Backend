package ita.univey.domain.user.domain.controller;

import ita.univey.domain.point.domain.PointTransaction;
import ita.univey.domain.point.domain.service.PointTransactionService;
import ita.univey.domain.point.domain.PointType;
import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.survey.domain.service.SurveyService;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.dto.UserInfoDto;
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
    public BaseResponse<UserInfoDto> getUserInfo(Authentication authentication) {
        String userEmail = authentication.getName();
        User userByEmail = userService.getUserByEmail(userEmail);

        UserInfoDto userInfo = UserInfoDto.builder()
                .name(userByEmail.getName())
                .email(userByEmail.getEmail())
                .nickName(userByEmail.getNickName())
                .phoneNumber(userByEmail.getPhoneNumber())
                .build();

        return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, userInfo);
    }

    @GetMapping("/info/{nickName}/exists")
    public BaseResponse<Boolean> checkNicknameDuplicate(@PathVariable String nickName, Authentication authentication) {
        String userEmail = authentication.getName();
        boolean response = !userService.checkNicknameDuplicate(nickName, userEmail);
        return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, response);
    }

    @PatchMapping("/info")
    public BaseResponse<SuccessCode> updateUserInfo(@RequestBody UserInfoDto userInfoDto, Authentication authentication) {
        String userEmail = authentication.getName();
        Long id = userService.updateUserInfoByEmail(userEmail, userInfoDto);

        return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS);

    }

    @GetMapping("/surveys")
    public BaseResponse<List<UserSurveyResponse>> getUserSurveys(@RequestParam String type, Authentication authentication) {

        String userEmail = authentication.getName();
        //User userByEmail = userService.getUserByEmail(userEmail);

        List<UserSurveyResponse> response = surveyService.getMyPageSurvey(userEmail, type);
        return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, response);
    }

    @GetMapping("/surveys/{surveyId}/close")
    public BaseResponse<List<UserSurveyResponse>> terminateSurvey(@PathVariable Long surveyId, Authentication authentication) {
        String userEmail = authentication.getName();
        Survey survey = surveyService.findSurvey(surveyId);

        surveyService.closeSurvey(survey);
        List<UserSurveyResponse> response = surveyService.getMyPageSurvey(userEmail, "created");
        return BaseResponse.success(SuccessCode.SURVEY_TERMINATED_SUCCESS, response);
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
                        .pointType(pointTransaction.getPointType())
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
                        .sub("설문 생성")
                        .pointType(pointTransaction.getPointType())
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
                        .sub("설문 참여")
                        .pointType(PointType.POINT_GAIN)
                        .point(pointTransaction.getPointAmount())
                        .remainingPoint(pointTransaction.getRemainingPoints())
                        .build();
                pointHistoryResponse.add(history);
            }
        } else if (type.equals("all")) {
            List<PointTransaction> userPointHistory = pointTransactionService
                    .getUserTransactionsOrderedByTime(userByEmail);

            for (PointTransaction pointTransaction : userPointHistory) {
                String sub = null;
                PointType transactionType = pointTransaction.getPointType();
                if (transactionType == PointType.POINT_GAIN) {
                    sub = "설문 참여";
                } else if (transactionType == PointType.POINT_USAGE) {
                    sub = "설문 생성";
                }

                String formattedCreatedDay = pointTransaction.getCreatedAt().format(formatter);
                UserPointHistoryResponse history = UserPointHistoryResponse.builder()
                        .createdDay(formattedCreatedDay)
                        .topic(pointTransaction.getSurvey().getTopic())
                        .sub(sub)
                        .pointType(transactionType)
                        .point(pointTransaction.getPointAmount())
                        .remainingPoint(pointTransaction.getRemainingPoints())
                        .build();
                pointHistoryResponse.add(history);
            }
        }
        return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, pointHistoryResponse);
    }
}
