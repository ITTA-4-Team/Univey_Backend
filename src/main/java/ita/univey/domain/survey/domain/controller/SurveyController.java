package ita.univey.domain.survey.domain.controller;

import ita.univey.domain.survey.domain.dto.SurveyCreateDto;
import ita.univey.domain.survey.domain.service.SurveyService;
import ita.univey.global.BaseResponse;
import ita.univey.global.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/surveys")
public class SurveyController {
    private final SurveyService surveyService;

    @PostMapping("/create")
    public ResponseEntity<BaseResponse<Long>> createSurvey(@Valid @RequestBody SurveyCreateDto surveyCreateDto) {
        Long surveyId = surveyService.createSurvey(surveyCreateDto);

        // 성공 응답을 생성 , gpt 추천 질문 생성해서 보내야 함.
        BaseResponse<Long> successResponse = BaseResponse.success(SuccessCode.CUSTOM_CREATED_SUCCESS, surveyId);

        return ResponseEntity.ok(successResponse);
    }
}
