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
import org.springframework.stereotype.Component;
import postly.example.postly.exceptions.InvalidRequestException;

@Aspect
@Component
public class LogAspect {
    private static final Logger logger = LoggerFactory.getLogger(LogAspect.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

    @Pointcut("within(postly.example.postly.controllers..*)")
  public void controllerMethods() {}


    @Before("controllerMethods()")
  public void logBefore(JoinPoint joinPoint) {
        String methodName = joinPoint.getSignature().getName();
        Object[] args = joinPoint.getArgs();
        String timestamp = dateFormat.format(new Date());

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

        if (exception instanceof InvalidRequestException) {
            String message = String.format("%s ERROR: Ошибка 400 в методе %s: %s",
                  timestamp, methodName, exception.getMessage());
            logger.error(message);
        } else {
            String message = String.format("%s ERROR: Ошибка в методе %s: %s",
                  timestamp, methodName, exception.getMessage());
            logger.error(message);
        }
    }

    @PostConstruct
  public void init() {
        System.out.println("LogAspect создан: " + this);
    }
}