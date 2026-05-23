package com.example.employee_management_system.mapper;

import com.example.employee_management_system.dto.EmployeeDto;
import com.example.employee_management_system.dto.EmployeePatchDto;
import com.example.employee_management_system.model.Employees;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import org.mapstruct.factory.Mappers;

import java.util.List;
/// by me
/// Mapper interface for mapping between EmployeeDto and Employees
/// componentModel = "spring" indicates that the mapper will be used with Spring

@Mapper(componentModel = "spring")
public interface EmployeeMapper {
    /// by me
    /// Mapper instance is used for mapping between EmployeeDto and Employees
    /// Mappers.getMapper is used to get the mapper instance
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    EmployeeDto toDto(Employees employee);
    /// by me
    /// Mapping between EmployeeDto and Employees
    /// @Mapping is used to map fields between the two classes
    /// target = "id", ignore = true means ignore the id field
    @Mapping(target = "id", ignore = true)
    Employees toEntity(EmployeeDto employeeDto);

    @Mapping(target = "id", ignore = true)
    /// by me
    /// @MappingTarget is used to map fields between the two classes
    void updateEntityFromDto(EmployeeDto employeeDto, @MappingTarget Employees employee);

    @Mapping(target = "id", ignore = true)
    /// by me
    /// @BeanMapping is used to map fields between the two classes
    /// nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE means ignore null values
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromPatchDto(EmployeePatchDto employeePatchDto, @MappingTarget Employees employee);

    List<EmployeeDto> toDtoList(List<Employees> employees);
    List<Employees> toEntityList(List<EmployeeDto> employeeDtos);
}
