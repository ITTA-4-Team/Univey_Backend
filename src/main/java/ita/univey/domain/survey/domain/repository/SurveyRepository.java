package ita.univey.domain.survey.domain.repository;

import ita.univey.domain.survey.domain.Survey;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Optional<Survey> findSurveyById(Long id);
}
