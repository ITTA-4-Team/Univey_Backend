package ita.univey.domain.survey.domain.repository;

import ita.univey.domain.survey.domain.SurveyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {
    @Query("SELECT q FROM SurveyQuestion q WHERE q.survey.id = :surveyId")
    List<SurveyQuestion> findBySurveyId(@Param(value = "surveyId") Long surveyId);

}
