package ita.univey.domain.survey.domain.service;

import ita.univey.domain.category.domain.Category;
import ita.univey.domain.category.domain.repository.CategoryRepository;
import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.survey.domain.dto.QuestionDto;
import ita.univey.domain.survey.domain.dto.SurveyCreateDto;
import ita.univey.domain.survey.domain.dto.SurveyDto;
import ita.univey.domain.survey.domain.repository.Gender;
import ita.univey.domain.survey.domain.repository.SurveyRepository;
import ita.univey.domain.survey.domain.repository.SurveyStatus;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.repository.UserRepository;
import ita.univey.global.CustomLogicException;
import ita.univey.global.ErrorCode;
import ita.univey.global.jwt.JwtProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService {
    private final CategoryRepository categoryRepository;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final SurveyQuestionService surveyQuestionService;

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
                .surveyState(SurveyStatus.IN_PROGRESS)
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

    @Transactional
    public int updatePointById(Long id, int countQuestions) {
        Survey findSurvey = surveyRepository.findSurveyById(id).orElseThrow(() -> new RuntimeException("survey point 업데이트 중 없는 survey 조회"));
        int point = countQuestions * 10; // 문제 1개당 10P, 비율 바뀔 시 이 숫자만 수정하도록.
        findSurvey.updateSurveyPoint(point);
        return findSurvey.getPoint();
    }

    public SurveyDto getSurveyDetail(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId).orElseThrow(() -> new CustomLogicException(ErrorCode.REQUEST_VALIDATION_EXCEPTION));
        List<QuestionDto> questionDtoList = surveyQuestionService.getSurveyQuestion(surveyId);

        SurveyDto dto = SurveyDto.builder()
                .topic(survey.getTopic())
                .description(survey.getDescription())
                .userQuestions(questionDtoList)
                .build();

        return dto;
    }
}
