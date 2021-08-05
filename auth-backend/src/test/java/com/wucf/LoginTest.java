package com.wucf;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.wucf.core.model.LoginBody;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class LoginTest {
    @Autowired
    private MockMvc mockMvc;

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

        //拿到Token之后请求 SysUserController /system/user/1

        //请求只有ADMIN角色才能访问的接口
        this.mockMvc
                .perform(get("/system/user/1").header("Authorization", token).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andDo(print());
    }
}
