package org.dorkmaster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class MinecraftServer {
    record Instance(Process process, PrintStream out) {
    }

    protected static String serverDirectory = ".";
    protected RingBuffer<String> logBuffer = new RingBuffer<>(500);
    protected Logger logger = LoggerFactory.getLogger(MinecraftServer.class);
    protected ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();

    protected volatile Instance instance;

    public boolean startServer() throws IOException {
        if (null != instance) {
            return false;
        }

        ProcessBuilder processBuilder = new ProcessBuilder("java", "-jar", "fabric-server-mc.1.21.1-loader.0.17.2-launcher.1.1.0.jar", "-nogui");
        logger.info("Starting with command '{}'", processBuilder.toString());
        processBuilder.redirectInput(ProcessBuilder.Redirect.PIPE);
        processBuilder.redirectOutput(ProcessBuilder.Redirect.PIPE);

        Process process = processBuilder.start();
        InputStream inputStream = process.getInputStream();
        PrintStream out = new PrintStream(process.getOutputStream());

        executor.submit(() -> {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    logger.info("MCS: {}", line);
                    logBuffer.add(line);
                }
            } catch (IOException e) {
                logger.warn("Error reading from child process: " + e.getMessage());
            }
        });

        instance = new Instance(process, out);
        return true;
    }

    public List<String> tail(int cnt){
        return logBuffer.tail(cnt);
    }

    public synchronized void say(String command) {
        instance.out.println(command);
        instance.out.flush();
    }

    public int stopServer() throws InterruptedException {
        if (null != instance) {
            logger.info("Stopping the server");
            say("/stop");

            try {
                if (instance.process.waitFor(1, TimeUnit.MINUTES)) {
                    return instance.process.exitValue();
                } else {
                    logger.warn("Server did not shutdown as expected, forcing the process to stop");
                    instance.process.destroyForcibly();
                    throw new RuntimeException("Server did not shutdown as expected, forcing the process to stop");
                }
            } finally {
                instance = null;
            }
        }
        return -1000;
    }
}