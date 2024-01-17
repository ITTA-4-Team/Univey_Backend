package ita.univey.domain.user.domain.controller;

import ita.univey.domain.point.domain.PointTransaction;
import ita.univey.domain.point.domain.service.PointTransactionService;
import ita.univey.domain.point.domain.PointType;
import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.survey.domain.service.SurveyService;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.UserImage;
import ita.univey.domain.user.domain.dto.*;
import ita.univey.domain.user.domain.repository.UserImageRepository;
import ita.univey.domain.user.domain.repository.UserRepository;
import ita.univey.domain.user.domain.service.UserImageService;
import ita.univey.domain.user.domain.service.UserService;
import ita.univey.global.BaseResponse;
import ita.univey.global.CustomLogicException;
import ita.univey.global.ErrorCode;
import ita.univey.global.SuccessCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final UserService userService;
    private final SurveyService surveyService;
    private final PointTransactionService pointTransactionService;
    private final UserImageRepository userImageRepository;
    private final UserImageService userImageService;
    private final UserRepository userRepository;

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
        UserInfoDto userInfoDto;

        if (userByEmail.getUserImage() != null) {
            ImageDto imageDto = userImageService.getImage(userByEmail);
            userInfoDto = userService.getUserDetailWithImage(userByEmail, imageDto);
        } else {
            userInfoDto = userService.getUserDetail(userByEmail);
        }
        return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, userInfoDto);
    }

    @GetMapping("/info/{nickName}/exists")
    public BaseResponse<Boolean> checkNicknameDuplicate(@PathVariable String nickName, Authentication authentication) {
        String userEmail = authentication.getName();
        boolean response = !userService.checkNicknameDuplicate(nickName, userEmail);
        return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, response);
    }

    @PatchMapping("/info")
    public BaseResponse<UserInfoDto> updateUserInfo(@RequestPart(required = false) MultipartFile file, @RequestBody UserInfoDto userInfoDto, Authentication authentication) {
        String userEmail = authentication.getName();
        User user = userService.getUserByEmail(userEmail);

        if (file == null) {
            Long id = userService.updateUserInfoByEmail(userEmail, userInfoDto);
            return BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, userInfoDto);
        }

        if (!file.getContentType().startsWith("image")) {
            return BaseResponse.error(ErrorCode.IMAGE_NOT_CORRECT);
        }
        String originName = file.getOriginalFilename();

        String fileName = Paths.get(originName).getFileName().toString();

        String uuid = UUID.randomUUID().toString();
        String imageName = uuid + "_" + fileName;

        String saveName = "/Users/te___ho/Desktop/" + File.separator + imageName;
        Path savePath = Paths.get(saveName);

        try {
            file.transferTo(savePath);
            ImageDto imageDto = new ImageDto(originName, imageName, saveName);
            userInfoDto.setImageDto(imageDto);

            UserImage userImage = new UserImage(originName, imageName, saveName);
            UserImage saveuserImage = userImageRepository.save(userImage);
            user.setUserImage(saveuserImage);

            Long id = userService.updateUserInfoByEmail(user.getEmail(), userInfoDto, imageDto);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
