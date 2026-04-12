package com.avijeet.sprout.dto.mappers;

import com.avijeet.sprout.dto.UserRequestDto;
import com.avijeet.sprout.dto.UserResponseDto;
import com.avijeet.sprout.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserResponseDto toDto(User user);

    @Mapping(target = "id", ignore = true)
    User toEntity(UserRequestDto userRequestDto);
}