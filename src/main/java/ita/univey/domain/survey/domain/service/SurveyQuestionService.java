package ita.univey.domain.survey.domain.service;

import ita.univey.domain.survey.domain.SurveyQuestion;
import ita.univey.domain.survey.domain.SurveyQuestionAnswer;
import ita.univey.domain.survey.domain.dto.QuestionDto;
import ita.univey.domain.survey.domain.dto.ResultQuestionDto;
import ita.univey.domain.survey.domain.dto.SurveyQuestionAnswerDto;
import ita.univey.domain.survey.domain.repository.ParticipationRepository;
import ita.univey.domain.survey.domain.repository.QuestionType;
import ita.univey.domain.survey.domain.repository.SurveyQuestionAnswerRepository;
import ita.univey.domain.survey.domain.repository.SurveyQuestionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SurveyQuestionService {

    private final SurveyQuestionRepository surveyQuestionRepository;
    private final SurveyQuestionAnswerRepository surveyQuestionAnswerRepository;
    private final ParticipationRepository participationRepository;

    @Transactional
    public List<QuestionDto> getSurveyQuestion(Long surveyId){
        List<QuestionDto> questionDtoList = new ArrayList<>();
        List<SurveyQuestion> questionList = surveyQuestionRepository.findBySurveyId(surveyId);

        questionList.forEach(question -> {
            List<SurveyQuestionAnswer> questionAnswerList = surveyQuestionAnswerRepository.findByQuestionId(question.getId());
            List<SurveyQuestionAnswerDto> answerDtoList = questionAnswerList.stream()
                    .map(answers -> SurveyQuestionAnswerDto.builder()
                            .answer_id(answers.getId())
                            .answer(answers.getAnswer())
                            .build())
                    .collect(Collectors.toList());

            QuestionDto questionDto = QuestionDto.builder()
                    .question_id(question.getId())
                    .question_num(question.getQuestionNum())
                    .isRequired(question.isRequried())
                    .question_type(question.getQuestionType())
                    .question(question.getQuestion())
                    .answers(answerDtoList)
                    .build();

            questionDtoList.add(questionDto);
        });
        return questionDtoList;
    }

    @Transactional
    public List<ResultQuestionDto> getSurveyResultQuestion(Long surveyId) {
        List<ResultQuestionDto> resultQuestionDtoList = new ArrayList<>();
        List<SurveyQuestion> questionList = surveyQuestionRepository.findBySurveyId(surveyId);

        questionList.forEach(question -> {
            if (question.getQuestionType().equals(QuestionType.MULTIPLE_CHOICE)) {
                List<SurveyQuestionAnswer> questionAnswerList = surveyQuestionAnswerRepository.findByQuestionId(question.getId());
                List<String> answers = questionAnswerList.stream()
                        .map(SurveyQuestionAnswer::getAnswer)
                        .collect(Collectors.toList());
                List<Integer> votes = questionAnswerList.stream()
                        .map(participationRepository::countBySurveyQuestionAnswer)
                        .collect(Collectors.toList());

                ResultQuestionDto resultQuestionDto = ResultQuestionDto.builder()
                        .question_num(question.getQuestionNum())
                        .question_type(question.getQuestionType())
                        .question(question.getQuestion())
                        .answer(answers)
                        .votes(votes)
                        .build();
                resultQuestionDtoList.add(resultQuestionDto);
            } else {
                List<String> answers = participationRepository.findContentBySurveyQuestion(question);

                ResultQuestionDto resultQuestionDto = ResultQuestionDto.builder()
                        .question_num(question.getQuestionNum())
                        .question_type(question.getQuestionType())
                        .question(question.getQuestion())
                        .answer(answers)
                        .build();
                resultQuestionDtoList.add(resultQuestionDto);
            }
        });

        return resultQuestionDtoList;
    }

}
