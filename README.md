# tosan-httpserver-spring-boot-starter

This project provides an Spring-Boot Starter that facilitate common requirements of a rest server like filling MDC parameters 
, logging http request and response with ability to mask sensitive data in header and body, and simple metrics logging.

## Usage

The `tosan-httpserver-spring-boot-starter` brings most of the required configuration with it, therefore you only need to add
it as a maven dependency and enable the desired functionality. 

```
<dependency>
  <groupId>com.tosan.server.http</groupId>
  <artifactId>tosan-httpserver-spring-boot-starter</artifactId>
  <version>latest-version</version>
</dependency>
```

### mdc filter
one of the common requirements of Http server is providing correct mdc parameters in order to trace request easily. 
to reach this goal a filter name HttpMdcFilter is provided. this bean is defined by default as below:
```
    @Bean
    @ConditionalOnMissingBean
    public HttpMdcFilter httpMdcFilter(MdcUtil mdcUtil) {
        return new HttpMdcFilter(mdcUtil);
    }
```
to customize excluded url patterns you can define your own filter bean as below. in this example url with pattern '/testUrl'
will be excluded from MdcFilter.
```
    @Bean
    @ConditionalOnMissingBean
    public HttpMdcFilter httpMdcFilter(MdcUtil mdcUtil) {
        HttpMdcFilter httpMdcFilter = new HttpMdcFilter(mdcUtil);
        List<String> excludeUrlPatterns = Collections.singletonList("/testUrl");
        httpMdcFilter.setExcludeUrlPatterns(excludeUrlPatterns);
        return httpMdcFilter;
    }
```


in order to specify mdc parameters processed by HttpMdcFilter, MdcFilterConfig must be defined. default config is as below:
```
    @Bean("http-server-util-mdc-filter-config")
    @ConditionalOnMissingBean(name = "http-server-util-mdc-filter-config")
    public MdcFilterConfig mdcFilterConfig() {
        List<HttpHeaderMdcParameter> list = new ArrayList<>();
        list.add(Constants.X_REQUEST_ID_MDC_PARAM);
        return new MdcFilterConfig(list);
    }
```
this config consist mainly of a list of HttpHeaderMdcParameter object that have below parameters:

> headerParameterName: parameter name define in http request header
> mdcParametersName: parameter name in MDC
> replaceUnfreeCharacters: check unfree characters and replace to new character
> randomParameter: object related to parameter random generation if parameter is not sent in http request

random parameter generation will be applied when random parameter is set and header parameter is not sent in http request.
this parameter has below fields:
> generationType: have two types of NUMERIC and ALPHANUMERIC
> length: length of random generated string
> prefix: prefix added to random generated string(empty string by default)

example1: simple http request header in MDC 

```
HttpHeaderMdcParameter USER_AGENT_MDC_PARAM = new HttpHeaderMdcParameter.
            HttpHeaderMdcParameterBuilder("User-Agent", "userAgent").build();
```
above example will work for header parameter named(User-Agent). this parameter will simply be added to MDC with name(userAgent).

example2: http request with random generation option
```
HttpHeaderMdcParameter X_REQUEST_ID_MDC_PARAM = new HttpHeaderMdcParameter.
            HttpHeaderMdcParameterBuilder("X-Request-ID", "requestId").removeUnfreeCharacters(
            new RandomParameter(RandomGenerationType.ALPHANUMERIC, 8)).build();
```

above example will work for header parameter named (X-Request-ID). this parameter will be entered in MDC with name (requestId).
and if it's not sent in http request, it will be random generated as an 8 character alphanumeric string and set in MDC field.

example3:http request with unfree characters replacement

```
HttpHeaderMdcParameter CLIENT_IP_MDC_PARAM = new HttpHeaderMdcParameter.
            HttpHeaderMdcParameterBuilder("X-Forwarded-For", "clientIP").removeUnfreeCharacters(true).build();
```
sometimes we want to remove some characters before added to MDC. in this case we can activate this mode by setting removeUnfreeCharacters
to true. these unfree characters and new character can be configured as below:
```
    @Bean("http-server-util-mdc-filter-config")
    public MdcFilterConfig mdcFilterConfig() {
        List<HttpHeaderMdcParameter> list = new ArrayList<>();
        HttpHeaderMdcParameter clientIp = new HttpHeaderMdcParameter
                .HttpHeaderMdcParameterBuilder("ClientAddress", COMPLETE_CLIENT_ADDRESS_PARAMETER_NAME)
                .removeUnfreeCharacters(true)
                .build();
        list.add(clientIp);
        char[] unfreeCharacters = {'/', '\\', ':', '*', '?', '"'};
        char newCharacter = '-';
        MdcFilterConfig mdcFilterConfig = new MdcFilterConfig(list, unfreeCharacters, newCharacter);
        return mdcFilterConfig;
    }
```

in above configuration filter will search selected header(ClientAddress) for unfree characters and if it finds any of 
specified characters it will replace it with new character.

attention: MDCFilterConfig must be defined with bean name "http-server-util-mdc-filter-config". otherwise, other
configuration beans must be defined as well.

in addition to parameters specified configuration remote client ip parameter will be extracted. this parameter will be placed 
in MDC with parameter name: "clientIP". first (X-Forwarded-For) header will be checked if it's not empty first ip address
in this header will be placed in clientIp MDC (for example: for "192.168.16.49,192.168.16.50" header, this "192.168.16.49" will be selected) 
otherwise the remote address in RequestContextHolder will be placed in clientIP MDC parameter.

### http log filter
logging complete http request and response characteristic is an important feature in tracing request. many web servers
provide this ability but might lack in some aspects like masking sensitive data before logging. this library uses provided 
servlet api in order to work with different web servers. these embedded web servers are supported in this library:
> tomcat
> jetty
> undertow

sample of http request and response is logged as below:
```
-- Http Request --
POST /httpserver/test
accept: application/json, application/*+json
content-type: application/json
x-request-id: val453453ue
clientaddress: 192.168.170.23
username: *SEMIENCRYPTED:mina
context: {"secretKey":"*SEMI_ENCRYPTED:456","test":"test*****"}
user-agent: Java/17.0.2
host: localhost:53454
connection: keep-alive
content-length: 73

{"name":"mina","family":"kh","pan":"*SEMI_ENCRYPTED:403948******3094","test":"test*****"}


-- Http Response --
200 OK

{"secretKey":"*SEMI_ENCRYPTED:sec","password":"ENCRYPTED"}
```

as it's clear some parameters are masked in http header and http body. these parameters can be specified by defining
SecureParametersConfig bean. in this library this bean is defined by default as below:
```
    @Bean("http-server-util-secured-parameters")
    @ConditionalOnMissingBean(name = "http-server-util-secured-parameters")
    public SecureParametersConfig secureParametersConfig() {
        HashSet<SecureParameter> securedParameters = new HashSet<>();
        securedParameters.add(new SecureParameter("authorization", MaskType.COMPLETE));
        securedParameters.add(new SecureParameter("proxy-authorization", MaskType.COMPLETE));
        return new SecureParametersConfig(securedParameters);
    }
```

above configuration mask any parameter with name "authorization" and "proxy-authorization" with complete maskType if it 
exists in header or body(request/response). this library uses 'tosan-mask-spring-boot-starter' for masking data. in order to 
specify your own sensitive data with desired mask type you can define your own mask type, and it will be registered automatically.
1. define your new MaskType:
```
public class UserMaskType extends MaskType {

    public static final MaskType TEST_MASK_TYPE = new MaskType();
}
```

2. define masking behaviour
```
@Component
public class TestValueMasker implements ValueMasker {

    @Override
    public MaskType getType() {
        return UserMaskType.TEST_MASK_TYPE;
    }

    /**
     * change each value with test string
     * @param parameterPlainValue
     * @return
     */
    @Override
    public String mask(String parameterPlainValue) {
        return "test*****";
    }
}
```

3. now you can use your mask type in your defined SecureParametersConfig:
```
    @Bean("http-server-util-secured-parameters")
    public SecureParametersConfig secureParametersConfig() {
        Set<SecureParameter> securedParameters = MaskBeanConfiguration.SECURED_PARAMETERS;
        securedParameters.add(new SecureParameter("test", UserMaskType.TEST_MASK_TYPE));
        securedParameters.add(new SecureParameter("secretKey", UserMaskType.RIGHT));
        securedParameters.add(new SecureParameter("username", UserMaskType.SEMI));
        return new SecureParametersConfig(securedParameters);
    }
```

above example mask any field with name 'test' with mask behaviour defined in UserMaskType.TEST_MASK_TYPE. some predefined 
maskType's exist in 'tosan-mask-spring-boot-starter' like maskType (RIGHT or SEMI)

for logging data a bean of type HttpLoggingFilter is provided by default as below. this bean exlude actuator url pattern
(/actuator/*) by default.
```
    @Bean
    @ConditionalOnMissingBean
    public HttpLoggingFilter httpLoggingFilter(HttpLogUtil httpLogUtil) {
        return new HttpLoggingFilter(httpLogUtil);
    }
```
this excluded url pattern can be customized by defining your own loggingFilter bean. by using (addExcludeUrlPatterns) you 
can add your desired pattern to default patterns. you can also use (setExcludeUrlPatterns) method to add your own exclude
url patterns without default patterns.
```
    @Bean
    public HttpLoggingFilter httpLoggingFilter(HttpLogUtil httpLogUtil) {
        HttpLoggingFilter httpLoggingFilter = new HttpLoggingFilter(httpLogUtil);
        List<String> excludeUrlPatterns = Collections.singletonList("/testUrl");
        httpLoggingFilter.addExcludeUrlPatterns(excludeUrlPatterns);
        return httpLoggingFilter;
    }
```

attention: HttpLogFilter only register http logs in DEBUG mode. 

### statistics filter
HttpStatisticsFilter is created for purpose of logging simple metrics about http requests.
this filter logs some metrics as below:
on request:
```
{ "+service" : "POST /httpserver/test", "active requests" : 1 } 
```
on response:
```
{ "-service" : "POST /httpserver/test", "duration" : "3.793s", "active requests" : 0 } 
```
this filter excludes some url patterns by default:
```
"/swagger-ui/**"
"/api-docs/**"
"/favicon.ico"
"/actuator/**"
```
you can add your desired exclude url patterns by creating HttpStatisticFilter bean like this example:
```
    @Bean
    public HttpStatisticsFilter httpStatisticsFilter() {
        HttpStatisticsFilter httpStatisticsFilter = new HttpStatisticsFilter();
        httpStatisticsFilter.addExcludeUrlPatterns(Collections.singletonList("/testUrl"));
        return httpStatisticsFilter;
    }
```

### ServiceLogAspect
this aspect is for logging request and response/exception after converting http request to application dto and before
changing response dto to Http response. this aspects work on each public method of any class annotated with @RequestMapping
and run in INFO log mode. this aspect uses DTO toString method to create log. so you can customize your toString method as 
desired. in this library we used 'tosan-tostring-builder' for building toString methods. you can see a sample of this 
library in [TestRequestDto](./tosan-httpserver-spring-boot-sample/src/main/java/com/tosan/http/server/sample/dto/TestRequestDto.java)

### Sample Project
You can find a sample project in tosan-httpserver-spring-boot-sample module

### Prerequisites
This Library requires java version 8 or above.

## Contributing
Any contribution is greatly appreciated.

If you have a suggestion that would make this project better, please fork the repo and create a pull request.
You can also simply open an issue with the tag "enhancement".

## License
The source files in this repository are available under the [Apache License Version 2.0](./LICENSE.txt).
