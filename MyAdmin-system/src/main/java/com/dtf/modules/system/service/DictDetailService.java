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
    /**
     * 创建字典
     * @param dictDetail \
     */
    void create(DictDetail dictDetail);

    /**
     * 更新
     * @param dictDetail \
     */
    void update(DictDetail dictDetail);

    /**
     * 删除
     * @param id \
     */
    void delete(Long id);

    /**
     * 查询全部
     * @param criteria \
     * @param pageable \
     * @return \
     */
    Map<String ,Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable);

    /**
     * 根据名称获取
     * @param name \
     * @return \
     */
    List<DictDetailDto> getDictByName(String name);
}
