package ita.univey.domain.user.domain;

import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.user.domain.dto.UserJoinDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.Set;
import java.util.stream.Collectors;

@Entity
@Table(name = "User")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Setter
@Where(clause = "status = 'ACTIVE'")
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true, updatable = false)
    private String email;

    @Column(name = "password", nullable = false, updatable = false) // seucrtiy 설정을 위한 컬럼 추가
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "provider_id", nullable = false, unique = true, updatable = false)
    private String providerId;

    /*
 @ElementCollection : 컬렉션의 각 요소를 저장할 수 있다. 부모 Entity와 독립적으로 사용 X
 @CollectionTable : @ElementCollection과 함께 사용될 때, 생성될 테이블의 이름 지정
 */
    @ElementCollection(targetClass = UserRole.class)
    @CollectionTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "userr_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roleSet;

    @Column(nullable = false) // 포인트
    @ColumnDefault("0")
    private Long point;

    @Builder
    public User(String email, String password, String name, String providerId, Set<UserRole> roleSet, Long point) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.providerId = providerId;
        this.roleSet = roleSet;
        this.point = point;
    }

    public  UserJoinDto.Response toResponseDto(){
        return UserJoinDto.Response.builder()
                //.roleSet(roleSet.stream().map(role -> role.get.name()).collect(Collectors.toList()))
                .email(email)
                .name(name)
                //.genders(genders)
                .point(point)
                .build();
    }
}
