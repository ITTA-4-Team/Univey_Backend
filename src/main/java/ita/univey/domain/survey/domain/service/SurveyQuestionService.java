package ita.univey.domain.survey.domain.service;

import ita.univey.domain.survey.domain.SurveyQuestion;
import ita.univey.domain.survey.domain.SurveyQuestionAnswer;
import ita.univey.domain.survey.domain.dto.QuestionDto;
import ita.univey.domain.survey.domain.dto.SurveyQuestionAnswerDto;
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

    @Transactional
    public List<QuestionDto> getSurveyQuestion(Long surveyId){
        List<QuestionDto> questionDtoList = new ArrayList<>();
        List<SurveyQuestion> questionList = surveyQuestionRepository.findBySurveyId(surveyId);

        questionList.forEach(question -> {
            List<SurveyQuestionAnswer> questionAnswerList = surveyQuestionAnswerRepository.findByQuestionId(question.getId());
            List<SurveyQuestionAnswerDto> answerDtoList = questionAnswerList.stream()
                    .map(answers -> SurveyQuestionAnswerDto.builder()
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

}
