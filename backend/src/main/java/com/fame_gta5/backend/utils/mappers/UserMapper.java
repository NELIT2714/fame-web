package com.fame_gta5.backend.utils.mappers;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import com.fame_gta5.backend.dto.users.PersonalDataDTO;
import com.fame_gta5.backend.dto.users.UpdateUserDTO;
import com.fame_gta5.backend.schemas.PersonalData;
import com.fame_gta5.backend.schemas.Admin;

@Mapper(componentModel = "spring")
public interface UserMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateUserFromDTO(UpdateUserDTO dto, @MappingTarget Admin user);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updatePersonalDataFromDTO(PersonalDataDTO dto, @MappingTarget PersonalData personalData);

}