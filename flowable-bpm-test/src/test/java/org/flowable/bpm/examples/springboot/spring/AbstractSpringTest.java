package org.flowable.bpm.examples.springboot.spring;

import com.tngtech.jgiven.Stage;
import com.tngtech.jgiven.integration.spring.SpringScenarioTest;
import org.flowable.bpm.examples.springboot.App;
import org.flowable.bpm.examples.springboot.spring.configuration.ScenarioConfiguration;
import org.junit.runner.RunWith;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(SpringDataProviderRunner.class)
@SpringBootTest(classes = {
        App.class,
        ScenarioConfiguration.class
})
public abstract class AbstractSpringTest<GIVEN extends Stage<?>, WHEN extends Stage<?>, THEN extends Stage<?>>
        extends SpringScenarioTest<GIVEN, WHEN, THEN> {

    protected String comments(String... comments) {
        return String.join(",", comments);
    }

    protected void setProxyField(Object proxy, String field, Object value) throws Exception {
        ReflectionTestUtils.setField(unwrapProxy(proxy), field, value);
    }

    // http://forum.springsource.org/showthread.php?60216-Need-to-unwrap-a-proxy-to-get-the-object-being-proxied
    protected Object unwrapProxy(Object bean) throws Exception {
        if (AopUtils.isAopProxy(bean) && bean instanceof Advised) {
            Advised advised = (Advised) bean;
            bean = advised.getTargetSource().getTarget();
        }
        return bean;
    }

}
