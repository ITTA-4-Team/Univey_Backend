package ita.univey.domain.survey.domain.repository;

import ita.univey.domain.survey.domain.Participation;
import ita.univey.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ParticipationRepository extends JpaRepository<Participation, Long> {
    List<Participation> findAllByUser(User user);
}
