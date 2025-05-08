package postly.example.postly.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import postly.example.postly.models.VisitStats;
import postly.example.postly.services.VisitCounterService;

@RestController
@Tag(name = "Статистика посещений", description = "Учет и просмотр статистики посещений URL")
public class VisitCounterController {

    private final VisitCounterService visitCounterService;

    @Autowired
  public VisitCounterController(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @GetMapping("/stats/visits")
    @Operation(summary = "Получить статистику всех посещений",
        description = "Возвращает список всех URL и количество их посещений.")
    public List<VisitStats> getAllVisitStats() {
        return visitCounterService.getAllVisitStats();
    }

    @GetMapping("/stats/visits/url")
    @Operation(summary = "Получить статистику посещений для URL",
        description = "Возвращает количество посещений для указанного URL.")
    public long getVisitCount(@RequestParam String url) {
        return visitCounterService.getVisitCount(url);
    }
}