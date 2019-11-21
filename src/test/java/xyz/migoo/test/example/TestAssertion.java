package xyz.migoo.test.example;

import com.alibaba.fastjson.JSONObject;
import xyz.migoo.framework.assertions.AbstractAssertion;

/**
 * @author xiaomi
 * @date 2019-04-14 02:22
 */
public class TestAssertion extends AbstractAssertion {
    @Override
    public boolean assertThat(JSONObject data) {
        String s1 = String.valueOf(data.get("expect"));
        return s1.equalsIgnoreCase("1");
    }

    @Override
    public void setActual(Object actual) {
        this.actual = actual;
    }
}
