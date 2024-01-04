package ita.univey.domain.survey.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

@Builder
@AllArgsConstructor
@Data
public class PageReqDto {
    private int page; //페이지 번호
    private int size; //페이지 사이즈
    private String type;
    private String keyword;

    public PageReqDto() {
        this.page = 1;
        this.size = 10;
    }

    public Pageable getPageable(Sort sort) {
        return PageRequest.of(page - 1, size, sort);
    }

    //public Pageable getPageable() { return PageRequest.of(page - 1, size); }
}