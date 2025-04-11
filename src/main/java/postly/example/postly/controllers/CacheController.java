package postly.example.postly.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import postly.example.postly.cashe.CacheService;
import postly.example.postly.models.Post;

@RestController
@Tag(name = "Кэш", description = "Работа с кэшем постов")
public class CacheController {

    private final CacheService<Integer, Post> cacheService;

    @Autowired
  public CacheController(CacheService<Integer, Post> cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/cache/contents")
    @Operation(summary = "Получить содержимое кэша",
        description = "Возвращает текущее содержимое кэша с постами")
  public Map<Integer, Post> getCacheContents() {
        return cacheService.getCacheContents();
    }
}
