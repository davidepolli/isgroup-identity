// src/main/java/it/isgroup/identity/mapper/UserMapper.java
package it.isgroup.identity.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import it.isgroup.identity.jpa.entities.User;
import it.isgroup.identity.rest.dto.UserCreateRequest;
import it.isgroup.identity.rest.dto.UserResponse;
import it.isgroup.identity.rest.dto.UserUpdateRequest;

@Mapper(componentModel = "spring")
public interface UserMapper {

  // Create -> Entity
  User toEntity(UserCreateRequest req);

  // Entity -> Response
  UserResponse toResponse(User user);

  // Update (PUT): copy fields from DTO to existing entity
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntity(@MappingTarget User target, UserUpdateRequest source);
}