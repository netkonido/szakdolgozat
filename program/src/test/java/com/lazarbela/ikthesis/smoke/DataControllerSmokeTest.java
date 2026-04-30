// example: https://spring.io/guides/gs/testing-web

package com.lazarbela.ikthesis.smoke;

import com.lazarbela.ikthesis.controller.DataController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test whether the context is creating DataController
 */
@SpringBootTest
public class DataControllerSmokeTest {

    @Autowired
    private DataController controller;

    @Test
    void contextLoads() throws Exception {
        assertThat(controller).isNotNull();
    }
}
