package ita.univey.domain.global.response;

import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class SliceInfo {
    private final long getNumber;
    private final long getSize;
    private final long getNumberOfElements;
    private final boolean hasNext;
    private final boolean hasPrevious;

    public SliceInfo(Pageable pageable, long getNumberOfElements, boolean hasNext) {
        this.getNumber = pageable.getPageNumber();
        this.getSize = pageable.getPageSize();
        this.getNumberOfElements = getNumberOfElements;
        this.hasNext = hasNext;
        this.hasPrevious = pageable.hasPrevious();
    }


}
