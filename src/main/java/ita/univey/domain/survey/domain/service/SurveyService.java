package ita.univey.domain.survey.domain.service;

import ita.univey.domain.category.domain.Category;
import ita.univey.domain.category.domain.repository.CategoryRepository;
import ita.univey.domain.survey.domain.Participation;
import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.survey.domain.dto.*;
import ita.univey.domain.survey.domain.repository.Gender;
import ita.univey.domain.survey.domain.repository.ParticipationRepository;
import ita.univey.domain.survey.domain.repository.SurveyRepository;
import ita.univey.domain.survey.domain.repository.SurveyStatus;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.repository.UserRepository;
import ita.univey.domain.user.domain.service.UserService;
import ita.univey.global.CustomLogicException;
import ita.univey.global.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SurveyService {
    private final CategoryRepository categoryRepository;
    private final SurveyRepository surveyRepository;
    private final UserRepository userRepository;
    private final UserService userService;
    private final ParticipationRepository participationRepository;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM-dd-yy");

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
        return point;
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

    public List<Survey> getCreateSurveyByUserEmail(String email) {
        User user = userService.getUserByEmail(email);

        return surveyRepository.findAllByUser(user);
    }

    public List<Survey> getParticipatedSurveyByUserEmail(String email) {
        User user = userService.getUserByEmail(email);
        List<Participation> allByUser = participationRepository.findAllByUser(user);
        List<Survey> surveyList = new ArrayList<>();

        for (Participation participation : allByUser) {
            surveyList.add(participation.getSurvey());
        }
        return surveyList;
    }

    @Transactional
    public void closeSurvey(Survey survey) {
        survey.endSurvey();
    }

    @Transactional
    public Page<SurveyListDto> getSurveyList(String userEmail, String category, String postType, String orderType, PageReqDto pageReqDto) {
        Pageable pageable = pageReqDto.getPageable(Sort.by(orderType).descending());
        User finduser = userRepository.findUserByEmail(userEmail).orElseThrow(() -> new CustomLogicException(ErrorCode.REQUEST_VALIDATION_EXCEPTION));
        Long userId = finduser.getId();

        Category findCategory = null;
        SurveyStatus findStatus = null;

        if (!category.equals("all")) {
            findCategory = categoryRepository.findByCategory(category); //카테고리 찾아서
        }

        if (!postType.equals("all") && !postType.equals("participated")) {
            findStatus = SurveyStatus.getStatusByValue(postType);
        }

        List<Long> participatedSurveyIds = participationRepository.findSurveyIdsByUserId(userId); //설문 참여 했던 설문 아이디 리스트

        if ("participated".equals(postType)) { //참여한 설문 대상
            if ("all".equals(category)) {
                return surveyRepository.findAllByIdIn(participatedSurveyIds, pageable)
                        .map(this::mapToSurveyListDto);
            } else {
                return surveyRepository.findByCategoryAndIdIn(findCategory, participatedSurveyIds, pageable)
                        .map(this::mapToSurveyListDto);
            }
        } else { //참여 안한 설문 대상
            List<Long> excludedSurveyIds = surveyRepository.findIdsNotIn(participatedSurveyIds);
            if ("all".equals(postType)) { //진행 중 + 완료
                if ("all".equals(category)) {
                    return surveyRepository.findAllByIdIn(excludedSurveyIds, pageable)
                            .map(this::mapToSurveyListDto);
                } else {
                    return surveyRepository.findByCategoryAndIdIn(findCategory, excludedSurveyIds, pageable)
                            .map(this::mapToSurveyListDto);
                }
            } else { //진행 중 or 완료
                if ("all".equals(category)) {
                    return surveyRepository.findByIdAndSurveyStatusIn(excludedSurveyIds, findStatus, pageable)
                            .map(this::mapToSurveyListDto);
                } else {
                    return surveyRepository.findByIdAndSurveyStatusAndCategoryIn(excludedSurveyIds, findStatus, findCategory, pageable)
                            .map(this::mapToSurveyListDto);
                }
            }
        }
    }

    @Transactional
    public Page<SurveyListDto> getSearchList(String keyword, String orderType, PageReqDto pageReqDto) {
        Pageable pageable = pageReqDto.getPageable(Sort.by(orderType).descending());
        return surveyRepository.findByTopicContaining(keyword, pageable)
                .map(this::mapToSurveyListDto);
    }

    @Transactional
    public List<TrendListDto> getTrendList(String category) {
        Category findCategory = null;

        if (!category.equals("all")) {
            findCategory = categoryRepository.findByCategory(category); //카테고리 찾아서
            return surveyRepository.findByTrendTrueAndCategory(findCategory)
                    .stream().map(this::mapToTrendListDto)
                    .collect(Collectors.toList());
        }
        else {
            return surveyRepository.findByTrendTrue()
                    .stream().map(this::mapToTrendListDto)
                    .collect(Collectors.toList());
        }
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateSurveyStatus() {
        List<Survey> surveyList = surveyRepository.findBySurveyStateAndDeadlineBefore(SurveyStatus.IN_PROGRESS, LocalDate.now());

        surveyList.forEach(survey -> survey.setSurveyState(SurveyStatus.COMPLETED));
        surveyRepository.saveAll(surveyList);
    }

    private SurveyListDto mapToSurveyListDto(Survey survey) {
        SurveyListDto surveyListDto = new SurveyListDto();
        surveyListDto.setId(survey.getId());
        surveyListDto.setTopic(survey.getTopic());
        surveyListDto.setDescription(survey.getDescription());
        surveyListDto.setCategory(survey.getCategory().getCategory());
        surveyListDto.setTime(survey.getTime());
        surveyListDto.setAge(survey.getAge());
        surveyListDto.setTargetRespondents(survey.getTargetRespondents());
        surveyListDto.setCurrentRespondents(survey.getCurrentRespondents());
        surveyListDto.setPoint(survey.getPoint());
        surveyListDto.setStatus(survey.getSurveyState().getValue());
        surveyListDto.setTrend(survey.isTrend());
        surveyListDto.setDead_line(survey.getDeadline());
        return surveyListDto;
    }

    private TrendListDto mapToTrendListDto(Survey survey) {
        TrendListDto trendListDto = new TrendListDto();
        trendListDto.setId(survey.getId());
        trendListDto.setTopic(survey.getTopic());
        trendListDto.setDescription(survey.getDescription());
        trendListDto.setCategory(survey.getCategory().getCategory());
        trendListDto.setAge(survey.getAge());
        trendListDto.setTargetRespondents(survey.getTargetRespondents());
        trendListDto.setCurrentRespondents(survey.getCurrentRespondents());
        trendListDto.setPoint(survey.getPoint());
        trendListDto.setDead_line(survey.getDeadline());
        return trendListDto;
    }

}
