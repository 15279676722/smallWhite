package com.example.smallwhite.utils;

import lombok.extern.slf4j.Slf4j;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 对数据库增删改查操作
 * @author: yangqiang
 * @create: 2020-03-24 19:35
 */
@Slf4j
public class JdbcUtil {

    private String driver;
    private String url;
    private String username;
    private String password;
    private Connection connection;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private ResultSet resultSet;
    public JdbcUtil(String url, String driver, String username, String password) {
        log.info(driver + "," + url + "," + username + "," + password);
        this.driver = driver;
        this.url = url;
        this.username = username;
        this.password = password;
        this.Connect();
    }

    /**
     * 加载数据库驱动
     */
    private void Connect() {
        try {
            //1.加载jdbc驱动类
            Class.forName(this.driver);
            //2.获取远程mysql连接（url（主要用到的是ip+port+databaseName），user，password（主要用到的是创建的可远程访问的用户的用户名和密码））
            connection = DriverManager.getConnection(url, username, password);
//            connection.setAutoCommit(false);
            //3.开启sql称述，（Statement，PreparedStatement（？占位符形式）），执行sql语句
            statement = connection.createStatement();
        } catch (ClassNotFoundException e) {
            log.error("Load driver Error");
            e.printStackTrace();
        } catch (SQLException e) {
            log.error("connection mysql database Error");
            e.printStackTrace();
        }
    }

    /**
     * 根据完整的sql语句查询实体VO数据,返回值为VO类型
     * 可用于联表查询，但需要创建返回类型VO
     *
     * @param sql   整体sql
     * @param clazz 返回的实体VO.class
     * @return 实体数据集合
     * @throws Exception
     */
    public <T> List<T> query(String sql, Class<T> clazz) {
        // 创建一个对应的空的泛型集合
        List<T> list = new ArrayList<T>();
        try {
            T object;

            preparedStatement = connection.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();
            Field[] fs = clazz.getFields();
            while (resultSet.next()) {
                // 创建实例
                object = (T) clazz.newInstance();
                // 赋值
                for (int i = 0; i < fs.length; i++) {
                    /*
                     * fs[i].getName()：获得字段名
                     *
                     * f:获得的字段信息
                     */
                    Field f = fs[i];
                    // 参数true 可跨越访问权限进行操作
                    f.setAccessible(true);
                    /*
                     * f.getType().getName()：获得字段类型的名字
                     */
                    // 判断其类型进行赋值操作
//                    if (f.getType().getName().equals(String.class.getName())) {
//                        f.set(object, resultSet.getString(fs[i].getName()));
//                    } else if (f.getType().getName().equals(int.class.getName()) || f.getType().getName().equals(Integer.class.getName())) {
//                        f.set(object, resultSet.getInt(fs[i].getName()));
//                    } else if (f.getType().getName().equals(Timestamp.class.getName())) {
//                        f.set(object, resultSet.getTimestamp(fs[i].getName()));
//                    } else if (f.getType().getName().equals(Float.class.getName())){
//                        f.set(object,resultSet.getFloat(fs[i].getName()));
//                    }
                    f.set(object,resultSet.getObject(fs[i].getName()));

                }
                list.add(object);
            }
        } catch (SQLException e) {
            log.error("query Error! ");
            e.printStackTrace();
        } catch (IllegalAccessException | InstantiationException e) {
            e.printStackTrace();
        }
        return list;
    }


    /**
     * 根据vo clazz查询出vo集合所有字段
     * 可用于联表查询，但需要创建返回类型VO
     *
     * @param clazz 返回的实体VO.class
     * @return 实体数据集合
     * @throws Exception
     */
    public <T> List<T> query(Class<T> clazz) {
        String sql = getSelectSql(clazz);
        List<T> list = new ArrayList<>();
        try {
            list = query(sql, clazz);
        } catch (Exception e) {
            throw new BaseBusinessException(e.getMessage());
        }
        return list;
    }

    /**
     * @param clazz
     * @return tablename
     */
    public static <T> String getSelectSql(Class<T> clazz) {
        String sql = null;
        String tableName = null;
        try {
            T t = clazz.newInstance();
            tableName = (String) t.getClass().getMethod("getTableName").invoke(t);
            sql = "SELECT * FROM " + tableName;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }catch (NoSuchMethodException e) {
            throw new BaseBusinessException(ResultCodeEnum.NOT_TABLE);
        }
        return sql;
    }

    /**
     * 批量删除操作 需要在vo中有 getTableName方法
     *
     * @Param: [ids, clazz]
     * @return: boolean
     * @Author: yangqiang
     * @Date: 2020/3/26
     */
    public <T> Integer batchDeleteDr(List<String> ids, Class<T> clazz) {
        try {
            StringBuilder sb = new StringBuilder();
            String tableName = (String) clazz.getMethod("getTableName").invoke(clazz);
            sb.append("UPDATE ");
            sb.append(tableName);
            sb.append(" SET DR = 1 WHERE ID IN (");
            ids.forEach(id -> {
                sb.append("?,");
            });
            //            connection.setAutoCommit(false);

            sb.delete(sb.length() - 1, sb.length());
            sb.append(")");
            log.info(sb.toString());
            preparedStatement = connection.prepareStatement(sb.toString());
            for (int i = 1; i <= ids.size(); i++) {
                preparedStatement.setObject(i, ids.get(i));
            }
            int index = preparedStatement.executeUpdate();
            log.info("删除成功删除{}条数据",index);
            return index;
        } catch (IllegalAccessException | InvocationTargetException | SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new BaseBusinessException(ResultCodeEnum.NOT_TABLE);
        }
        return 0;
    }

    /**
     * 删除操作 需要在vo中有 getTableName方法
     *
     * @Param: [id, clazz]
     * @return: boolean 是否成功
     * @Author: yangqiang
     * @Date: 2020/3/26
     */
    public <T> Integer deleteDr(String id, Class<T> clazz) {
        try {
            StringBuilder sb = new StringBuilder();
            String tableName = (String) clazz.getMethod("getTableName").invoke(clazz);
            sb.append("UPDATE ");
            sb.append(tableName);
            sb.append(" SET DR = 1 WHERE ID IN (");
            sb.append("?,");
            sb.delete(sb.length() - 1, sb.length());
            sb.append(")");
            log.info(sb.toString());
            preparedStatement = connection.prepareStatement(sb.toString());
            preparedStatement.setObject(1, id);
            Integer index = preparedStatement.executeUpdate();
            log.info("删除成功删除{}条数据",index);
            return index;
        } catch (IllegalAccessException | InvocationTargetException | SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new BaseBusinessException(ResultCodeEnum.NOT_TABLE);
        }
        return 0;
    }

    /**
     * @Param: [ids, clazz]
     * @return: boolean
     * @Author: yangqiang
     * @Date: 2020/3/26
     */
    public <T> Integer batchDelete(List<String> ids, Class<T> clazz) {
        try {
            String tableName = (String) clazz.getMethod("getTableName").invoke(clazz);
            StringBuilder sb = new StringBuilder();
            sb.append("DELETE FROM  ");
            sb.append(tableName);
            sb.append(" WHERE ID IN (");
            ids.forEach(id -> {
                sb.append("?,");
            });
            sb.delete(sb.length() - 1, sb.length());
            sb.append(")");
            log.info(sb.toString());
            preparedStatement = connection.prepareStatement(sb.toString());
            for (int i = 1; i <= ids.size(); i++) {
                preparedStatement.setObject(i, ids.get(i));
            }
            Integer index = preparedStatement.executeUpdate();
            log.info("删除成功删除{}条数据",index);
            return index;
        } catch (IllegalAccessException | InvocationTargetException | SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new BaseBusinessException(ResultCodeEnum.NOT_TABLE);
        }
        return 0;
    }
    /**
     *
     * @Param: [list]
     * @return: boolean
     * @Author: yangqiang
     * @Date: 2020/3/26
     */
    public <T> Integer batchDelete(List<T> list) {
        if (list == null || list.size() == 0){
            return 0;
        }
        try {
            T t = list.get(0);
            String tableName = (String) t.getClass().getMethod("getTableName").invoke(t);
            StringBuilder sb = new StringBuilder();
            sb.append("DELETE FROM  ");
            sb.append(tableName);
            sb.append(" WHERE ID IN (");
            for (int i = 1; i <= list.size(); i++) {
                sb.append("?,");
            }
            sb.delete(sb.length() - 1, sb.length());
            sb.append(")");
            log.info(sb.toString());
            preparedStatement = connection.prepareStatement(sb.toString());
            for (int i = 1; i <= list.size(); i++) {
                T t1 = list.get(i-1);
                Method getId = t1.getClass().getMethod("getId");
                preparedStatement.setObject(i,getId.invoke(t1));
            }
            Integer index = preparedStatement.executeUpdate();
            log.info("删除成功删除{}条数据",index);
            return index;
        } catch (IllegalAccessException | InvocationTargetException | SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new BaseBusinessException(ResultCodeEnum.NOT_TABLE);
        }
        return 0;
    }
    /**
     * @Param: [ids, clazz]
     * @return: boolean
     * @Author: yangqiang
     * @Date: 2020/3/26
     */
    public <T> Integer delete(String id, Class<T> clazz) {
        try {
            T t = clazz.newInstance();
            String tableName = (String) clazz.getMethod("getTableName").invoke(t);
            StringBuilder sb = new StringBuilder();
            sb.append("DELETE FROM  ");
            sb.append(tableName);
            sb.append(" WHERE ID IN (?)");
            log.info(sb.toString());
            preparedStatement = connection.prepareStatement(sb.toString());
            preparedStatement.setObject(1, id);
            Integer index = preparedStatement.executeUpdate();
            log.info("删除成功删除{}条数据",index);
            return index;
        } catch (IllegalAccessException | InstantiationException | InvocationTargetException | SQLException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new BaseBusinessException(ResultCodeEnum.NOT_TABLE);
        }
        return 0;
    }


    /**
     *   批量新增vo数据
     * @Param: [list]
     * @return: java.lang.Integer
     * @Author: yangqiang
     * @Date: 2020/3/26
     */
    public  synchronized <T> Integer insert(List<T> list) {
        if (list == null || list.size() == 0){
            return 0;
        }
        Map<String, Object> map = getInsertSql(list);
        String sql = (String) map.get("sql");
        log.info("执行sql===》》》" + sql);
        try {
            Long startMs = System.currentTimeMillis();
            preparedStatement = connection.prepareStatement(sql);
            Map valueMap = (Map<Object, Object>) map.get("valuemap");
            for (int i = 1; i <= valueMap.size(); i++) {
                preparedStatement.setObject(i, String.valueOf(valueMap.get(i)));
            }
            int index = preparedStatement.executeUpdate();
            Long endMs = System.currentTimeMillis();

            log.info("新增成功===》新增{}条数据，执行时间{}ms", index,endMs-startMs);
            return index;
        } catch (SQLException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据传入的list集合 拼接新增的sql
     */
    private <T> Map<String, Object> getInsertSql(List<T> list) {
        Map<String, Object> remaps = new HashMap<>(2);
        if (list == null || list.size() == 0) {
            throw new BaseBusinessException("没有数据可新增！");
        }
        StringBuilder sql = new StringBuilder();
        sql.append("INSERT INTO ");
        Map<Object, Object> valueMap = new HashMap<>(100);
        try {
            T t = list.get(0);
            Object getTableName = t.getClass().getMethod("getTableName").invoke(t);
            sql.append(getTableName);
            sql.append("(");
            StringBuilder fieldNames = new StringBuilder();
            List<String> fieldValues = new ArrayList<>();
            Field[] fields = t.getClass().getFields();
            int flag = 0;
            Integer valueFlag = 0;
            for (T vo : list) {
                StringBuilder fieldValue = new StringBuilder("(");
                Class<?> aClass = vo.getClass();
                Method[] methods = aClass.getMethods();
                flag++;
                for (Method method : methods) {
                    //为每个set方法赋值
                    if ("get".equals(method.getName().substring(0, 3)) && !"getTableName".equals(method.getName()) && !"getClass".equals(method.getName())) {

                        Class<?> clazzArray = method.getReturnType();
                        // insert into 字段属性只加一次
                        if (flag == 1) {
                            for (Field field : fields) {
                                if (method.getName().substring(3, method.getName().length()).toLowerCase().equals(field.getName().toLowerCase())) {
                                    fieldNames.append(field.getName()).append(",");
                                }
                            }
                        }
                        valueFlag++;
                        if("java.util.Date".equals(method.getGenericReturnType().getTypeName())){
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            valueMap.put(valueFlag, simpleDateFormat.format(method.invoke(vo)));
                        }else{
                            valueMap.put(valueFlag, method.invoke(vo));
                        }
                        fieldValue.append("?,");
                    }
                }
                fieldValue = new StringBuilder(fieldValue.length() > 0 ? fieldValue.substring(0, fieldValue.length() - 1) : fieldValue.toString());
                fieldValue.append("),");
                fieldValues.add(fieldValue.toString());
            }
            fieldNames = new StringBuilder(fieldNames.length() > 0 ? fieldNames.substring(0, fieldNames.length() - 1) : fieldNames.toString());
            sql.append(fieldNames);
            sql.append(") ");
            sql.append(" VALUES ");
            for (String fieldValue : fieldValues) {
                sql.append(fieldValue);
            }
            sql.deleteCharAt(sql.length() - 1);
        } catch (NoSuchMethodException e) {
            throw new BaseBusinessException(ResultCodeEnum.NOT_TABLE);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        remaps.put("sql", sql.toString());
        remaps.put("valuemap", valueMap);
        return remaps;
    }


    /**
     *
     * @Param: [list]
     * @return: boolean
     * @Author: yangqiang
     * @Date: 2020/3/26
     */
    public <T> Integer batchUpdate(List<T> list) {
        if (list == null || list.size() == 0){
            return 0;
        }
        try {
            Map<String, Object> map = getUpdateSql(list);
            String sql = (String) map.get("sql");
            log.info("执行sql===》》》" + sql);
            Long startMs = System.currentTimeMillis();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement(sql);
            Map valueMap = (Map<Object, Object>) map.get("valuemap");
            for (int j = 1 ; j <= list.size() ; j ++) {
                for (int i = 1; i <= valueMap.size() / list.size(); i++) {
                    preparedStatement.setObject(i, String.valueOf(valueMap.get(valueMap.size()*(j-1)/ list.size()+i)));
                }
                preparedStatement.addBatch();
            }
            int[] index = preparedStatement.executeBatch();
            connection.commit();
            log.info("修改成功===》修改{}条数据，执行时间{}ms", index.length,System.currentTimeMillis()-startMs);
            return index.length;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 根据传入的list集合 拼接修改的sql
     */
    private <T> Map<String, Object> getUpdateSql(List<T> list) {
        Map<String, Object> remaps = new HashMap<>(2);
        if (list == null || list.size() == 0) {
            throw new BaseBusinessException("没有数据可修改！");
        }
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE ");
        Map<Object, Object> valueMap = new HashMap<>(100);
        try {
            T t = list.get(0);
            Object tableName = t.getClass().getMethod("getTableName").invoke(t);
            sql.append(tableName);
            sql.append(" SET ");
            Field[] fields = t.getClass().getFields();
            int flag = 0;
            Integer valueFlag = 0;
            for (T vo : list) {
                String fieldValue = "(";
                Class<?> aClass = vo.getClass();
                Method[] methods = aClass.getMethods();
                flag++;
                for (Method method : methods) {
                    //为每个set方法赋值
                    if ("get".equals(method.getName().substring(0, 3)) && !"getTableName".equals(method.getName()) && !"getClass".equals(method.getName())) {

                        Class<?> clazzArray = method.getReturnType();
                        // insert into 字段属性只加一次
                        if (flag == 1) {
                            for (Field field : fields) {
                                if (method.getName().substring(3).toLowerCase().equals(field.getName())) {
                                    sql.append(field.getName()).append("=?,");
                                }
                            }
                        }
                        valueFlag++;
                        valueMap.put(valueFlag, method.invoke(vo));
                    }

                }
                valueFlag++;
                valueMap.put(valueFlag, vo.getClass().getMethod("getId").invoke(vo));
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(" WHERE id=? ");
        } catch (NoSuchMethodException e) {
            throw new BaseBusinessException(ResultCodeEnum.NOT_TABLE);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        remaps.put("sql", sql.toString());
        remaps.put("valuemap", valueMap);
        return remaps;
    }
    /**
     * 关闭数据库连接
     */
    private void close() {
        try {
            if (connection != null) {
                connection.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (resultSet != null) {
                resultSet.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }
}
