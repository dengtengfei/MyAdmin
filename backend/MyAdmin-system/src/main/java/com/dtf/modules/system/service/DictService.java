package com.dtf.modules.system.service;

import com.dtf.modules.system.domain.Dict;
import com.dtf.modules.system.service.dto.DictDto;
import com.dtf.modules.system.service.dto.DictQueryCriteria;
import org.springframework.data.domain.Pageable;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/8 23:47
 */
public interface DictService {
    /**
     * 新增字典
     *
     * @param dict \
     */
    void create(Dict dict);

    /**
     * 根据id列表删除
     *
     * @param ids \
     */
    void delete(Set<Long> ids);

    /**
     * 修改字典
     *
     * @param dict \
     */
    void update(Dict dict);

    /**
     * 查询全部字典
     *
     * @param criteria \
     * @return \
     */
    List<DictDto> queryAll(DictQueryCriteria criteria);

    /**
     * 查询全部字典
     *
     * @param criteria \
     * @param pageable \
     * @return \
     */
    Map<String, Object> queryAll(DictQueryCriteria criteria, Pageable pageable);

    /**
     * 导出字典数据
     *
     * @param dictDtoList \
     * @param response    \
     * @throws IOException \
     */
    void download(List<DictDto> dictDtoList, HttpServletResponse response) throws IOException;
}
