package postly.example.postly.controllers;

import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import postly.example.postly.cashe.CacheService;
import postly.example.postly.models.Post;

@RestController
public class CacheController {
    private final CacheService<Integer, Post> cacheService;

    @Autowired
    public CacheController(CacheService<Integer, Post> cacheService) {
        this.cacheService = cacheService;
    }

    @GetMapping("/cache/contents")
  public Map<Integer, Post> getCacheContents() {
        return cacheService.getCacheContents();
    }
}
