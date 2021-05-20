package com.ronz.community;

import com.ronz.community.util.SensitiveFilter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * @Description TODO
 * @Author Ronz
 * @Date 2021/5/13 20:26
 * @Version 1.0
 */


@RunWith(SpringRunner.class)
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class CommonTest {

    @Autowired
    private SensitiveFilter sensitiveFilter;

    @Test
    public void testSensitiveFilter(){
        String info = "您好，这里可以※※嫖※※娼，还可以赌博";

        String str = sensitiveFilter.getFilteredStr(info);

        System.out.println(str);
    }

}
