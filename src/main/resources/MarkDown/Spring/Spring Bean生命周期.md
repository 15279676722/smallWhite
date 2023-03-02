### 1.加载beanDefinition  

1.通过BeanDefinitionReader 将xml中配置的bean进行解析 成 beanFactory中的beanDefinitionMap(发生在创建beanFactory之后)

2.或者通过java config配置的@Bean 定义解析成beanFactory中的beanDefinitionMap(发生在执行BeanFactoryPostProcessor的回调过程 具体点就是BeanDefinitionRegistryPostProcessor 的postProcessBeanDefinitionRegistry方法)

> xml 配置 同一个xml文件 id不重复 多个xml文件中允许重复会覆盖
>
> java配置中 同一个@Configuration中允许重复 后面的覆盖前面的 

### 2.bean的实例化 

1.实例化前可以通过 `InstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation` 或者 实现`SmartInstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`生成一个完整的对象或者生成一个代理对象 从而避开Spring bean完整的生命周期 只会执行 BeanPostProcessor的后置初始化方法。

> 1.生成完整对象的方式 自己实现`SmartInstantiationAwareBeanPostProcessor#postProcessBeforeInstantiation`返回一个对象
>
> 2.生成代理对象的方式 启用AOP功能`@EnableAspectJAutoProxy` 实现TargetSource 去返回一个代理对象

1.构造方法

- 匹配构造方法 默认无参构造    AutowiredAnnotationBeanPostProcessor#determineCandidateConstructors 匹配构造方法 优先匹配@Autowired修饰的构造方法，一个bean对象只能有一个@Autowired修饰的构造方法 出现多个会报错(如果是被@Lookup 或者<replace-method> 修饰过的则会利用CgLib生成代理对象)

  > 也可以自定义实现`SmartInstantiationAwareBeanPostProcessor#determineCandidateConstructors`指定使用的构造方法 而不受Spring的限制

- 根据匹配到的构造方法来实例化对象。如果是默认的无参构造方法 利用反射创建一个bean对象

  如果是@Autowired修饰的构造方法 或者 是xml中配置的<constructor-arg> 配置的构造器参数   就会提前进行依赖注入操作

### 3.bean的初始化 

bean实例化完成后 在初始化bean之前还有两个个步骤 

**解析 bean里面的注解 **

> `CommonAnnotationBeanPostProcessor#postProcessorMergeBeanDefinition` 解析对应的`@PostConstruct` ` @PreDestroy` ` @Resource` 注解元数据
>
> `AutowiredAnnotationBeanPostProcessor#postProcessorMergeBeanDefinition` 解析对应的
>
> `@Autowired` `@Value` `@Inject` 注解元数据
>
> `ApplicationListenerDetector#postProcessorMergeBeanDefinition` 判断bean是不是ApplicationListener类型bean

**如果开启了允许循环依赖**

> 把当前bean加入到三级缓存中

**实例化后扩展**

`InstantiationAwareBeanPostProcessor#postProcessAfterInstantiation`

> 此时可以得到一个 刚刚实例化完成的对象和beanName
>
> 我们可以按照自己的需求进行扩展
>
> 当返回值为ture时 进行属性填充，返回值为false时结束属性填充过程(populateBean方法)



#### 3.1 开始属性填充

##### 1.注入方式

> spring对bean的字段属性的赋值支持自动注入的方式 `@Bean(autowire= Autowire.BY_TYPE)`
>
> 此属性现在已被废弃 这样的话我们不需要对字段设置任何的注入方法也可以实现依赖注入

