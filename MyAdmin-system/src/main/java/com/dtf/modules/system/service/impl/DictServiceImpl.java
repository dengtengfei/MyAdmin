package com.dtf.modules.system.service.impl;

import com.dtf.modules.system.domain.Dict;
import com.dtf.modules.system.repository.DictRepository;
import com.dtf.modules.system.service.DictService;
import com.dtf.modules.system.service.dto.DictDto;
import com.dtf.modules.system.service.dto.DictQueryCriteria;
import com.dtf.modules.system.service.mapstruct.DictMapper;
import com.dtf.utils.PageUtil;
import com.dtf.utils.QueryHelp;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/8 23:50
 */
@Service
@RequiredArgsConstructor
public class DictServiceImpl implements DictService {
    private final DictRepository dictRepository;
    private final DictMapper dictMapper;

    @Override
    public List<DictDto> queryAll(DictQueryCriteria criteria) {
        return dictMapper.toDto(dictRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder))));
    }

    @Override
    public Map<String, Object> queryAll(DictQueryCriteria criteria, Pageable pageable) {
        Page<Dict> page = dictRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(dictMapper::toDto));
    }
}
