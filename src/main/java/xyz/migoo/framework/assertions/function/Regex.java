package xyz.migoo.framework.assertions.function;

import com.alibaba.fastjson.JSON;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author xiaomi
 * @date 2019-08-13 22:17
 */
@Alias(aliasList = {"regex"})
public class Regex extends AbstractAssertFunction {

    @Override
    public boolean assertTrue(Map<String, Object> data) {
        Object actual = data.get("actual");
        Object expect = data.get("expect");
        String str = "";
        if (actual instanceof JSON) {
            str = ((JSON) actual).toJSONString();
        }
        if (actual instanceof Number) {
            str = String.valueOf(actual);
        }
        if (actual instanceof String) {
            str = actual.toString();
        }
        Pattern pattern = Pattern.compile(expect.toString());
        Matcher matcher = pattern.matcher(str);
        return matcher.find();
    }
}
