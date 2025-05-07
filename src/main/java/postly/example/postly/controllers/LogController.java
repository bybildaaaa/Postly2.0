package postly.example.postly.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import postly.example.postly.exceptions.InvalidRequestException;
import postly.example.postly.services.LogService;

@RestController
@Tag(name = "Логи", description = "Просмотр и асинхронное создание логов приложения")
public class LogController {

    private static final String LOG_FILE_PATH = "application.log";
    private final LogService logService;

    @Autowired
    public LogController(LogService logService) {
        this.logService = logService;
    }

    @GetMapping("/logs")
    @Operation(summary = "Получить логи",
        description = "Возвращает строки логов из файла. Можно указать дату"
          + " в формате dd-MM-yyyy для фильтрации (преобразуется в yyyy-MM-dd).")
    public List<String> getLogsForDate(@RequestParam(required = false) String date) {
        List<String> logs = new ArrayList<>();
        String formattedDate = convertDateFormat(date);

        if (date != null && formattedDate == null) {
            throw new InvalidRequestException("Неверный формат даты: "
              + date + ". Ожидается dd-MM-yyyy");
        }

        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE_PATH,
            StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (formattedDate == null || LogService.matchesDate(line, formattedDate)) {
                    logs.add(line);
                }
            }
        } catch (IOException e) {
            logs.add("Ошибка при чтении логов: " + e.getMessage());
        }

        return logs;
    }

    @PostMapping("/logs/create")
    @Operation(summary = "Создать лог-файл",
        description = "Инициирует асинхронное создание лог-файла. Возвращает уникальный "
          + "ID задачи. Файл будет готов через 20 секунд. Дата в формате dd-MM-yyyy.")
    public ResponseEntity<String> createLogFile(@RequestParam(required = false) String date) {
        String formattedDate = convertDateFormat(date);
        if (date != null && formattedDate == null) {
            throw new InvalidRequestException("Неверный формат даты: "
              + date + ". Ожидается dd-MM-yyyy");
        }
        String taskId = UUID.randomUUID().toString();
        logService.startLogFileCreation(taskId, formattedDate);
        return ResponseEntity.ok(taskId);
    }

    @GetMapping("/logs/status/{taskId}")
    @Operation(summary = "Получить статус лог-файла",
        description = "Возвращает статус подготовки лог-файла "
          + "по ID задачи: PENDING, READY или ERROR.")
    public ResponseEntity<String> getLogFileStatus(@PathVariable String taskId) {
        String status = logService.getTaskStatus(taskId);
        if (status == null) {
            return ResponseEntity.badRequest().body("Задача с ID " + taskId + " не найдена");
        }
        return ResponseEntity.ok(status);
    }

    @GetMapping("/logs/file/{taskId}")
    @Operation(summary = "Получить лог-файл",
        description = "Возвращает содержимое лог-файла по ID задачи. Если файл не готов,"
          + " возвращает сообщение о необходимости подождать.")
    public ResponseEntity<List<String>> getLogFile(@PathVariable String taskId) {
        List<String> logs = logService.getLogFile(taskId);
        if (logs == null) {
            return ResponseEntity.badRequest().body(List.of("Задача с ID "
              + taskId + " не найдена"));
        }
        if (logs.isEmpty() && logService.getTaskStatus(taskId).equals("PENDING")) {
            return ResponseEntity.ok(List.of("Файл ещё не готов, пожалуйста, подождите"));
        }
        return ResponseEntity.ok(logs);
    }

    private static String convertDateFormat(String inputDate) {
        if (inputDate == null) {
            return null;
        }
        try {
            SimpleDateFormat inputFormat = new SimpleDateFormat("dd-MM-yyyy");
            SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
            return outputFormat.format(inputFormat.parse(inputDate));
        } catch (ParseException e) {
            return null;
        }
    }
}