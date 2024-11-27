package io.resttestgen.implementation.parametervalueprovider.single;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.google.gson.stream.JsonReader;
import io.resttestgen.core.Environment;
import io.resttestgen.core.datatype.parameter.leaves.LeafParameter;
import io.resttestgen.core.testing.parametervalueprovider.CountableParameterValueProvider;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

public class LlmParameterValueProvider extends CountableParameterValueProvider {

    private static final Logger logger = LogManager.getLogger(LlmParameterValueProvider.class);

    private HashMap<String, Object> llmDictionary;

    public LlmParameterValueProvider() {
        Gson gson = new Gson();
        try {
            JsonReader reader = new JsonReader(new FileReader(Environment.getInstance().getApiUnderTest().getDir() + "/dictionaries/llm.json"));
            llmDictionary = gson.fromJson(reader, LinkedHashMap.class);
        } catch (FileNotFoundException e) {
            llmDictionary = null;
            logger.warn("Could not load LLM dictionary for this API. File not found.");
        }
    }

    @Override
    @SuppressWarnings("unchecked")
    protected Collection<Object> collectValuesFor(LeafParameter leafParameter) {
        String operation = leafParameter.getOperation().toString();
        String parameterName = leafParameter.getName().toString();
        if (llmDictionary != null) {
            if (llmDictionary.containsKey(operation)) {
                if (((LinkedTreeMap<String, Object>) llmDictionary.get(operation)).containsKey(parameterName)) {
                    List<Object> values = (List<Object>) ((LinkedTreeMap<String, Object>) llmDictionary.get(operation)).get(parameterName);
                    values = values.stream().filter(leafParameter::isValueCompliant).collect(Collectors.toList());
                    return values;
                }
            }
        }
        return List.of();
    }
}
