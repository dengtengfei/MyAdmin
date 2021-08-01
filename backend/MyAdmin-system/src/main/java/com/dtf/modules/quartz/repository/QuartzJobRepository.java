package com.dtf.modules.quartz.repository;

import com.dtf.modules.quartz.domain.QuartzJob;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

/**
 * 0 *
 * 1 * @Author:  deng.tengfei
 * 2 * @email:  imdtf@qq.com
 * 3 * @Date:  2021/7/10 20:51
 */
public interface QuartzJobRepository extends JpaRepository<QuartzJob, Long>, JpaSpecificationExecutor<QuartzJob> {
}
