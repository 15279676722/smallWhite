1. 加载beanDefinition  

   1.通过BeanDefinitionReader 将xml中配置的bean进行解析 成 beanFactory中的beanDefinitionMap(发生在创建beanFactory之后)

   2.或者通过java config配置的@Bean 定义解析成beanFactory中的beanDefinitionMap(发生在执行BeanFactoryPostProcessor的回调过程 具体点就是BeanDefinitionRegistryPostProcessor 的postProcessBeanDefinitionRegistry方法)

   > xml 配置 同一个xml文件 id不重复 多个xml文件中允许重复会覆盖
   >
   > java配置中 同一个@Configuration中允许重复 后面的覆盖前面的 

2. bean的实例化 

   1.构造对象

   - 匹配构造方法 默认无参构造    AutowiredAnnotationBeanPostProcessor#determineCandidateConstructors 匹配构造方法 优先匹配@Autowired修饰的构造方法，一个bean对象只能有一个@Autowired修饰的构造方法 出现多个会报错(如果是被@Lookup 或者<replace-method> 修饰过的则会利用CgLib生成代理对象)

   - 根据匹配到的构造方法来实例化对象。如果是默认的无参构造方法 利用反射创建一个bean对象

     如果是@Autowired修饰的构造方法 或者 是xml中配置的<constructor-arg> 配置的构造器参数   就会提前进行依赖注入操作

3. bean的初始化 

   > bean实例化完成后

   -  执行CommonAnnotationBeanPostProcessor 的

4. 

5. 

6. 

7. 