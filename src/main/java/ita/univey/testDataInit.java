package ita.univey;

import ita.univey.domain.category.domain.Category;
import ita.univey.domain.category.domain.repository.CategoryRepository;
import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.survey.domain.repository.Gender;
import ita.univey.domain.survey.domain.repository.SurveyRepository;
import ita.univey.domain.survey.domain.repository.SurveyStatus;
import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.UserRole;
import ita.univey.domain.user.domain.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class testDataInit {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final SurveyRepository surveyRepository;
    private final CategoryRepository categoryRepository;

    @PostConstruct
    public void adminInit() {
        Set<UserRole> roles = new HashSet<>();
        roles.add(UserRole.ROLE_USER);
        roles.add(UserRole.ROLE_ADMIN);
        User user = User.builder()
                .name("admin")
                .email("admin")
                .password(passwordEncoder.encode("admin"))
                .roleSet(roles)
                .providerId("admin") // 임의의 providerId 생성 , 카카오로그인 작성 시 카카오에서 받아온 값으로 변경.
                .build();
        User saveMember = userRepository.save(user);

        User user2 = User.builder()
                .name("이상연")
                .email("asylee02@naver.com")
                .password(passwordEncoder.encode("asylee02@naver.com_3168662017"))
                .roleSet(Collections.singleton(UserRole.ROLE_USER))
                .providerId("3168662017") // 임의의 providerId 생성 , 카카오로그인 작성 시 카카오에서 받아온 값으로 변경.
                .build();
        log.info("admin 계정 등록 완료.");

    }

    @PostConstruct
    public void categoryInit() {
        List<Category> categoryList = new ArrayList<>();
        Category category1 = Category.builder()
                .category("education")
                .build();
        categoryList.add(category1);

        Category category2 = Category.builder()
                .category("IT")
                .build();
        categoryList.add(category2);

        Category category3 = Category.builder()
                .category("economy")
                .build();
        categoryList.add(category3);

        Category category4 = Category.builder()
                .category("society")
                .build();
        categoryList.add(category4);

        Category category5 = Category.builder()
                .category("culture")
                .build();
        categoryList.add(category5);

        categoryRepository.saveAll(categoryList);
    }


}
