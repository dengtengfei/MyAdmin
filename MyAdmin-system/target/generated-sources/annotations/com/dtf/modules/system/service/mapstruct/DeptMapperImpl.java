package com.dtf.modules.system.service.mapstruct;

import com.dtf.modules.system.domain.Dept;
import com.dtf.modules.system.service.dto.DeptDto;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Generated;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2021-06-13T20:04:54+0800",
    comments = "version: 1.3.1.Final, compiler: javac, environment: Java 1.8.0_181 (Oracle Corporation)"
)
@Component
public class DeptMapperImpl implements DeptMapper {

    @Override
    public Dept toEntity(DeptDto dto) {
        if ( dto == null ) {
            return null;
        }

        Dept dept = new Dept();

        dept.setCreateBy( dto.getCreateBy() );
        dept.setUpdateBy( dto.getUpdateBy() );
        dept.setCreateTime( dto.getCreateTime() );
        dept.setUpdateTime( dto.getUpdateTime() );
        dept.setId( dto.getId() );
        dept.setDeptSort( dto.getDeptSort() );
        dept.setName( dto.getName() );
        dept.setEnabled( dto.getEnabled() );
        dept.setPid( dto.getPid() );
        dept.setSubCount( dto.getSubCount() );

        return dept;
    }

    @Override
    public DeptDto toDTO(Dept entity) {
        if ( entity == null ) {
            return null;
        }

        DeptDto deptDto = new DeptDto();

        deptDto.setCreateBy( entity.getCreateBy() );
        deptDto.setUpdateBy( entity.getUpdateBy() );
        deptDto.setCreateTime( entity.getCreateTime() );
        deptDto.setUpdateTime( entity.getUpdateTime() );
        deptDto.setId( entity.getId() );
        deptDto.setName( entity.getName() );
        deptDto.setEnabled( entity.getEnabled() );
        deptDto.setDeptSort( entity.getDeptSort() );
        deptDto.setPid( entity.getPid() );
        deptDto.setSubCount( entity.getSubCount() );

        return deptDto;
    }

    @Override
    public List<Dept> toEntity(List<DeptDto> dtoList) {
        if ( dtoList == null ) {
            return null;
        }

        List<Dept> list = new ArrayList<Dept>( dtoList.size() );
        for ( DeptDto deptDto : dtoList ) {
            list.add( toEntity( deptDto ) );
        }

        return list;
    }

    @Override
    public List<DeptDto> toDto(List<Dept> entityList) {
        if ( entityList == null ) {
            return null;
        }

        List<DeptDto> list = new ArrayList<DeptDto>( entityList.size() );
        for ( Dept dept : entityList ) {
            list.add( toDTO( dept ) );
        }

        return list;
    }
}
