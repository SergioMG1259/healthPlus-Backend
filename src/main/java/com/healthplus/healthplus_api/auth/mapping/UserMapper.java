package com.healthplus.healthplus_api.auth.mapping;

import com.healthplus.healthplus_api.auth.domain.model.entity.User;
import com.healthplus.healthplus_api.auth.dto.UserCreateDTO;
import com.healthplus.healthplus_api.auth.dto.UserResponseDTO;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    @Autowired
    ModelMapper modelMapper;

   public User toEntity(UserCreateDTO userCreateDTO) {
       return modelMapper.map(userCreateDTO, User.class);
   }

   public UserResponseDTO toResponseDTO (User user) {
       return modelMapper.map(user, UserResponseDTO.class);
   }
}
