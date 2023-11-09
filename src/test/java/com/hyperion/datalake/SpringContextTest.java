package com.hyperion.datalake;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.hyperion.datalake.DataLakeApplication;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DataLakeApplication.class)
public class SpringContextTest {

    @Test
    public void whenSpringContextIsBootstrapped_thenNoExceptions() {
    }
}