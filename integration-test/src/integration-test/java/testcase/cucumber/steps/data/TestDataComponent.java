package testcase.cucumber.steps.data;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import io.restassured.response.Response;
import org.springframework.stereotype.Component;

@Component
public class TestDataComponent {

    public enum ContextVariable {

        CUSTOMER(String.class),
        ACCOUNT(String.class),
        RESPONSE(Response.class);

        private Class<?> dataType;

        ContextVariable(Class<?> dataType) {
            this.dataType = dataType;
        }

        public Class<?> getDataType() {
            return this.dataType;
        }
    }

    private ConcurrentHashMap<String, Map<ContextVariable, Object>> testDataMap = new ConcurrentHashMap<>();

    public void remove(String contextID, ContextVariable contextVariable) {
        testDataMap.putIfAbsent(contextID, new HashMap<>());

        testDataMap.get(contextID)
                .remove(contextVariable);
    }

    public void put(String contextID, ContextVariable contextVariable, Object data) {
        if (!contextVariable.getDataType().isAssignableFrom(data.getClass())) {
            throw new IllegalArgumentException();
        }
        testDataMap.putIfAbsent(contextID, new HashMap<>());

        testDataMap.get(contextID)
                .put(contextVariable, data);
    }

    public Object get(String contextID, ContextVariable contextVariable) {
        return Optional.ofNullable(this.testDataMap.get(contextID))
                .map(m -> m.get(contextVariable))
                .filter(Objects::nonNull)
                .orElseThrow(() -> new RuntimeException("Variable " + contextVariable + " not exists for " + contextID));
    }

    public boolean exists(String contextID, ContextVariable contextVariable) {
        return Optional.ofNullable(this.testDataMap.get(contextID))
                .map(m -> m.containsKey(contextVariable))
                .orElse(false);
    }

}
