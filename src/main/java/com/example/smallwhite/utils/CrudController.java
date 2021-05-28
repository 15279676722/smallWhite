package com.example.smallwhite.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * @author: yangqiang
 * @create: 2020-03-26 20:05
 */
@Slf4j
@PropertySource(value = {"classpath:application.yml", "classpath:application.properties"})//制定读取配置文件的路径
@RestController
@RequestMapping("/crud")
public class CrudController {
    @Autowired
    JdbcUtil jdbcUtil;
    @Value("${entity-package}")
    private String PACKAGE_NAME;
    @RequestMapping(value = {"/{operation}/{classname}"})
    public ResultData crudMethod(@PathVariable("operation") String operation, @PathVariable("classname") String classname, @RequestBody AcceptData data) {
        log.info("正在执行{}操作,操作对象为{}", OperationEnum.getName(operation), classname);
        Object retObj = crudOpeation(data, operation, classname);
        return ResultData.ok().data(classname, retObj);
    }
    private Object crudOpeation(AcceptData data, String operation, String classname) {
        Object retObj = null;
        try {
            Class<?> aClass = Class.forName(PACKAGE_NAME + classname);
            List<?> list = mapToDTO(data, aClass);
            switch (operation) {
                case "insert":
                    retObj = jdbcUtil.insert(list);
                    break;
                case "delete":
                    retObj = jdbcUtil.batchDelete(list);
                    break;
                case "update":
                    retObj = jdbcUtil.batchUpdate(list);
                    break;
                case "select":
                    retObj = jdbcUtil.query(aClass);
                    break;
                default: break;
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return retObj;
    }

    private <T> List<T> mapToDTO(AcceptData data, Class<T> aClass) {
        if (data == null || data.getData() == null || data.getData().size() == 0) {
            return null;
        }
        List<T> retList = new ArrayList<>();
        try {
            data.getData().forEach(map -> {
                retList.add((T) parseMapToVo(map, aClass));
            });
            Object o = aClass.newInstance();
            Method[] method = aClass.getMethods();

        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        }

        return retList;
    }

    /**
     * 通过反射机制将map中的key对应value注入到vo中
     *
     * @param map
     * @param cl
     * @return
     * @throws Exception
     */
    public <T> T parseMapToVo(Map<String, Object> map, Class<T> cl) {
        Object obj = null;
        try {

            obj = cl.newInstance();

            Field[] fields = cl.getDeclaredFields();

            Iterator<String> it = map.keySet().iterator();

            while (it.hasNext()) {
                String fieldName = it.next();
                for (Field f : fields) {
                    if (f.getName().equalsIgnoreCase(fieldName)) {
                        // 判断类型是否一致，调用反射注入值，
                        f.setAccessible(true);
                        f.set(obj,new Object[]{TypeConversionUtils.converType(TypeConversionUtils.getType(map.get(fieldName)), f.getType().getName(), map.get(fieldName))}[0]);
                    }
                }
            }
        } catch (Exception e) {
            log.error("ParseUtils parseMapToVo error.");
            log.error(e.getMessage());

        }

        return (T) obj;
    }
}
