package com.wucf.system.service;


import com.wucf.system.domain.SysUser;

/**
 * 用户 业务层
 */
public interface ISysUserService {

    /**
     * 通过用户名查询用户
     *
     * @param userName 用户名
     * @return 用户对象信息
     */
    SysUser selectUserByUserName(String userName);
}
