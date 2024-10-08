package com.appsdeveloperblog.tutorials.junit.ui.controllers;

import com.appsdeveloperblog.tutorials.junit.service.UsersService;
import com.appsdeveloperblog.tutorials.junit.service.UsersServiceImpl;
import com.appsdeveloperblog.tutorials.junit.shared.UserDto;
import com.appsdeveloperblog.tutorials.junit.ui.request.UserDetailsRequestModel;
import com.appsdeveloperblog.tutorials.junit.ui.response.UserRest;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityDataConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@MockBean({UsersServiceImpl.class})
@WebMvcTest(controllers = UsersController.class,
excludeAutoConfiguration = { SecurityDataConfiguration.class})
@AutoConfigureMockMvc(addFilters = false)
class UsersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    UsersService usersService;

    @Test
    @DisplayName("User can be created")
    void testCreateUser_whenValidUSerDatailsProvided_returnsCreatedUSerDetails() throws Exception {

        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("any name");
        userDetailsRequestModel.setLastName("any Last name");
        userDetailsRequestModel.setEmail("anyemail@gmial.com");
        userDetailsRequestModel.setPassword("12345678");
        userDetailsRequestModel.setRepeatPassword("123456789");

        UserDto userDto = new ModelMapper().map(userDetailsRequestModel, UserDto.class);
        userDto.setUserId(UUID.randomUUID().toString());

        when(usersService.createUser(any(UserDto.class))).thenReturn(userDto);

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));


        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();
        System.out.println(mvcResult);
        String responseBodyAsString = mvcResult.getResponse().getContentAsString();
        System.out.println("----------------------------------");
        System.out.println(responseBodyAsString);
        System.out.println("----------------------------------");
        UserRest createdUser = new ObjectMapper().readValue(responseBodyAsString, UserRest.class);

        assertEquals(createdUser.getFirstName(), userDetailsRequestModel.getFirstName());
        assertEquals(createdUser.getLastName(), userDetailsRequestModel.getLastName());
        assertEquals(createdUser.getEmail(), userDetailsRequestModel.getEmail());
    }

    @Test
    @DisplayName("First name is not empty")
    void testCreateUser_whenFirstNameIsNotProvided_returns400StatusCode() throws Exception {
        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("");
        userDetailsRequestModel.setLastName("any Last name");
        userDetailsRequestModel.setEmail("anyemail@gmial.com");
        userDetailsRequestModel.setPassword("12345678");
        userDetailsRequestModel.setRepeatPassword("123456789");


        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));


        MvcResult mvcResult =  mockMvc.perform(requestBuilder).andReturn();

        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());
    }

    @Test
    @DisplayName("First name is longer than 1 characters")
    void testCreateUser_whenFirstNameIsNotLongerThan2_returns400StatusCode() throws Exception {
        UserDetailsRequestModel userDetailsRequestModel = new UserDetailsRequestModel();
        userDetailsRequestModel.setFirstName("a");
        userDetailsRequestModel.setLastName("any Last name");
        userDetailsRequestModel.setEmail("anyemail@gmial.com");
        userDetailsRequestModel.setPassword("12345678");
        userDetailsRequestModel.setRepeatPassword("123456789");

        RequestBuilder requestBuilder = MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(new ObjectMapper().writeValueAsString(userDetailsRequestModel));

        MvcResult mvcResult = mockMvc.perform(requestBuilder).andReturn();


        assertEquals(HttpStatus.BAD_REQUEST.value(), mvcResult.getResponse().getStatus());

    }

    @Test
    void getUsers() {
    }
}