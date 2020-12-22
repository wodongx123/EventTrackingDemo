package com.wodongx123.eventtrackingdemo.aspect;

import android.util.JsonReader;
import android.util.Log;

import com.wodongx123.eventtrackingdemo.annotation.BaseTracking;
import com.wodongx123.eventtrackingdemo.annotation.TrackingKeyName;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 处理点击事件的Aspect
 */
@Aspect
public class ClickAspect {

    private static final String TAG = "ClickAspect";

    @Pointcut("execution(@com.wodongx123.eventtrackingdemo.annotation.ClickTracking * *(..))")
    public void tracking(){ }

    @Around("tracking()")
    public void executionTracking(ProceedingJoinPoint proceedingJoinPoint){
        // 获取到方法的反射对象，所以我们注解要限制只能用在方法上，不然这里就会类型转换错误报错
        MethodSignature signature = (MethodSignature)proceedingJoinPoint.getSignature();
        Method method = signature.getMethod();

        // 获取到方法所有用到的注解（此处只是获得ClickTracking）
        Annotation[] annotations = method.getAnnotations();
        Log.e(TAG, "executionTracking: " + annotations[0]);

        // 从所有注解（ClickTracking）中，获取到baseTracking
        BaseTracking baseTracking = null;
        for (Annotation annotation : annotations) {
            Class<?> annotationType = annotation.annotationType();
            Log.e(TAG, "executionTracking: " + annotationType.getAnnotation(BaseTracking.class));
            baseTracking = annotationType.getAnnotation(BaseTracking.class);
            if (baseTracking == null)
                break;
        }

        // 如果获取不到BaseTracking，就表示这个方法出了某种问题。就return，执行原来的逻辑
        if (baseTracking == null){
            try{
                proceedingJoinPoint.proceed();
            } catch (Throwable throwable) {
                throwable.printStackTrace();
            }
            return;
        }

        // 获取到id和type，如果你的后端需要更多或者更少的变量，你就再BaseTracking里面改就行
        String type = baseTracking.type();
        String id = baseTracking.id();

        // 获取到参数中我们指定的注解（也就是标记key值的注解）
        Annotation[] annotationsThroughMethodParameter = getAnnotationsThroughMethodParameter(method);

        // 把参数的实体和对应的key值封装成json串
        JSONObject jsonObject = getData(proceedingJoinPoint.getArgs(), annotationsThroughMethodParameter);

        // 处理收集后的数据，处理，这里就直接跑一下
        Log.e(TAG, "eventTracking: " + "type =" + type + "  id =" + id + "  data = " + jsonObject.toString());

        try{
            proceedingJoinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }

    }

    /**
     * 获得所有参数的TrackingKeyName注解，如果没有则为空
     * @param method 方法
     * @return
     */
    public Annotation[] getAnnotationsThroughMethodParameter(Method method){
        // 这个方法有API等级限制，建议不用
        // Parameter[] parameters = method.getParameters();

        // 所有参数的注解，他是一个二维数组，因为每个参数可以附带多个注解
        // 比如 public void test(@aa@bb String name, @cc@dd String password);
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Annotation[] annotations = new Annotation[parameterAnnotations.length];
        for (int i = 0; i < parameterAnnotations.length; i++) {
            for (Annotation annotation : parameterAnnotations[i]) {
                if (annotation instanceof TrackingKeyName)
                    annotations[i] = annotation;
            }
        }
        return annotations;
    }

    /**
     * 把参数和对应的注解打包成k，v形式。封装到json串里面去
     * @param args 所有参数
     * @param annotationsThroughMethodParameter 所有参数对应的TrackingKeyName注解（没有则为空）
     * @return
     */
    public JSONObject getData(Object[] args, Annotation[] annotationsThroughMethodParameter) {
        JSONObject jsonObject = new JSONObject();

        if (annotationsThroughMethodParameter == null || annotationsThroughMethodParameter.length <= 0)
            return null;

        for (int i = 0; i < annotationsThroughMethodParameter.length; i++) {
            Annotation annotation = annotationsThroughMethodParameter[i];
            if (annotation instanceof TrackingKeyName){
                String name = ((TrackingKeyName) annotation).value();
                try {
                    jsonObject.put(name, args[i].toString());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        return jsonObject;
    }


}
