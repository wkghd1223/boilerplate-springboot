package com.example.boilerplatespringboot.config.database;

import com.zaxxer.hikari.HikariDataSource;
import com.example.boilerplatespringboot.common.Constants;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.type.Alias;
import org.aspectj.lang.annotation.Aspect;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.mybatis.spring.SqlSessionTemplate;
import org.mybatis.spring.annotation.MapperScan;
import org.reflections.Reflections;
import org.reflections.util.ConfigurationBuilder;
import org.springframework.aop.aspectj.AspectJExpressionPointcut;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.LazyConnectionDataSourceProxy;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;
import org.springframework.transaction.interceptor.TransactionInterceptor;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Properties;

@AllArgsConstructor
@EnableTransactionManagement
@MapperScan(
    annotationClass = Mapper.class,
    basePackages = {MainDataSourceConfig.MYBATIS_MAPPER_PACKAGES},
    sqlSessionFactoryRef = MainDataSourceConfig.DATASOURCE_PREFIX + "SessionFactory")
@EnableJpaRepositories(
    basePackages = {MainDataSourceConfig.JPA_REPOSITORY_PACKAGES},
    entityManagerFactoryRef = MainDataSourceConfig.DATASOURCE_PREFIX + "EntityManagerFactory",
    transactionManagerRef = MainDataSourceConfig.DATASOURCE_PREFIX + "TransactionManager")
@Configuration
public class MainDataSourceConfig {

    public static final String DATASOURCE_PREFIX = "main";
    public static final String MYBATIS_MAPPER_PACKAGES =
        Constants.BASE_PACKAGE + ".api.**.mapper";
    public static final String JPA_REPOSITORY_PACKAGES =
        Constants.BASE_PACKAGE + ".api.**.repository";
    private static final String DATASOURCE_CONFIG_PROPERTY_PREFIX = "spring";
    private static final String[] JPA_ENTITY_PACKAGES = {
        Constants.BASE_PACKAGE + ".api.**.entity"};

    /**
     * DB 이중화 설정 - write
     */
    @Primary
    @Bean(DATASOURCE_PREFIX + "WriterDataSource")
    @ConfigurationProperties(prefix = DATASOURCE_CONFIG_PROPERTY_PREFIX + ".datasource")
    public DataSource writerDataSource(
        @Value("${" + DATASOURCE_CONFIG_PROPERTY_PREFIX + ".datasource.url}") String url
        ) {
      return DataSourceBuilder.create().type(HikariDataSource.class)
          .url(url)
          .build();
    }

    /**
     * DB 이중화 설정 - readonly
     */
    @Primary
    @Bean(DATASOURCE_PREFIX + "ReaderDataSource")
    public DataSource readDataSource(
        @Value("${" + DATASOURCE_CONFIG_PROPERTY_PREFIX + ".datasource.url}") String url,
        @Qualifier(DATASOURCE_PREFIX + "WriterDataSource") DataSource dataSource) {
      return DataSourceBuilder.derivedFrom(dataSource)
          .url(url)
          .build();
    }

    /**
     * read, write routing
     */
    @Primary
    @Bean(DATASOURCE_PREFIX + "DataSource")
    public DataSource routingDataSource(
        @Qualifier(DATASOURCE_PREFIX + "WriterDataSource") DataSource writerDataSource,
        @Qualifier(DATASOURCE_PREFIX + "ReaderDataSource") DataSource readerDataSource
    ) {

      final String writerKey = "writer";
      final String readerKey = "reader";
      AbstractRoutingDataSource routingDataSource = new AbstractRoutingDataSource() {
        @Override
        protected Object determineCurrentLookupKey() {
          boolean con = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
          return con ? readerKey : writerKey;
        }
      };
      Map<Object, Object> dataSourceMap =
          Map.of(writerKey, writerDataSource, readerKey, readerDataSource);

      routingDataSource.setTargetDataSources(dataSourceMap);
      routingDataSource.setDefaultTargetDataSource(writerDataSource);
      routingDataSource.afterPropertiesSet();

      return new LazyConnectionDataSourceProxy(routingDataSource);
    }

    @Primary
    @Bean(DATASOURCE_PREFIX + "JpaProperties")
    @ConfigurationProperties(prefix = DATASOURCE_CONFIG_PROPERTY_PREFIX + ".jpa")
    public JpaProperties jpaProperties() {
      return new JpaProperties();
    }

    // jpaVendorAdapter, jpaProperties
    @Primary
    @Bean(DATASOURCE_PREFIX + "EntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(
        @Qualifier(DATASOURCE_PREFIX + "DataSource") DataSource dataSource,
        @Qualifier(DATASOURCE_PREFIX + "JpaProperties") JpaProperties jpaProperties) {
      EntityManagerFactoryBuilder builder = entityManagerFactoryBuilder(jpaProperties);
      return builder.dataSource(dataSource).packages(JPA_ENTITY_PACKAGES)
          .persistenceUnit(DATASOURCE_PREFIX + "EntityManager").build();
    }

    private EntityManagerFactoryBuilder entityManagerFactoryBuilder(JpaProperties jpaProperties) {
      HibernateJpaVendorAdapter jpaVendorAdapter = new HibernateJpaVendorAdapter();
      jpaVendorAdapter.setGenerateDdl(jpaProperties.isGenerateDdl());
      jpaVendorAdapter.setShowSql(jpaProperties.isShowSql());
      jpaVendorAdapter.setDatabasePlatform(jpaProperties.getDatabasePlatform());
      return new EntityManagerFactoryBuilder(jpaVendorAdapter, jpaProperties.getProperties(), null);
    }

    @Primary
    @Bean(DATASOURCE_PREFIX + "TransactionManager")
    public PlatformTransactionManager transactionManager(@Qualifier(DATASOURCE_PREFIX
        + "EntityManagerFactory") LocalContainerEntityManagerFactoryBean entityManagerFactory) {
      JpaTransactionManager transactionManager =
          new JpaTransactionManager(entityManagerFactory.getObject());
      transactionManager.setNestedTransactionAllowed(true); // 중첩 트렌젝션 설정
      return transactionManager;
    }

    @Bean(DATASOURCE_PREFIX + "SessionFactory")
    public SqlSessionFactory sessionFactory(
        @Qualifier(DATASOURCE_PREFIX + "DataSource") DataSource dataSource,
        @Qualifier("mybatisConfig") org.apache.ibatis.session.Configuration mybatisConfig)
        throws Exception {
      SqlSessionFactoryBean factoryBean = new SqlSessionFactoryBean();
      factoryBean.setDataSource(dataSource);
      Class[] typeAliases = new Reflections(
          new ConfigurationBuilder()
              .forPackages(Constants.BASE_PACKAGE))
          .getTypesAnnotatedWith(Alias.class)
          .stream()
          .toArray(Class[]::new);
      factoryBean.setTypeAliases(typeAliases);
      factoryBean.setTypeHandlersPackage(Constants.BASE_PACKAGE + ".config.database");
      factoryBean.setMapperLocations(
          new PathMatchingResourcePatternResolver().getResources("classpath:mappers/**/*.xml"));
      factoryBean.setConfiguration(mybatisConfig);
      return factoryBean.getObject();
    }

    @Bean(DATASOURCE_PREFIX + "SqlSessionTemplate")
    public SqlSessionTemplate sqlSessionTemplate(
        @Qualifier(DATASOURCE_PREFIX + "SessionFactory") SqlSessionFactory sqlSessionFactory) {
      return new SqlSessionTemplate(sqlSessionFactory);
    }

    @Bean("mybatisConfig")
    @ConfigurationProperties(prefix = "mybatis.configuration")
    public org.apache.ibatis.session.Configuration mybatisConfig() {
      return new org.apache.ibatis.session.Configuration();
    }

    /*
     * @Transactional(readOnly = true) 어노테이션 없을 시 기본값
     * default transaction read-only = true
     */
    @Aspect
    @Configuration
    @RequiredArgsConstructor
    public class TransactionConfig {

      @Qualifier(DATASOURCE_PREFIX + "TransactionManager")
      private final PlatformTransactionManager txManager;

      @Bean
      public TransactionInterceptor txAdvice() {

        TransactionInterceptor txAdvice = new TransactionInterceptor();
        DefaultTransactionAttribute readOnlyAttribute = new DefaultTransactionAttribute(
            TransactionDefinition.PROPAGATION_SUPPORTS);
        readOnlyAttribute.setReadOnly(true);

        String readOnlyTransactionAttributesDefinition = readOnlyAttribute.toString();

        Properties txAttributes = new Properties();
        txAttributes.setProperty("*", readOnlyTransactionAttributesDefinition);
        txAdvice.setTransactionAttributes(txAttributes);
        txAdvice.setTransactionManager(txManager);

        return txAdvice;
      }

      @Bean
      public DefaultPointcutAdvisor txAdviceAdvisor() {
        AspectJExpressionPointcut pointcut = new AspectJExpressionPointcut();
        pointcut.setExpression("execution(* *..service.*Service.*(..))");
        return new DefaultPointcutAdvisor(pointcut, txAdvice());
      }

    }
}
