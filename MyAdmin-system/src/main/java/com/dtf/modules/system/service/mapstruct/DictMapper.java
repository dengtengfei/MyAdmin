package com.dtf.modules.system.service.mapstruct;

import com.dtf.base.BaseMapper;
import com.dtf.modules.system.domain.Dict;
import com.dtf.modules.system.service.dto.DictDto;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/8 23:50
 */
@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface DictMapper extends BaseMapper<DictDto, Dict> {
}
