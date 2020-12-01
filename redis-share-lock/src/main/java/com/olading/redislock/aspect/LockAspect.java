package com.olading.redislock.aspect;

import com.olading.redislock.annotion.Lock;
import com.olading.redislock.exception.LockException;
import com.olading.redislock.lock.DistributedLock;
import com.olading.redislock.lock.OladingLock;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.LocalVariableTableParameterNameDiscoverer;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.Expression;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Optional;

/**
 * 分布式锁切面
 */
@Slf4j
@Aspect
@Component
@ConditionalOnProperty(prefix = "olading.lock", name = "enable", havingValue = "true",matchIfMissing = true)
public class LockAspect {

    @Autowired
    private DistributedLock locker;


    @Around("@within(lock) || @annotation(lock)")
    public Object aroundLock(ProceedingJoinPoint point, Lock lock) throws Throwable {
        if (lock == null) {
            // 获取类上的注解
            lock = point.getTarget().getClass().getDeclaredAnnotation(Lock.class);
        }
        String lockKey = lock.key();

        Optional.ofNullable(locker).orElseThrow(()->new LockException("DistributedLock is null"));
        Optional.ofNullable(lockKey).orElseThrow(()->new LockException("DistributedLock is null"));
        if (lockKey.contains("#")) {
            lockKey = Optional.ofNullable(getLockKey(point)).orElseThrow(()->new LockException("lockKey is null"));
        }
        OladingLock lockObj = null;
        try {
            lockObj = locker.tryLock(lockKey, lock.waitTime(), lock.leaseTime(), lock.unit(), lock.lockType());
            if (lockObj == null) {
                //如果获取锁失败了，则进入失败的处理逻辑
                log.info("Lock acquisition timeout，LockKey：({}),Enter failure logic processing", lockKey);
                lock.lockTimeoutStrategy().handle(lockKey);
            }
            return point.proceed();
        } finally {
            locker.unlock(lockObj);
        }
    }

    /**
     * 获取spel 定义的参数值
     * @param context 参数容器
     * @param key     key
     * @param clazz   需要返回的类型
     * @param <T>     返回泛型
     * @return 参数值
     */
    private <T> T getValue(EvaluationContext context, String key, Class<T> clazz) {
        SpelExpressionParser spelExpressionParser = new SpelExpressionParser();
        Expression expression = spelExpressionParser.parseExpression(key);
        return expression.getValue(context, clazz);
    }

    /**
     * 获取参数容器
     * @param arguments       方法的参数列表
     * @param signatureMethod 被执行的方法体
     * @return 装载参数的容器
     */
    private EvaluationContext getContext(Object[] arguments, Method signatureMethod) {

        String[] parameterNames = new LocalVariableTableParameterNameDiscoverer().getParameterNames(signatureMethod);
        Optional.ofNullable(parameterNames).orElseThrow(()->new LockException("Parameter list cannot be null"));
        EvaluationContext context = new StandardEvaluationContext();
        for (int i = 0; i < arguments.length; i++) {
            context.setVariable(parameterNames[i], arguments[i]);
        }
        return context;
    }


    /**
     * 获取key值
     * @param point
     * @return
     */
    private String getLockKey(ProceedingJoinPoint point){
        MethodSignature signature = (MethodSignature) point.getSignature();
        Method signatureMethod = signature.getMethod();
        Lock lock = signatureMethod.getAnnotation(Lock.class);
        String value =null;
        if (lock != null) {
            EvaluationContext context = getContext(point.getArgs(), signature.getMethod());
            value = getValue(context, lock.key(), String.class);
        }
        return value;
    }
}
