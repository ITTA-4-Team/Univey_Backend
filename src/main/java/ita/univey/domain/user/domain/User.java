package ita.univey.domain.user.domain;

import ita.univey.domain.common.BaseEntity;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

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

    @Column(name = "password", nullable = false, unique = true, updatable = false) // seucrtiy 설정을 위한 컬럼 추가
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "provider_id", nullable = false, unique = true, updatable = false)
    private String providerId;

    // seucrtiy 권한 부여를 위해 Authority 엔티티 생성 후 joinTable 사용하여 매핑
    @ManyToMany(cascade = {CascadeType.MERGE, CascadeType.PERSIST})
    @JoinTable(
            name = "user_authority",
            joinColumns = {@JoinColumn(name = "id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "Authority_id", referencedColumnName = "Authority_id")})
    private Set<Authority> authorities = new HashSet<>();

    @Builder
    public User(String email, String password, String name, String providerId, Set<Authority> authorities) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.providerId = providerId;
        this.authorities = authorities;
    }

}
