package ita.univey.domain.user.domain.service;

import ita.univey.domain.user.domain.User;
import ita.univey.domain.user.domain.dto.ImageDto;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserImageService {

    public ImageDto getImage(User user) {
        ImageDto dto = ImageDto.builder()
                .originName(user.getUserImage().getOriginImageName())
                .imageName(user.getUserImage().getImageName())
                .pathName(user.getUserImage().getImagePath())
                .build();
        return dto;
    }

}
