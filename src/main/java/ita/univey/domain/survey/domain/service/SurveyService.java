package ita.univey.domain.survey.domain.service;

import ita.univey.domain.category.domain.Category;
import ita.univey.domain.category.domain.repository.CategoryRepository;
import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.survey.domain.dto.SurveyCreateDto;
import ita.univey.domain.survey.domain.repository.Gender;
import ita.univey.domain.survey.domain.repository.SurveyRepository;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.repository.UserRepository;
import ita.univey.global.jwt.JwtProvider;
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
    private final UserRepository userRepository;

    public Survey findSurvey(Long surveyId) {
        Survey survey = surveyRepository.findSurveyById(surveyId).orElseThrow(() -> new RuntimeException("찾을 수 없는 설문!"));
        return survey;
    }

    public Long createSurvey(SurveyCreateDto surveyCreateDto, String userEmail) {
        User surveyCreateUser = userRepository.findUserByEmail(userEmail).orElseThrow(() -> new RuntimeException("설문 생성 시 찾을 수 없는 유저"));
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
                .user(surveyCreateUser)
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
