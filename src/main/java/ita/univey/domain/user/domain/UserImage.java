package ita.univey.domain.user.domain;

import ita.univey.domain.common.BaseEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class UserImage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(length = 500, nullable = true)
    private String originImageName; //원래 파일명 = filename

    @Column(length = 500, nullable = true)
    private String imageName; //실제 저장 되는 파일명(겹치는 것 방지)

    @Column(length = 1000, nullable = true)
    private String imagePath; //저장될 경로와 파일 명을 포함함. 파일을 조회 하여 보여 주거나 다운로드 할 때 이용됨 = savename

    @Builder
    public UserImage(String originImageName, String imageName, String imagePath) {
        this.originImageName = originImageName;
        this.imageName = imageName;
        this.imagePath = imagePath;
    }
}
