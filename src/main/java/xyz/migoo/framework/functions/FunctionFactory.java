package xyz.migoo.framework.functions;

import com.alibaba.fastjson.JSONObject;
import org.reflections.Reflections;
import xyz.migoo.exception.ExecuteError;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author xiaomi
 * @date 2019/11/18 17:10
 */
public class FunctionFactory extends AbstractFunction{

    private final static FunctionFactory FACTORY = new FunctionFactory();
    private static final Reflections REFLECTIONS = new Reflections("xyz.migoo.functions");
    private static final Set<Class<? extends AbstractFunction>> SUB_TYPES = REFLECTIONS.getSubTypesOf(AbstractFunction.class);
    private static final Map<String, Class<? extends AbstractFunction>> FUNCTION_HASH_MAP = new HashMap<>(SUB_TYPES.size());

    static {
        for (Class<? extends AbstractFunction> sub : SUB_TYPES){
            FUNCTION_HASH_MAP.put(sub.getSimpleName().toUpperCase(), sub);
        }
    }

    public static Object execute(String name, String parameter, JSONObject variables) throws ExecuteError {
        FACTORY.getFunction(name);
        FACTORY.addParameter(parameter, variables);
        return FACTORY.execute(null);
    }

    private AbstractFunction function;

    private void getFunction(String name) throws ExecuteError {
        try {
            function = FUNCTION_HASH_MAP.get(name.toUpperCase()).newInstance();
        } catch (Exception e) {
            throw new ExecuteError("get functions error. ", e);
        }
    }

    @Override
    public Object execute(CompoundVariable parameters) throws ExecuteError {
        return function.execute();
    }

    @Override
    public void addParameter(String parameters, JSONObject variables) {
        function.addParameter(parameters, variables);
    }
}
