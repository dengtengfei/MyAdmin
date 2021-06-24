package com.dtf.modules.system.service;

import com.dtf.modules.system.domain.DictDetail;
import com.dtf.modules.system.service.dto.DictDetailDto;
import com.dtf.modules.system.service.dto.DictDetailQueryCriteria;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.Map;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/24 22:05
 */
public interface DictDetailService {
    void create(DictDetail dictDetail);

    void update(DictDetail dictDetail);

    void delete(Long id);

    Map<String ,Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable);

    List<DictDetailDto> getDictByName(String name);
}
