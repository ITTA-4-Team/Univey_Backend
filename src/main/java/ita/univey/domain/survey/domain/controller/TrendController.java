package ita.univey.domain.survey.domain.controller;

import ita.univey.domain.survey.domain.dto.TrendListDto;
import ita.univey.domain.survey.domain.service.SurveyService;
import ita.univey.global.BaseResponse;
import ita.univey.global.SuccessCode;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/trends")
public class TrendController {

    private final SurveyService surveyService;

    //트렌드 조회
    @RequestMapping(value = "", method = RequestMethod.GET)
    public BaseResponse<List<TrendListDto>> getTrendList(@RequestParam(value = "category", required = false, defaultValue = "all") String category) {

        List<TrendListDto> list = surveyService.getTrendList(category);
        BaseResponse<List<TrendListDto>> response = BaseResponse.success(SuccessCode.CUSTOM_SUCCESS, list);

        return new BaseResponse<>(response.getStatus(), response.getMessage(), list);
    }
}
