package ita.univey.domain.category.domain.repository;

import ita.univey.domain.category.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findCategoryByCategory(String category);

    Category findByCategory(String category);
}
