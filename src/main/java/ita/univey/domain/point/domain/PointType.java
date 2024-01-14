package ita.univey.domain.point.domain;

import lombok.AllArgsConstructor;

@AllArgsConstructor
public enum PointType {
    POINT_GAIN, // 포인트 획득
    POINT_USAGE, // 포인트 사용
    POINT_PURCHASE // 포인트 구매
}
