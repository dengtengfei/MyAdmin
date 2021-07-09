package com.dtf.modules.system.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.dtf.exception.EntityExistException;
import com.dtf.modules.system.domain.Dict;
import com.dtf.modules.system.repository.DictRepository;
import com.dtf.modules.system.service.DictService;
import com.dtf.modules.system.service.dto.DictDetailDto;
import com.dtf.modules.system.service.dto.DictDto;
import com.dtf.modules.system.service.dto.DictQueryCriteria;
import com.dtf.modules.system.service.mapstruct.DictMapper;
import com.dtf.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;

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
    private final RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(Dict dict) {
        if (dictRepository.findByName(dict.getName()) != null) {
            throw new EntityExistException(Dict.class, "name", dict.getName());
        }
        dictRepository.save(dict);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Set<Long> ids) {
        List<Dict> dictList = dictRepository.findByIdIn(ids);
        for (Dict dict : dictList) {
            delCaches(dict);
        }
        dictRepository.deleteByIdIn(ids);
    }

    @Override
    public void update(Dict dict) {
        delCaches(dict);
        Dict oldDict = dictRepository.findById(dict.getId()).orElseGet(Dict::new);
        ValidationUtil.isNull(oldDict.getId(), "Dict", "id", dict.getId());
        Dict dictByName = dictRepository.findByName(dict.getName());
        if (dictByName != null && !dictByName.getId().equals(oldDict.getId())) {
            throw new EntityExistException(Dict.class, "name", dict.getName());
        }
        dictRepository.save(dict);
    }

    @Override
    public List<DictDto> queryAll(DictQueryCriteria criteria) {
        return dictMapper.toDto(dictRepository.findAll(((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder))));
    }

    @Override
    public Map<String, Object> queryAll(DictQueryCriteria criteria, Pageable pageable) {
        Page<Dict> page = dictRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(dictMapper::toDto));
    }

    @Override
    public void download(List<DictDto> dictDtoList, HttpServletResponse response) throws IOException {
        List<Map<String, Object>> list = new ArrayList<>();
        for (DictDto dictDTO : dictDtoList) {
            if(CollectionUtil.isNotEmpty(dictDTO.getDictDetails())){
                for (DictDetailDto dictDetail : dictDTO.getDictDetails()) {
                    Map<String,Object> map = new LinkedHashMap<>();
                    map.put("字典名称", dictDTO.getName());
                    map.put("字典描述", dictDTO.getDescription());
                    map.put("字典标签", dictDetail.getLabel());
                    map.put("字典值", dictDetail.getValue());
                    map.put("创建日期", dictDetail.getCreateTime());
                    list.add(map);
                }
            } else {
                Map<String,Object> map = new LinkedHashMap<>();
                map.put("字典名称", dictDTO.getName());
                map.put("字典描述", dictDTO.getDescription());
                map.put("字典标签", null);
                map.put("字典值", null);
                map.put("创建日期", dictDTO.getCreateTime());
                list.add(map);
            }
        }
        FileUtil.downloadExcel(list, response);
    }

    private void delCaches(Dict dict) {
        redisUtils.del(CacheKey.DICT_NAME + dict.getName());
    }
}
