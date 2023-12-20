package ita.univey.domain.survey.domain.repository;

import ita.univey.domain.survey.domain.SurveyQuestionAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SurveyQuestionAnswerRepository extends JpaRepository<SurveyQuestionAnswer, Long> {

    @Query("SELECT qa FROM SurveyQuestionAnswer qa WHERE qa.question.id = :questionId")
    List<SurveyQuestionAnswer> findByQuestionId(@Param(value = "questionId") Long questionId);
}
