package postly.example.postly.aspects;

import jakarta.annotation.PostConstruct;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import postly.example.postly.exceptions.InvalidRequestException;
import postly.example.postly.services.VisitCounterService;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    private final VisitCounterService visitCounterService;

    @Autowired
  public LogAspect(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @Pointcut("within(postly.example.postly.controllers..*)")
  public void controllerMethods() {}

    @Before("controllerMethods()")
  public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String timestamp = dateFormat.format(new Date());

        String url = getRequestUrl();
        if (url != null) {
            visitCounterService.incrementVisit(url);
        }

        String message = String.format("%s INFO: Метод %s вызван", timestamp, methodName);
        if (args.length > 0) {
            message += " с параметрами: " + Arrays.toString(args);
        }
        logger.info(message);
    }

    @AfterThrowing(pointcut = "controllerMethods()", throwing = "exception")
  public void logError(JoinPoint joinPoint, Exception exception) {
        String methodName = joinPoint.getSignature().getName();
        String timestamp = dateFormat.format(new Date());
        String errorType = exception instanceof InvalidRequestException ? "Ошибка 400" : "Ошибка";

        String message = String.format("%s ERROR: %s в методе %s: %s",
            timestamp, errorType, methodName, exception.getMessage());
        logger.error(message);
    }

    private String getRequestUrl() {
        try {
            ServletRequestAttributes attributes =
                (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                return attributes.getRequest().getRequestURI();
            }
        } catch (Exception e) {
            logger.error("Ошибка получения URL запроса: {}", e.getMessage());
        }
        return null;
    }

    @PostConstruct
    public void init() {
        System.out.println("LogAspect создан: " + this);
    }
}