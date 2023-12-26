package ita.univey.domain.survey.domain.service;

import ita.univey.domain.survey.domain.Participation;
import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.survey.domain.SurveyQuestion;
import ita.univey.domain.survey.domain.SurveyQuestionAnswer;
import ita.univey.domain.survey.domain.dto.ParticipationAnswerDto;
import ita.univey.domain.survey.domain.repository.*;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.repository.UserRepository;
import ita.univey.global.CustomLogicException;
import ita.univey.global.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;

@Log4j2
@Service
@RequiredArgsConstructor
public class ParticipationService {

    private final ParticipationRepository participationRepository;
    private final SurveyRepository surveyRepository;
    private final SurveyQuestionRepository surveyQuestionRepository;
    private final UserRepository userRepository;
    private final SurveyQuestionAnswerRepository surveyQuestionAnswerRepository;

    //답변 저장
    @Transactional
    public void participateSurvey(Long userId, Long surveyId, List<ParticipationAnswerDto> answerDtoList) {
        User finduser = userRepository.findById(userId).orElseThrow(() -> new CustomLogicException(ErrorCode.REQUEST_VALIDATION_EXCEPTION));
        Survey findSur = surveyRepository.findById(surveyId).orElseThrow(() -> new CustomLogicException(ErrorCode.REQUEST_VALIDATION_EXCEPTION));

        answerDtoList.forEach(participationAnswerDto -> {
            SurveyQuestion findQues = surveyQuestionRepository.findById(participationAnswerDto.getQuestion_id()).orElseThrow(() -> new CustomLogicException(ErrorCode.REQUEST_VALIDATION_EXCEPTION));

            if (findQues.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                SurveyQuestionAnswer findAns = surveyQuestionAnswerRepository.findById(participationAnswerDto.getAnswer_id()).orElseThrow(() -> new CustomLogicException(ErrorCode.REQUEST_VALIDATION_EXCEPTION));
                Participation participation = Participation.builder()
                        .user(finduser)
                        .survey(findSur)
                        .surveyQuestion(findQues)
                        .surveyQuestionAnswer(findAns)
                        .build();
                participationRepository.save(participation);

            }
            else {
                String content = participationAnswerDto.getContent();
                Participation participation = Participation.builder()
                        .user(finduser)
                        .survey(findSur)
                        .surveyQuestion(findQues)
                        .content(content)
                        .build();
                participationRepository.save(participation);
            }
        });

        //포인트 적립
        finduser.setPoint(finduser.getPoint() + findSur.getPoint());

        //CurrentRespondents 증가
        findSur.setCurrentRespondents(findSur.getCurrentRespondents() + 1);

    }

}