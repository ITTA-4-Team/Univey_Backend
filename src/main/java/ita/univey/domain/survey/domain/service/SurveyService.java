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
import ita.univey.domain.user.domain.dto.UserSurveyResponse;
import ita.univey.domain.user.domain.repository.UserRepository;
import ita.univey.domain.user.domain.service.UserService;
import ita.univey.global.CustomLogicException;
import ita.univey.global.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
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
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yy");

        // LocalDate로 parse
        LocalDate deadline = LocalDate.parse(stringDeadline, formatter);
        String stringCategory = surveyCreateDto.getCategory();
        Category category = categoryRepository.findCategoryByCategory(stringCategory)
                .orElseThrow(() -> new RuntimeException("Category not found"));
        if (surveyCreateDto.getTargetRespondents() == null) {
            surveyCreateDto.setTargetRespondents(0);
        }
        Survey newSurvey = Survey.builder()
                .user(surveyCreateUser)
                .surveyState(SurveyStatus.IN_PROGRESS)
                .topic(surveyCreateDto.getTopic())
                .description(surveyCreateDto.getDescription())
                .minAge(surveyCreateDto.getAge().getMinAge())
                .maxAge(surveyCreateDto.getAge().getMaxAge())
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

        return surveyRepository.findAllByUserOrderByCreatedAtDesc(user);
    }

    public Set<Survey> getParticipatedSurveyByUserEmail(String email) {
        User user = userService.getUserByEmail(email);
        List<Participation> allByUser = participationRepository.findAllByUserOrderByCreatedAtDesc(user);
        Set<Survey> surveyList = new HashSet<>();

        for (Participation participation : allByUser) {
            surveyList.add(participation.getSurvey());
        }
        return surveyList;
    }

    public List<UserSurveyResponse> getMyPageSurvey(String userEmail, String type) {
        List<Survey> surveyList = new ArrayList<>();
        List<UserSurveyResponse> response = new ArrayList<>();

        if (type.equals("created")) {

            surveyList = getCreateSurveyByUserEmail(userEmail);

        } else if (type.equals("participated")) {
            surveyList = new ArrayList<>(getParticipatedSurveyByUserEmail(userEmail));
        }
        for (Survey survey : surveyList) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yy-MM-dd");
            String createdDate = survey.getCreatedAt().format(formatter);
            String deadline = survey.getDeadline().format(formatter);
            UserSurveyResponse userSurveyResponse = UserSurveyResponse.builder()
                    .surveyId(survey.getId())
                    .status(survey.getSurveyState())
                    .age(UserSurveyResponse.Age.builder()
                            .minAge(survey.getMinAge())
                            .maxAge(survey.getMaxAge())
                            .build())
                    .topic(survey.getTopic())
                    .description(survey.getDescription())
                    .deadline(survey.getDescription())
                    .category(survey.getCategory().getCategory())
                    .createdDay(createdDate)
                    .currentRespondents(survey.getCurrentRespondents())
                    .targetRespondents(survey.getTargetRespondents())
                    .deadline(deadline)
                    .point(survey.getPoint())
                    .build();
            response.add(userSurveyResponse);

        }
        return response;
    }

    @Transactional
    public void closeSurvey(Survey survey) {
        survey.endSurvey();
    }

    public Slice<SurveyListDto> getSurveyList2(Authentication authentication, String category, String postType, String orderType, Pageable pageable) {

        Category findCategory = null;
        SurveyStatus findStatus = null;

        if (authentication == null) {// 로그인 안한 유저일 경우
            log.info("로그인 안한 유저 목록보기");
            if (category.equals("all")) { //카테고리 all일 경우
                if (postType.equals("all")) {// postType all(참여 제외 => 진행중, 완료된 인데 로그인 안한 유저니까 모든 설문)일 경우
                    return surveyRepository.findSliceBy(pageable)
                            .map(this::mapToSurveyListDto);
                }
                if (postType.equals("participated")) {// 카테고리 all + postType 참여 => null
                    List<SurveyListDto> emptyList = Collections.emptyList();
                    return new SliceImpl<>(emptyList);
                } else {
                    findStatus = SurveyStatus.getStatusByValue(postType);// 카테고리 all + 나머지 postType(진행중 or 완료)
                    return surveyRepository.findAllBySurveyState(findStatus, pageable)
                            .map(this::mapToSurveyListDto);
                }
            } else { // 카테고리 all 아닐 경우
                findCategory = categoryRepository.findByCategory(category); //카테고리 찾아서
                if (postType.equals("all")) {// postType all(로그인 안한 유저니까 카테고리만 설정하고 모두)일 경우
                    return surveyRepository.findAllByCategory(findCategory, pageable)
                            .map(this::mapToSurveyListDto);
                }
                if (postType.equals("participated")) {//  postType 참여 => null
                    List<SurveyListDto> emptyList = Collections.emptyList();
                    return new SliceImpl<>(emptyList);
                } else {
                    findStatus = SurveyStatus.getStatusByValue(postType);// 나머지 postType(진행중 or 완료)
                    return surveyRepository.findAllByCategoryAndSurveyState(findCategory, findStatus, pageable)
                            .map(this::mapToSurveyListDto);
                }
            }
        } else { // 로그인 한 유저일 경우
            log.info("로그인한 유저 목록보기");

            String userEmail = authentication.getName();
            User finduser = userRepository.findUserByEmail(userEmail).orElseThrow(() -> new CustomLogicException(ErrorCode.REQUEST_VALIDATION_EXCEPTION));
            Long userId = finduser.getId();

            List<Long> participatedSurveyIds = participationRepository.findSurveyIdsByUserId(userId); //참여한 설문
            List<Long> excludedSurveyIds = surveyRepository.findIdsNotIn(participatedSurveyIds);// 참여하지 않은 설문

            if (category.equals("all")) { //카테고리 all일 경우
                if (postType.equals("all")) {// postType all일 경우
                    // 유저가 참여한 설문 제외 모든 설문(진행중 + 완료) 가져오기
                    if (excludedSurveyIds.isEmpty()) {
                        return surveyRepository.findAll(pageable)
                                .map(this::mapToSurveyListDto);
                    } else {
                        return surveyRepository.findAllByIdIn(excludedSurveyIds, pageable)
                                .map(this::mapToSurveyListDto);
                    }
                }
                if (postType.equals("participated")) {// 카테고리 all + postType 참여
                    return surveyRepository.findAllByIdIn(participatedSurveyIds, pageable)
                            .map(this::mapToSurveyListDto);

                } else { // 카테고리 all + 나머지 postType(진행중 or 완료)
                    findStatus = SurveyStatus.getStatusByValue(postType);
                    if (excludedSurveyIds.isEmpty()) {
                        return surveyRepository.findAllBySurveyState(findStatus, pageable)
                                .map(this::mapToSurveyListDto);
                    } else {
                        return surveyRepository.findByIdAndSurveyStatusIn(excludedSurveyIds, findStatus, pageable)
                                .map(this::mapToSurveyListDto);
                    }
                }
            } else { // 카테고리 all 아닐 경우
                findCategory = categoryRepository.findByCategory(category); //카테고리 찾아서
                if (postType.equals("all")) {// 카테고리 + postType all일 경우 (참여한 설문 제외하고 카테고리 설정)
                    if (excludedSurveyIds.isEmpty()) {
                        return surveyRepository.findAllByCategory(findCategory, pageable)
                                .map(this::mapToSurveyListDto);
                    } else {
                        return surveyRepository.findByCategoryAndIdIn(findCategory, excludedSurveyIds, pageable)
                                .map(this::mapToSurveyListDto);
                    }
                }
                if (postType.equals("participated")) {// 카테고리  + postType 참여
                    return surveyRepository.findByCategoryAndIdIn(findCategory, participatedSurveyIds, pageable)
                            .map(this::mapToSurveyListDto);
                } else {
                    //카테고리 + 상태
                    findStatus = SurveyStatus.getStatusByValue(postType);//
                    if (excludedSurveyIds.isEmpty()) {
                        return surveyRepository.findAllByCategoryAndSurveyState(findCategory, findStatus, pageable)
                                .map(this::mapToSurveyListDto);
                    } else {
                        return surveyRepository.findByIdAndSurveyStatusAndCategoryIn(excludedSurveyIds, findStatus, findCategory, pageable)
                                .map(this::mapToSurveyListDto);
                    }
                }
            }
        }
    }

//    @Transactional
//    public Page<SurveyListDto> getSurveyList(String userEmail, String category, String postType, String orderType, PageReqDto pageReqDto) {
//        Pageable pageable = pageReqDto.getPageable(Sort.by(orderType).descending());
//        User finduser = userRepository.findUserByEmail(userEmail).orElseThrow(() -> new CustomLogicException(ErrorCode.REQUEST_VALIDATION_EXCEPTION));
//        Long userId = finduser.getId();
//
//        Category findCategory = null;
//        SurveyStatus findStatus = null;
//
//        if (!category.equals("all")) {
//            findCategory = categoryRepository.findByCategory(category); //카테고리 찾아서
//        }
//
//        if (!postType.equals("all") && !postType.equals("participated")) {
//            findStatus = SurveyStatus.getStatusByValue(postType);
//        }
//
//        List<Long> participatedSurveyIds = participationRepository.findSurveyIdsByUserId(userId); //설문 참여 했던 설문 아이디 리스트
//
//        if ("participated".equals(postType)) { //참여한 설문 대상
//            if ("all".equals(category)) {
//                return surveyRepository.findAllByIdIn(participatedSurveyIds, pageable)
//                        .map(this::mapToSurveyListDto);
//            } else {
//                return surveyRepository.findByCategoryAndIdIn(findCategory, participatedSurveyIds, pageable)
//                        .map(this::mapToSurveyListDto);
//            }
//        } else { //참여 안한 설문 대상
//            List<Long> excludedSurveyIds = surveyRepository.findIdsNotIn(participatedSurveyIds);
//            if ("all".equals(postType)) { //진행 중 + 완료
//                if ("all".equals(category)) {
//                    return surveyRepository.findAllByIdIn(excludedSurveyIds, pageable)
//                            .map(this::mapToSurveyListDto);
//                } else {
//                    return surveyRepository.findByCategoryAndIdIn(findCategory, excludedSurveyIds, pageable)
//                            .map(this::mapToSurveyListDto);
//                }
//            } else { //진행 중 or 완료
//                if ("all".equals(category)) {
//                    return surveyRepository.findByIdAndSurveyStatusIn(excludedSurveyIds, findStatus, pageable)
//                            .map(this::mapToSurveyListDto);
//                } else {
//                    return surveyRepository.findByIdAndSurveyStatusAndCategoryIn(excludedSurveyIds, findStatus, findCategory, pageable)
//                            .map(this::mapToSurveyListDto);
//                }
//            }
//        }
//    }

    @Transactional
    public Slice<SurveyListDto> getSearchList(String keyword, String orderType, Pageable pageable) {
        return surveyRepository.findByTopicContaining(keyword, pageable)
                .map(this::mapToSurveyListDto);
    }

    @Transactional
    public List<TrendListDto> getTrendList(Authentication authentication, String category) {
        List<TrendListDto> trendList = new ArrayList<>();

        if (!category.equals("all")) {
            Category findCategory = categoryRepository.findByCategory(category); //카테고리 찾아서
            trendList = surveyRepository.findTop3ByCategoryOrderByCurrentRespondentsDesc(findCategory)
                    .stream().map(this::mapToTrendListDto)
                    .collect(Collectors.toList());


        } else {
            trendList = surveyRepository.findTop3ByOrderByCurrentRespondentsDesc()
                    .stream().map(this::mapToTrendListDto)
                    .collect(Collectors.toList());
        }

        if (authentication == null) {
            for (TrendListDto trendListDto : trendList) {
                trendListDto.setParticipated(false);
            }
        } else { // 로그인 했다면 참여했는지 안했는지 여부로 설정
            String userEmail = authentication.getName();
            for (TrendListDto trendListDto : trendList) {
                trendListDto.setParticipated(getDuplicationCheck(userEmail, trendListDto.getId()));
            }
        }

        return trendList;
    }

    @Transactional
    @Scheduled(cron = "0 0 0 * * *")
    public void updateSurveyStatus() {
        List<Survey> surveyList = surveyRepository.findBySurveyStateAndDeadlineBefore(SurveyStatus.IN_PROGRESS, LocalDate.now());

        surveyList.forEach(survey -> survey.setSurveyState(SurveyStatus.COMPLETED));
        surveyRepository.saveAll(surveyList);
    }

    public ResultDto getSurveyResult(Long surveyId) {
        Survey survey = surveyRepository.findById(surveyId).orElseThrow(() -> new CustomLogicException(ErrorCode.REQUEST_VALIDATION_EXCEPTION));
        List<ResultQuestionDto> resultQuestionDtoList = surveyQuestionService.getSurveyResultQuestion(surveyId);

        ResultDto dto = ResultDto.builder()
                .id(surveyId)
                .topic(survey.getTopic())
                .description(survey.getDescription())
                .question(resultQuestionDtoList)
                .build();

        return dto;
    }

    public boolean getDuplicationCheck(String userEmail, Long surveyId) {
        User finduser = userRepository.findUserByEmail(userEmail).orElseThrow(() -> new CustomLogicException(ErrorCode.REQUEST_VALIDATION_EXCEPTION));
        Survey findSurvey = surveyRepository.findSurveyById(surveyId).orElseThrow(() -> new RuntimeException("없는 설문 조회"));
        return participationRepository.existsByUserAndSurvey(finduser, findSurvey);
    }

    private SurveyListDto mapToSurveyListDto(Survey survey) {
        SurveyListDto surveyListDto = new SurveyListDto();
        surveyListDto.setId(survey.getId());
        surveyListDto.setTopic(survey.getTopic());
        surveyListDto.setDescription(survey.getDescription());
        surveyListDto.setCategory(survey.getCategory().getCategory());
        surveyListDto.setTime(survey.getTime());
        surveyListDto.setAge(AgeDto.builder()
                .minAge(survey.getMinAge())
                .maxAge(survey.getMaxAge())
                .build());
        surveyListDto.setQuestionCount(survey.getSurveyQuestions().size());
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
        trendListDto.setAge(AgeDto.builder()
                .maxAge(survey.getMaxAge())
                .minAge(survey.getMinAge())
                .build());
        trendListDto.setTargetRespondents(survey.getTargetRespondents());
        trendListDto.setCurrentRespondents(survey.getCurrentRespondents());
        trendListDto.setPoint(survey.getPoint());
        trendListDto.setDead_line(survey.getDeadline());
        trendListDto.setStatus(survey.getSurveyState().getValue());
        return trendListDto;
    }

}
