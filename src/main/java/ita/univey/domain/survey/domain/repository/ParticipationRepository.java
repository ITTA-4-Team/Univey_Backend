package ita.univey.domain.survey.domain.repository;

import ita.univey.domain.survey.domain.Participation;
import ita.univey.domain.survey.domain.SurveyQuestion;
import ita.univey.domain.survey.domain.SurveyQuestionAnswer;
import ita.univey.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findAllByUser(User user);

    @Query("SELECT p.survey.id FROM Participation p WHERE p.user.id = :userId")
    List<Long> findSurveyIdsByUserId(@Param(value = "userId") Long userId);

    @Query("SELECT COUNT(p) FROM Participation p WHERE p.surveyQuestionAnswer = :surveyQuestionAnswer")
    Integer countBySurveyQuestionAnswer(@Param("surveyQuestionAnswer") SurveyQuestionAnswer surveyQuestionAnswer);

    @Query("SELECT p.content FROM Participation p WHERE p.surveyQuestion = :surveyQuestion")
    List<String> findContentBySurveyQuestion(@Param("surveyQuestion") SurveyQuestion surveyQuestion);
}
