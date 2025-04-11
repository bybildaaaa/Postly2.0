package postly.example.postly.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Tag(name = "Логи", description = "Просмотр логов приложения")
public class LogController {

    private static final String LOG_FILE_PATH = "application.log";

    @GetMapping("/logs")
    @Operation(summary = "Получить логи", description = "Возвращает строки логов из файла. Можно указать дату в формате dd-MM-yyyy для фильтрации.")
  public List<String> getLogsForDate(@RequestParam(required = false) String date) {
        List<String> logs = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(LOG_FILE_PATH,
            StandardCharsets.UTF_8))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (date == null || matchesDate(line, date)) {
                    logs.add(line);
                }
            }
        } catch (IOException e) {
            logs.add("Ошибка при чтении логов: " + e.getMessage());
        }

        return logs;
    }

    private static boolean matchesDate(String logLine, String date) {
        try {
            if (logLine.length() >= 10) {
                String logDatePart = logLine.substring(0, 10); // "01-04-2025"
                return logDatePart.equals(date);
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }
}