package com.dtf.modules.mnt.service.mapstruct;

import com.dtf.base.BaseMapper;
import com.dtf.modules.mnt.domain.Deploy;
import com.dtf.modules.mnt.service.dto.DeployDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/14 20:54
 */
@Mapper(componentModel = "spring", uses = {AppMapper.class, ServerDeployMapper.class}, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DeployMapper extends BaseMapper<DeployDto, Deploy> {
}
