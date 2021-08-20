package com.dtf.modules.mnt.repository;

import com.dtf.modules.mnt.domain.App;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.Set;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/14 19:50
 */
public interface AppRepository extends JpaRepository<App, Long>, JpaSpecificationExecutor<App> {
    /**
     * 根据id列表删除
     *
     * @param ids \
     */
    void deleteAllByIdIn(Set<Long> ids);
}
