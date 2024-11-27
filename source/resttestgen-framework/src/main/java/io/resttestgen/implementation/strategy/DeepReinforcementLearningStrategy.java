package io.resttestgen.implementation.strategy;

import io.resttestgen.core.Environment;
import io.resttestgen.core.datatype.HttpStatusCode;
import io.resttestgen.core.helper.DeepReinforcementLearningProxy;
import io.resttestgen.core.openapi.Operation;
import io.resttestgen.core.testing.*;
import io.resttestgen.core.testing.operationsorter.OperationsSorter;
import io.resttestgen.implementation.oracle.StatusCodeOracle;
import io.resttestgen.implementation.fuzzer.ExperienceFuzzer;
import io.resttestgen.implementation.fuzzer.IntensificationFuzzer;
import io.resttestgen.implementation.fuzzer.NominalFuzzer;
import io.resttestgen.implementation.operationssorter.DeepReinforcementLearningOperationsSorter;
import io.resttestgen.implementation.operationssorter.RandomOperationsSorter;
import io.resttestgen.implementation.strategy.configuration.DeepReinforcementLearningStrategyConfiguration;
import io.resttestgen.implementation.writer.ReportWriter;
import io.resttestgen.implementation.writer.RestAssuredWriter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.HashSet;

@SuppressWarnings("unused")
public class DeepReinforcementLearningStrategy extends Strategy {

    private static final Logger logger = LogManager.getLogger(DeepReinforcementLearningStrategy.class);

    private final DeepReinforcementLearningStrategyConfiguration config =
            StrategyConfiguration.loadConfiguration(DeepReinforcementLearningStrategyConfiguration.class);

    HashSet<Operation> intensificatedOperations = new HashSet<>();

    @Override
    public void start() {

        DeepReinforcementLearningProxy.initializeDeepReinforcementLearning(config.getNamedPipesPath(),
                Environment.getInstance().getOpenAPI().getOperations().size());

        OperationsSorter sorter = new DeepReinforcementLearningOperationsSorter();

        // If DRL is disabled, use random sorter
        if (config.isDisableDrl()) {
            sorter = new RandomOperationsSorter();
        }

        while (!sorter.isEmpty()) {

            Operation operationToTest = sorter.getFirst();

            logger.debug("Testing operation {}", operationToTest);
            TestSequence nominalSequence;
            if (config.getFuzzer().equals("experience")) {
                ExperienceFuzzer experienceFuzzer = new ExperienceFuzzer(operationToTest);
                nominalSequence = experienceFuzzer.generateTestSequences(1).get(0);
            } else {
                NominalFuzzer nominalFuzzer = new NominalFuzzer(operationToTest);
                nominalSequence = nominalFuzzer.generateTestSequences(1).get(0);
            }

            TestRunner.getInstance().run(nominalSequence);
            HttpStatusCode statusCode = nominalSequence.get(0).getResponseStatusCode();
            if (statusCode == null) {
                logger.warn("Found NULL status code");
                // Fallback
                statusCode = new HttpStatusCode(400);
            }

            StatusCodeOracle statusCodeOracle = new StatusCodeOracle();
            statusCodeOracle.assertTestSequence(nominalSequence);

            // Write report to file
            try {
                ReportWriter reportWriter = new ReportWriter(nominalSequence);
                reportWriter.write();
                RestAssuredWriter restAssuredWriter = new RestAssuredWriter(nominalSequence);
                restAssuredWriter.write();
            } catch (IOException e) {
                logger.warn("Could not write report to file.");
                e.printStackTrace();
            }

            DeepReinforcementLearningProxy.sendResult(statusCode);

            // In case of successful interaction, invoke intensification testing, but only the first time, and then with
            // a low probability. Of course, only if intensification is enabled by configuration
            if (statusCode.isSuccessful() && config.isIntensification()) {
                if (!intensificatedOperations.contains(operationToTest) || (Environment.getInstance().getRandom().nextInt(0, 100) < config.getIntensificationProbability())) {

                    logger.info("Performed successful interaction. Starting intensification for {}.", operationToTest);

                    IntensificationFuzzer intensificationFuzzer = new IntensificationFuzzer(nominalSequence);
                    intensificationFuzzer.generateTestSequences(0); // Input to this method is currently ignored

                    intensificatedOperations.add(operationToTest);

                    logger.info("Intensification completed. Continuing with testing.");
                }
            }

            sorter.removeFirst();
        }
    }
}
