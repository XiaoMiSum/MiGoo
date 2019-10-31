package xyz.migoo.framework.assertions.function;

import java.util.Map;

/**
 * @author xiaomi
 * @date 2019-08-13 22:17
 */
public class DoseNotContains extends AbstractFunction {

    @Override
    public boolean assertTrue(Map<String, Object> data) {
        return ! new Contains().assertTrue(data);
    }
}
