package ita.univey.domain.user.domain;

import ita.univey.domain.category.domain.Category;
import ita.univey.domain.common.BaseEntity;
import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.user.domain.dto.UserInfoDto;
import lombok.*;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Where;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

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

    @Column(name = "nick_name", unique = true)
    private String nickName;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "password", nullable = false, updatable = false) // seucrtiy 설정을 위한 컬럼 추가
    private String password;

    @Column(name = "name")
    private String name;

    @Column(name = "point")
    @ColumnDefault("0")
    private Integer point;

    @Column(name = "provider_id", nullable = false, unique = true, updatable = false)
    private String providerId;

    /*
 @ElementCollection : 컬렉션의 각 요소를 저장할 수 있다. 부모 Entity와 독립적으로 사용 X
 @CollectionTable : @ElementCollection과 함께 사용될 때, 생성될 테이블의 이름 지정
 */
    @ElementCollection(targetClass = UserRole.class)
    @CollectionTable(name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"))
    @Enumerated(EnumType.STRING)
    private Set<UserRole> roleSet;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Survey> surveyList;

    @Builder
    public User(String email, String password, String name, Integer point, String providerId, Set<UserRole> roleSet) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.point = (point != null) ? point : 0; //null일 경우 0으로 저장.
        this.providerId = providerId;
        this.roleSet = roleSet;
    }

    public Integer updatePoint(Integer point) {
        this.point += point;
        return this.point;
    }

    public void updateUserInfo(UserInfoDto userInfoDto) {
        this.nickName = userInfoDto.getNickName();
        this.phoneNumber = userInfoDto.getPhoneNumber();
    }

}
