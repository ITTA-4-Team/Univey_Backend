package ita.univey.domain.survey.domain.repository;

import ita.univey.domain.survey.domain.SurveyQuestion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SurveyQuestionRepository extends JpaRepository<SurveyQuestion, Long> {
}
