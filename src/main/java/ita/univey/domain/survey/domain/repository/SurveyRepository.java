package ita.univey.domain.survey.domain.repository;

import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Optional<Survey> findSurveyById(Long id);

    List<Survey> findAllByUser(User user);


}
