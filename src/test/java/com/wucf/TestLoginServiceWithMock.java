package com.wucf;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wucf.core.model.LoginBody;
import com.wucf.web.controller.LoginController;

import com.wucf.system.service.TokenService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.StringContains.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
/**
 * mock 测试接口
 */
@SpringBootTest
@AutoConfigureMockMvc
public class TestLoginServiceWithMock {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private LoginController controller;

    @Autowired
    private TokenService tokenService;

    @Test
    public void contextLoads(){
        assertThat(controller).isNotNull();
    }

    //测试admin账户登录
    @Test
    public void testLogin() throws Exception {
        LoginBody loginBody = new LoginBody("admin", "admin123");
        //创建登录的POST请求
        MockHttpServletRequestBuilder builder = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(loginBody))
                .accept(MediaType.APPLICATION_JSON);
        /**
         * 1.打印返回结果
         * 2.判断响应结果是不是200
         * 3.判断结果Json是否包含Token字段
         * 4.返回结果
         */
        MvcResult mvcResult = this.mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Token").exists())
                .andReturn();

        //获取login接口返回结果字符串
        String resultJson = mvcResult.getResponse().getContentAsString();
        //获取token
        String token = JSONObject.parseObject(resultJson).getString("Token");
        //解析token并判断是否admin账号
        assertThat(tokenService.getUserFromToken(token).getUsername()).isEqualTo("admin");
    }

    @Test
    //未登录请求接口，期望返回200,但是返回封装的json错误码是401
    public void testUnauthorized() throws Exception {
        this.mockMvc
                .perform(get("/testAdmin").accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").isNumber())
                .andExpect(content().string(containsString("401")));
    }

    //测试登录成功但是请求没有权限的接口，期望返回403
    @Test
    public void testForbidden() throws Exception {
        //以USER角色登录
        LoginBody loginBody = new LoginBody("user", "password");
        //创建登录的POST请求
        MockHttpServletRequestBuilder builder = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(loginBody))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = this.mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Token").exists())
                .andReturn();

        //获取login接口返回结果字符串
        String resultJson = mvcResult.getResponse().getContentAsString();
        //获取token
        String token = JSONObject.parseObject(resultJson).getString("Token");

        //请求只有ADMIN角色才能访问的接口
        this.mockMvc
                .perform(get("/testAdmin").header("Token", token).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.code").isNumber())
                .andExpect(content().string(containsString("403")));
    }

    //测试登录成功但是请求没有权限的接口，期望返回200
    @Test
    public void testAuthorized() throws Exception {
        //以ADMIN角色登录
        LoginBody loginBody = new LoginBody("admin", "password");
        //创建登录的POST请求
        MockHttpServletRequestBuilder builder = post("/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(loginBody))
                .accept(MediaType.APPLICATION_JSON);

        MvcResult mvcResult = this.mockMvc.perform(builder)
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.Token").exists())
                .andReturn();

        //获取login接口返回结果字符串
        String resultJson = mvcResult.getResponse().getContentAsString();
        //获取token
        String token = JSONObject.parseObject(resultJson).getString("Token");

        //请求只有ADMIN角色才能访问的接口,如果返回200则代表测试通过
        this.mockMvc
                .perform(get("/testAdmin").header("Token", token))
                .andExpect(status().isOk()).andDo(print());
    }
}
