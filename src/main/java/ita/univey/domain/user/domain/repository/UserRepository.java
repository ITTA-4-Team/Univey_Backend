package ita.univey.domain.user.domain.repository;

import ita.univey.domain.user.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findUserByEmail(String email);

    Optional<User> findUserById(Long id);

    // 해당 닉네임을 갖고있는 user가 본인말고 있는지
    boolean existsByNickNameAndEmailNotContains(String nickname, String email);


}
