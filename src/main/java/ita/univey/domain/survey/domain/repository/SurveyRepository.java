package ita.univey.domain.survey.domain.repository;

import ita.univey.domain.category.domain.Category;
import ita.univey.domain.survey.domain.Survey;
import ita.univey.domain.user.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface SurveyRepository extends JpaRepository<Survey, Long> {
    Optional<Survey> findSurveyById(Long id);

    List<Survey> findAllByUser(User user);

    @Query("SELECT s.id FROM Survey s WHERE s.id NOT IN :participatedSurveyIds")
    List<Long> findIdsNotIn(@Param("participatedSurveyIds") List<Long> participatedSurveyIds);

    Page<Survey> findAll(Pageable pageable);

    Page<Survey> findAllBySurveyState(SurveyStatus surveyStatus, Pageable pageable);

    Page<Survey> findAllByCategory(Category category, Pageable pageable);

    Page<Survey> findAllByCategoryAndSurveyState(Category category, SurveyStatus surveyStatus, Pageable pageable);

    Page<Survey> findAllByIdIn(@Param(value = "surveyIds") List<Long> surveyIds, Pageable pageable);

    @Query("SELECT s FROM Survey s WHERE s.category = :category AND s.id IN :surveyIds")
    Page<Survey> findByCategoryAndIdIn(@Param(value = "category") Category category, @Param(value = "surveyIds") List<Long> surveyIds, Pageable pageable);


    @Query("SELECT s FROM Survey s WHERE s.surveyState = :findStatus AND s.id IN :excludedSurveyIds")
    Page<Survey> findByIdAndSurveyStatusIn(@Param(value = "excludedSurveyIds") List<Long> surveyIds, @Param(value = "findStatus") SurveyStatus surveyStatus, Pageable pageable);

    @Query("SELECT s FROM Survey s WHERE s.surveyState = :findStatus AND s.category = :findCategory AND s.id IN :excludedSurveyIds")
    Page<Survey> findByIdAndSurveyStatusAndCategoryIn(@Param(value = "excludedSurveyIds") List<Long> surveyIds, @Param(value = "findStatus") SurveyStatus surveyStatus, @Param(value = "findCategory") Category category, Pageable pageable);

    Page<Survey> findByTopicContaining(String keyword, Pageable pageable);

    List<Survey> findByTrendTrueAndCategory(Category category);

    List<Survey> findByTrendTrue();

    List<Survey> findTop3ByOrderByCurrentRespondentsDesc();

    List<Survey> findTop3ByCategoryOrderByCurrentRespondentsDesc(Category category);

    List<Survey> findBySurveyStateAndDeadlineBefore(SurveyStatus surveyState, LocalDate deadline);


}
