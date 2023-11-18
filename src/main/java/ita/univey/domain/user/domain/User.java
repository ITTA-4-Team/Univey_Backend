package ita.univey.domain.user.domain;

import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.user.enums.Gender;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "users") //모든 테이블 소문자로 구성
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Where(clause = "status = 'ACTIVE'")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;

    @Column(nullable = false, unique = true, updatable = false)
    private String email;

    @Column(length = 20, nullable = false)
    private String nickname;

    @Column(length = 3, nullable = false)
    private Integer age;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Gender gender;

    @Column(length = 20, nullable = false)
    @ColumnDefault("0")
    private Long point;

    @Builder
    public User(Long id, String email, String nickname, Integer age, Long point) {
        this.id = id;
        this.email = email;
        this.nickname = nickname;
        this.age = age;
        this.point = point;
    }

}
