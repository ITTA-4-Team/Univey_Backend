package ita.univey.global.response;

import lombok.*;

import java.util.List;
 // 데이터를 슬라이스 형식으로 보여주기 위함
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SliceResponseDto<T>{
    List<T> data;
    SliceInfo sliceInfo;
}