// example: https://spring.io/guides/gs/testing-web

package com.lazarbela.ikthesis.smoke;

import static org.assertj.core.api.Assertions.assertThat;

import com.lazarbela.ikthesis.controller.ActionController;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Test whether the context is creating ActionController
 */
@SpringBootTest
public class ActionControllerSmokeTest {

    @Autowired
    private ActionController controller;

    @Test
    void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
}
