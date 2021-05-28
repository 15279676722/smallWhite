package com.example.smallwhite;

import com.example.smallwhite.entity.BabyImage;
import com.example.smallwhite.entity.BeanUser;
import com.example.smallwhite.entity.MysqlIndexVO;
import com.example.smallwhite.service.Calculator;
import com.example.smallwhite.service.Cap10MainConfig;
import com.example.smallwhite.utils.*;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.jdbc.core.JdbcTemplate;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@SpringBootTest
@PropertySource(value = "classpath:application.properties")
@Slf4j
class smallWhiteApplicationTests {
    @Autowired
    Environment env;
    @Value("${test.name}")
    private String name;
    @Value("${test.sex}")
    private Integer sex;
    @Value("${test.type}")
    private String type;
    @Autowired
    JdbcTemplate jdbcTemplate;
    @Autowired
    JdbcUtil jdbcUtil;
    @Autowired
    TestVo testVo;
    @Autowired
    Calculator calculator;
    @Autowired
    JdbcTemplateService service;
    @Autowired
    RedisService redisService;
    @Autowired
    RedisTemplate redisTemplate;

    @Test
    public void test() {
        redisTemplate.opsForValue().set("count",100);
        System.out.println("成功");
    }

    @Test
    public void test2() {

    }

    @Test
    public void test3() {
        ClassPathXmlApplicationContext ac = new ClassPathXmlApplicationContext("springconfig.xml");
        BeanUser beanUser = ac.getBean("beanUser", BeanUser.class);
        log.info(beanUser.getId());
    }

    @Test
    public void test1() {
        AnnotationConfigApplicationContext app = new AnnotationConfigApplicationContext(Cap10MainConfig.class);
        Calculator c = app.getBean(Calculator.class);
        int result = c.div(3);
        System.out.println(result);
        app.close();

    }

    @Test
    public void test33() {

    }

    public static void main(String[] args) {
        printWeekdays();
//        System.out.println(getmindate().toString());
//        System.out.println(getmaxdate().toString());
        Date startDate = getmindate();
        Date endDate = getmaxdate();
        for (int i = 0; i <= (endDate.getTime() - startDate.getTime()) / 1000 / 60 / 60 / 24; i++) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Long ms = i * 60 * 60 * 24 * 1000L;
            System.out.println(dateFormat.format(startDate.getTime() + ms));
        }
    }

    private static final int FIRST_DAY = Calendar.MONDAY;

    private static void printWeekdays() {
        Calendar calendar = Calendar.getInstance();
        setToFirstDay(calendar);
        for (int i = 0; i < 7; i++) {
            printDay(calendar);
            calendar.add(Calendar.DATE, 1);
        }
    }

    private static void setToFirstDay(Calendar calendar) {
        while (calendar.get(Calendar.DAY_OF_WEEK) != FIRST_DAY) {
            calendar.add(Calendar.DATE, -1);
        }
    }

    private static void printDay(Calendar calendar) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        System.out.println(dateFormat.format(calendar.getTime()));
    }

    /**
     * 获取本月第一天
     *
     * @return
     */
    public static Date getmindate() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));

        return calendar.getTime();
    }

    /**
     * 获取本月最后一天
     *
     * @return
     */
    public static Date getmaxdate() {
        Calendar calendar2 = Calendar.getInstance();
        calendar2.setTime(new Date());
        calendar2.set(Calendar.DAY_OF_MONTH, calendar2.getActualMaximum(Calendar.DAY_OF_MONTH));
        return calendar2.getTime();
    }

    @Test
    public void testPropertiesFile() {
        log.info(name + "," + sex + "," + type);
        log.info(testVo.toString());
        String str = "任意值";
        if ((str.matches("[0-9]+")) && (Integer.parseInt(str) > 0)) {
//如果以上条件成立，则str是正整数，可以将str转为int类型，得到一个正整数n
            int n = Integer.parseInt(str);
            System.out.println(n);
        }

    }

    @Test
    public void testPropertiesEnv() {
        log.info(env.getProperty("test.name"));
        log.info(env.getProperty("test.sex"));
        log.info(env.getProperty("test.type"));
    }

    @Test
    public void testJson() throws JSONException {
        String str = "{\"InvokeType\":\"2\",\"OperationType\":\"1\",\"BillType\":\"1\",\"BillCode\":\"EXP0000004912\",\"BillTitle\":\"測試跨年报销20200417\",\"EmployeeAccount\":\"tian_hai\",\"EndApprovalName\":\"\",\"OACode\":\"\",\"Rate\":\"1\",\"Currency\":\"CNY\",\"Details\":[{\"BelongDeptID\":\"1001A1100000007C4FAK\",\"BudgetAccountCode\":\"\",\"BudgetAccountID\":\"1001A1100000003O5X40\",\"BudgetOrgCode\":\"\",\"BudgetOrgID\":\"0001A4100000000YSZG8\",\"DetailID\":\"1587113597757\",\"Month\":\"12\",\"OABudgetDocNum\":\"\",\"ReimburAmount\":\"20\",\"ReqAmountOrigCurrency\":\"\",\"YSBillID\":\"\",\"YSDetailID\":\"\",\"Year\":\"2019\"}]}";
        JSONObject jsonObject = new JSONObject(str);
        JSONArray array = (JSONArray) jsonObject.get("Details");
        JSONObject detailsObject = (JSONObject) array.get(0);
        log.info(String.valueOf(detailsObject.get("Year")));
    }

    @Test
    public void testAOP() {
        Integer a = 2 ;
        Integer b = 3 ;
        Integer c = null ;
        int i = a * b;
        Integer d = a * b;
        boolean flag = false;
        Integer result = flag ? d : c;
        System.out.println(result);
        Map<String,Boolean> map =  new HashMap<String, Boolean>();
        Boolean bool = false;
        Boolean boo = (map!=null ? map.get("test") : bool);
    }
    @Test
    public void TestJdbcTemplate(){
        List<BabyImage> query = service.query(BabyImage.class);
        System.out.println("11");
    }
    @Test
    public void TestList(){
        List<Object> objectList = new ArrayList<>();
        objectList.add("11");
        objectList.add("22");
        objectList.add("33");
        Iterator<Object> iterator = objectList.iterator();

        while(iterator.hasNext()){
            System.out.println(iterator.next());
        }
        System.out.println(iterator.next());
    }
    @Test
    public void testRedis(){
        redisService.set("name","yangqiang2");
        Object name = redisService.get("name");
        log.info(name.toString());

        BabyImage babyImage1 = redisService.get("user1",BabyImage.class);
        log.info(babyImage1.toString());
        ValueOperations valueOperations = redisTemplate.opsForValue();
        BabyImage babyImage = GetDemoObject.getObject(BabyImage.class);
        valueOperations.set("user",babyImage);
        BabyImage user = (BabyImage) redisTemplate.opsForValue().get("user");
        System.out.println(user.toString());

    }
    @Test
    public void testMysqlIndex(){
        Integer id = 110002;
        for (int j = 0; j < 100; j++) {
            ArrayList<MysqlIndexVO> voList = new ArrayList<>();
            for (int i = 0; i < 10000; i++) {
                MysqlIndexVO mysqlIndexVO = new MysqlIndexVO();
                mysqlIndexVO.setTransaction_id(UUID.randomUUID().toString());
                mysqlIndexVO.setGross(1);
                mysqlIndexVO.setNet(2);
                mysqlIndexVO.setStock_id(123);
                mysqlIndexVO.setId(id++);
                voList.add(mysqlIndexVO);
            }
            jdbcUtil.insert(voList);
        }

    }
    @Test
    public void AnnoMybatisTest(){
        Integer insert = jdbcUtil.insert(GetDemoObject.getObjectList(BabyImage.class, 10));
//        List<BabyImage> query = mybatisUtils.query(BabyImage.class);
//        List<BabyImage> delete = mybatisUtils.delete(query.get(0));
    }
    @Test
    public void add(){
        long l = System.currentTimeMillis();
        List<BabyImage> objectList = GetDemoObject.getObjectList(BabyImage.class, 30000);
        Integer insert = jdbcUtil.insert(objectList);
        log.info("耗时:{}",System.currentTimeMillis()-l);
    }
    @Test
    public void addThread(){
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(3, 3, 0, TimeUnit.SECONDS, new LinkedBlockingQueue<>());
        CountDownLatch countDownLatch = new CountDownLatch(3);
        long l = System.currentTimeMillis();
        List<BabyImage> objectList = GetDemoObject.getObjectList(BabyImage.class, 30000);
        for (int i = 0; i < 3; i++) {
            int finalI = i;
            threadPoolExecutor.execute(()->jdbcUtil.insert(objectList.subList(finalI *10000, finalI *10000+10000)));
            countDownLatch.countDown();
        }
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("耗时:{}",System.currentTimeMillis()-l);
    }
}
