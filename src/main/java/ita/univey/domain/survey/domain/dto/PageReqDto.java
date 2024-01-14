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
    private int pageNumber; //페이지 번호
    private int pageSize; //페이지 사이즈

    public PageReqDto() {
        this.pageNumber = 1;
        this.pageSize = 10;
    }

    public Pageable getPageable(Sort sort) {
        return PageRequest.of(pageNumber - 1, pageSize, sort);
    }
}