package io.resttestgen.core.helper;

import io.resttestgen.core.datatype.HttpStatusCode;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

public class DeepReinforcementLearningProxy {

    private static final Logger logger = LogManager.getLogger(DeepReinforcementLearningProxy.class);

    private static String P2J_PIPE = "/p2j";
    private static String J2P_PIPE = "/j2p";

    public static void initializeDeepReinforcementLearning(@NotNull String namedPipesPath, @NotNull Integer numOperations) {
        P2J_PIPE = namedPipesPath + P2J_PIPE;
        J2P_PIPE = namedPipesPath + J2P_PIPE;
        try {
            FileOutputStream fos = new FileOutputStream(J2P_PIPE);
            fos.write(numOperations.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }

    @NotNull
    public static Integer getAction() {
        try {
            FileInputStream fis = new FileInputStream(P2J_PIPE);
            byte[] content = new byte[4];
            fis.read(content, 0, 4);
            fis.close();
            String nextActionString = new String(content);
            return Integer.parseInt(nextActionString);
        } catch (IOException e) {
            logger.error(e);
            return 0;
        }
    }

    public static void sendResult(@NotNull HttpStatusCode statusCode) {
        try {
            FileOutputStream fos = new FileOutputStream(J2P_PIPE);
            fos.write(statusCode.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            logger.error(e);
        }
    }
}

