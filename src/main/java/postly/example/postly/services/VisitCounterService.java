package postly.example.postly.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import postly.example.postly.models.VisitStats;

@Service
public class VisitCounterService {

    private static final Logger logger = LoggerFactory.getLogger(VisitCounterService.class);
    private final Map<String, AtomicLong> visitCounts = new ConcurrentHashMap<>();

    public void incrementVisit(String url) {
        visitCounts.computeIfAbsent(url, k -> new AtomicLong(0)).incrementAndGet();
        logger.debug("Посещение URL {}: общее количество = {}", url, visitCounts.get(url).get());
    }

    public List<VisitStats> getAllVisitStats() {
        List<VisitStats> stats = new ArrayList<>();
        visitCounts.forEach((url, count) -> stats.add(new VisitStats(url, count.get())));
        logger.info("Получена статистика посещений: {} записей", stats.size());
        return stats;
    }

    public long getVisitCount(String url) {
        AtomicLong count = visitCounts.get(url);
        long result = count != null ? count.get() : 0;
        logger.debug("Запрошена статистика для URL {}: {}", url, result);
        return result;
    }
}