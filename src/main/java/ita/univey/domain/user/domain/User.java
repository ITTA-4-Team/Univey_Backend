package ita.univey.domain.user.domain;

import ita.univey.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;

@Entity
@Table(name = "User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Where(clause = "status = 'ACTIVE'")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, updatable = false)
    private String email;

    @Column(name = "name")
    private String name;

    @Column(name = "provider_id", nullable = false, unique = true, updatable = false)
    private String providerId;

    @Builder
    public User(String email, String name, String providerId) {
        this.email = email;
        this.name = name;
        this.providerId = providerId;
    }

}
