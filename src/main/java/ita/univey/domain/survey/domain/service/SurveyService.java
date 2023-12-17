package ita.univey.domain.survey.domain.service;

import ita.univey.domain.category.domain.Category;
import ita.univey.domain.category.domain.repository.CategoryRepository;
import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.survey.domain.dto.SurveyCreateDto;
import ita.univey.domain.survey.domain.repository.Gender;
import ita.univey.domain.survey.domain.repository.SurveyRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService {
    private final CategoryRepository categoryRepository;
    private final SurveyRepository surveyRepository;

    public Long createSurvey(SurveyCreateDto surveyCreateDto) {
        String stringGender = surveyCreateDto.getGender();
        Gender gender;
        if (stringGender.equals("female")) {
            gender = Gender.FEMALE;
        } else if (stringGender.equals("male")) {
            gender = Gender.MALE;
        } else {
            gender = Gender.ALL;
        }

        String stringDeadline = surveyCreateDto.getDeadline();
        // 입력된 날짜 형식 지정
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");

        // LocalDate로 parse
        LocalDate deadline = LocalDate.parse(stringDeadline, formatter);
        String stringCategory = surveyCreateDto.getCategory();
        Category category = categoryRepository.findCategoryByCategory(stringCategory)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        Survey newSurvey = Survey.builder()
                .topic(surveyCreateDto.getTopic())
                .description(surveyCreateDto.getDescription())
                .age(surveyCreateDto.getAge())
                .gender(gender) //여성, 남성으로 dto 와야함
                .deadline(deadline)
                .targetRespondents(surveyCreateDto.getTargetRespondents())
                .category(category)
                .build();


        Survey saveSurvey = surveyRepository.save(newSurvey);
        return saveSurvey.getId();
    }
}
