package com.dtf.modules.system.service;

import com.dtf.modules.system.service.dto.DictDto;
import com.dtf.modules.system.service.dto.DictQueryCriteria;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/8 23:47
 */
public interface DictService {
    /**
     * 查询全部字典
     * @param criteria \
     * @return \
     */
     List<DictDto> queryAll(DictQueryCriteria criteria);

    /**
     * 查询全部字典
     * @param criteria \
     * @param pageable \
     * @return \
     */
     Map<String, Object> queryAll(DictQueryCriteria criteria, Pageable pageable);
}
