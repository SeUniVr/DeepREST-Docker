package io.resttestgen.implementation.strategy;

import com.google.gson.Gson;
import io.resttestgen.core.Environment;
import io.resttestgen.core.datatype.parameter.leaves.LeafParameter;
import io.resttestgen.core.datatype.parameter.leaves.NumberParameter;
import io.resttestgen.core.datatype.parameter.leaves.StringParameter;
import io.resttestgen.core.testing.Strategy;
import org.jetbrains.annotations.NotNull;

import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public class InitLlmValuesStrategy extends Strategy {

    @Override
    public void start() {

        // Find all string and number parameters of the API
        List<LeafParameter> parameters = Environment.getInstance().getOpenAPI().getOperations().stream()
                .flatMap(o -> o.getReferenceLeaves().stream())
                .filter(p -> p instanceof StringParameter || p instanceof NumberParameter)
                .collect(Collectors.toList());

        // Prepare output data
        List<HashMap<String, Object>> output = new LinkedList<>();
        for (LeafParameter parameter : parameters) {
            HashMap<String, Object> parameterMap = new HashMap<>();
            parameterMap.put("operation", parameter.getOperation().toString());
            parameterMap.put("operation_description", parameter.getOperation().getDescription());
            parameterMap.put("parameter_name", parameter.getName().toString());
            parameterMap.put("parameter_type", parameter.getType().toString());
            parameterMap.put("parameter_format", parameter.getFormat().toString());
            parameterMap.put("parameter_min", getMin(parameter));
            parameterMap.put("parameter_max", getMax(parameter));
            parameterMap.put("parameter_description", parameter.getDescription());
            output.add(parameterMap);
        }

        // Write output data to file
        Gson gson = new Gson();
        try (FileWriter fw = new FileWriter("./gpt_input.json")) {
            gson.toJson(output, fw);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @NotNull
    private String getMin(LeafParameter parameter) {
        if (parameter instanceof StringParameter) {
            if (((StringParameter) parameter).getMinLength() == null) {
                return "";
            } else {
                return ((StringParameter) parameter).getMinLength().toString();
            }
        } else {
            if (((NumberParameter) parameter).getMinimum() == null) {
                return "";
            } else {
                return Long.toString(((NumberParameter) parameter).getMinimum().longValue());
            }
        }
    }

    @NotNull
    private String getMax(LeafParameter parameter) {
        if (parameter instanceof StringParameter) {
            if (((StringParameter) parameter).getMaxLength() == null) {
                return "";
            } else {
                return ((StringParameter) parameter).getMaxLength().toString();
            }
        } else {
            if (((NumberParameter) parameter).getMaximum() == null) {
                return "";
            } else {
                return Long.toString(((NumberParameter) parameter).getMaximum().longValue());
            }
        }
    }
}
