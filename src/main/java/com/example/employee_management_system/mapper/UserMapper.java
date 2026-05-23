package com.example.employee_management_system.mapper;

import com.example.employee_management_system.dto.UserDto;
import com.example.employee_management_system.model.Roles;
import com.example.employee_management_system.model.Users;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.factory.Mappers;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


/// by me
/// in the runtime there will be a class that implements this interface
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    @Mapping(target = "password", ignore = true)
    @Mapping(target = "employeeId", ignore = true)
    @Mapping(target = "roles", source = "roles", qualifiedByName = "rolesToSet")
    UserDto toDto(Users user);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "userName", source = "userName")
    @Mapping(target = "enabled", source = "enabled")
    @Mapping(target = "employees", ignore = true)
    /// BY ME
    /// qualifiedByName is used for mapping between UserDto and Users
    /// that's why I used setToRoles method
    @Mapping(target = "roles", source = "roles", qualifiedByName = "setToRoles")
    Users toEntity(UserDto userDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "employees", ignore = true)
    @Mapping(target = "roles", source = "roles", qualifiedByName = "setToRoles")
    void updateEntityFromDto(UserDto userDto, @MappingTarget Users user);

    List<UserDto> toDtoList(List<Users> users);
    List<Users> toEntityList(List<UserDto> userDtos);
    /// BY ME
    /// @Name is used for mapping between Roles and Set<String>
    @Named("rolesToSet")
    default Set<String> rolesToSet(Set<Roles> roles) {
        if (roles == null || roles.isEmpty()) {
            return Set.of();
        }
        return roles.stream()
                .flatMap(role -> role.getAuthorities().stream())
                .map(auth -> auth.getName().replace("ROLE_", ""))
                .collect(Collectors.toSet());
    }
    /// by me
    /// default method in interface is used to not be implemented by the mapper class
    @Named("setToRoles")
    default Set<Roles> setToRoles(Set<String> roleNames) {
        if (roleNames == null || roleNames.isEmpty()) {
            return Set.of();
        }
        return roleNames.stream()
                .map(roleName -> {
                    Roles role = new Roles();
                    role.setRole(roleName);
                    return role;
                })
                .collect(Collectors.toSet());
    }
}
