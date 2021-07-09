package com.dtf.modules.system.service.impl;

import com.dtf.modules.system.domain.Dict;
import com.dtf.modules.system.domain.DictDetail;
import com.dtf.modules.system.repository.DictDetailRepository;
import com.dtf.modules.system.repository.DictRepository;
import com.dtf.modules.system.service.DictDetailService;
import com.dtf.modules.system.service.dto.DictDetailDto;
import com.dtf.modules.system.service.dto.DictDetailQueryCriteria;
import com.dtf.modules.system.service.mapstruct.DictDetailMapper;
import com.dtf.utils.*;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/6/24 22:21
 */
@Service
@RequiredArgsConstructor
@CacheConfig(cacheNames = "dict")
public class DictDetailServiceImpl implements DictDetailService {
    private final DictRepository dictRepository;
    private final DictDetailRepository dictDetailRepository;
    private final DictDetailMapper dictDetailMapper;
    private final RedisUtils redisUtils;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void create(DictDetail dictDetail) {
        dictDetailRepository.save(dictDetail);
        delCaches(dictDetail);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void delete(Long id) {
        DictDetail dictDetail = dictDetailRepository.findById(id).orElseGet(DictDetail::new);
        delCaches(dictDetail);
        dictDetailRepository.deleteById(id);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(DictDetail dictDetail) {
        // TODO 没明白如何更新的
        DictDetail oldDictDetail = dictDetailRepository.findById(dictDetail.getId()).orElseGet(DictDetail::new);
        ValidationUtil.isNull(oldDictDetail.getId(), "DictDetail", "id", dictDetail.getId());
        dictDetail.setId(oldDictDetail.getId());
        dictDetailRepository.save(dictDetail);
        delCaches(dictDetail);
    }

    @Override
    public Map<String, Object> queryAll(DictDetailQueryCriteria criteria, Pageable pageable) {
        Page<DictDetail> page = dictDetailRepository.findAll((root, criteriaQuery, criteriaBuilder) -> QueryHelp.getPredicate(root, criteria, criteriaBuilder), pageable);
        return PageUtil.toPage(page.map(dictDetailMapper::toDto));
    }

    @Override
    @Cacheable(key = "'name:' + #p0")
    public List<DictDetailDto> getDictByName(String name) {
        return dictDetailMapper.toDto(dictDetailRepository.findByDictName(name));
    }

    private void delCaches(DictDetail dictDetail) {
        Dict dict = dictRepository.findById(dictDetail.getDict().getId()).orElseGet(Dict::new);
        redisUtils.del(CacheKey.DICT_NAME + dict.getName());
    }
}
