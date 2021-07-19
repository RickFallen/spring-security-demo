package com.wucf.web.dto;

/**
 * 用户登录对象
 *
 */
public class LoginBody
{
    /**
     * 用户名
     */
    private String username;

    public LoginBody() {

    }

    public LoginBody(String username, String password) {
        this.username = username;
        this.password = password;
    }



    /**
     * 用户密码
     */
    private String password;

    /**
     * 唯一标识
     */
    private String uuid = "";

    public String getUsername()
    {
        return username;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getPassword()
    {
        return password;
    }

    public void setPassword(String password)
    {
        this.password = password;
    }

    public String getUuid()
    {
        return uuid;
    }

    public void setUuid(String uuid)
    {
        this.uuid = uuid;
    }
}
