package ita.univey.domain.user.domain;

import lombok.*;

import javax.persistence.*;
import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Table(name = "authority")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Authority {

    @Id
    @Column(name = "Authority_id")
    @GeneratedValue(strategy = IDENTITY)
    private Long id;

    @Column(name = "authority_name", length = 50)
    private String authorityName;
}
