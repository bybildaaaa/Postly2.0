package postly.example.postly.services;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogService {

    private static final Logger logger = LoggerFactory.getLogger(LogService.class);
    private static final String LOG_FILE_PATH = "application.log";
    private static final long PREPARATION_TIME_MS = 20_000;

    private final Map<String, String> taskStatus = new HashMap<>();
    private final Map<String, List<String>> taskResults = new HashMap<>();
    private final Map<String, Long> taskStartTimes = new HashMap<>();

    @Async
    public void startLogFileCreation(String taskId, String date) {
        logger.info("Начало создания лог-файла для taskId: {}, дата: {}", taskId, date);
        taskStatus.put(taskId, "PENDING");
        taskStartTimes.put(taskId, System.currentTimeMillis());

        CompletableFuture.runAsync(() -> {
            try {
                logger.debug("Симуляция подготовки для taskId: {}", taskId);
                Thread.sleep(PREPARATION_TIME_MS);

                List<String> logs = new ArrayList<>();
                try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE_PATH,
                     StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        if (date == null || matchesDate(line, date)) {
                            logs.add(line);
                            logger.trace("Добавлена строка лога для taskId {}: {}", taskId, line);
                        }
                    }
                } catch (IOException e) {
                    logger.error("Ошибка при чтении логов для taskId {}: {}",
                                  taskId, e.getMessage());
                    taskStatus.put(taskId, "ERROR");
                    taskResults.put(taskId, List.of("Ошибка при чтении логов: " + e.getMessage()));
                    return;
                }

                logger.info("Лог-файл для taskId {} готов, найдено {} строк", taskId, logs.size());
                taskStatus.put(taskId, "READY");
                taskResults.put(taskId, logs);
            } catch (InterruptedException e) {
                logger.error("Прерывание при создании лог-файла для taskId {}: {}",
                              taskId, e.getMessage());
                taskStatus.put(taskId, "ERROR");
                taskResults.put(taskId, List.of("Ошибка при создании файла: " + e.getMessage()));
            }
        }).exceptionally(throwable -> {
            logger.error("Необработанная ошибка при создании лог-файла для taskId {}: {}",
                          taskId, throwable.getMessage());
            taskStatus.put(taskId, "ERROR");
            taskResults.put(taskId, List.of("Необработанная ошибка: " + throwable.getMessage()));
            return null;
        });
    }

    public String getTaskStatus(String taskId) {
        String status = taskStatus.get(taskId);
        logger.debug("Проверка статуса для taskId {}: {}", taskId, status);
        return status;
    }

    public List<String> getLogFile(String taskId) {
        if (!taskStatus.containsKey(taskId)) {
            logger.warn("Задача с ID {} не найдена", taskId);
            return null;
        }

        String status = taskStatus.get(taskId);
        if ("PENDING".equals(status)) {
            long startTime = taskStartTimes.getOrDefault(taskId, 0L);
            long elapsedTime = System.currentTimeMillis() - startTime;
            logger.debug("Задача {} в статусе PENDING, прошло {} мс из {} мс",
                         taskId, elapsedTime, PREPARATION_TIME_MS);
            return List.of();
        }

        List<String> result = taskResults.getOrDefault(taskId, List.of());
        logger.debug("Возврат логов для taskId {}, статус: {}, строк: {}",
                      taskId, status, result.size());
        return result;
    }

    public static boolean matchesDate(String logLine, String date) {
        if (logLine == null || date == null) {
            return false;
        }
        try {
            if (logLine.length() >= 10) {
                String logDatePart = logLine.substring(0, 10);
                return logDatePart.matches("\\d{4}-\\d{2}-\\d{2}") && logDatePart.equals(date);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}