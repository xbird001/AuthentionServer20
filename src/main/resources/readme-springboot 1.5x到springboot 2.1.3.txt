1、密码模式获取token的过程
    1.1、先由spring security的拦截器根据请求的请求头里面带的信息判断客户端的合法性，并生成Principal
    1.2、拦截器验证合法后，请求达到/auth/token的controller
        1.2.1、根据clientId获取客户端的信息ClientDetails;
        1.2.2、根据请求里面的请求参数(oauthe2协议密码模式规定的参数)和1.2.1中获取到的客户端信息ClientDetails生成TokenRequest;
        1.2.3、TokenGranter(CompositeTokenGranter)根据授权类型grand_type和TokenRequest生成授权的token
            1.2.3.1、CompositeTokenGranter根据根据grand_type授权类型从5种('authorization_code','refresh_token','implicit','client_credentials','password')授权模型对应的tokenGranter处理
            1.2.3.2、具体的tokenGranter的业务是由TokenService处理的(默认是DefaultTokenService),tokenService然后根据authenticationManager去验证请求中传入的用户名和密码(非客户端客户id和密码)，这里
            可理解成微信或者qq的账号和密码，如果验证通过则生成token(先从tokenStore里面拿，拿不到则生成)，然后将生成的token给TokenService里面的accessTokenEnhancer(如果有配置)进行进一步处理，如自定义token的生成策略
            (在生成token的过程中就一并将token进行了增强(如果配置了增强器))，处理完成后在由tokenStore
            进行存储(支持多种方式，本机内存、数据库、缓存)，默认是本机内存存储，供后面token验证时进行对应的查询验证
        1.2.4、将授权的token返回给前端

2、@EnableAuthorizationServer实现oauth2.0协议的密码模式获取token的整个执行过程如下：
    2.0、DelegatingFilterProxy -> FilterChainProxy(getFilters) -> BasicAuthenticationFilter -> AuthenticationManager(ProviderManager.getProviders方法) -> AuthenticationProvider(DaoAuthenticationProvider.authenticate方法)
         |-> UserDetailsSevice(ClientDetailsUserDetailsService的ClientDetailsService属性)
         这个过程中主要捕获异常的地方是BasicAuthenticationFilter的doFilterInternal方法捕获(抛出异常的主要是DaoAuthenticationProvider.authenticate方法)，捕获到AuthenticationException的子类异常时，将authenticationEntryPoint的commence处理掉

    2.1、通过客户端(postman等)发起获取token的请求地址：http://{{ip}}:8070/oauth/token

    2.2、请求首先被spring security的顶层拦截器DelegatingFilterProxy拦截，如果其delegate属性还未初始化，则从spring容器中寻找FilterChainProxy对象进行初始化赋值，该过程是同步的，然后将请求转发给FilterChainProxy来处理

    2.3、进入到FilterChainProxy拦截器，首先获取预先配置的一系列的拦截器filters(这些都在spring容器中注册，是spring security管理的)，再初始化VirtualFilterChain对象，是整个流程在获取的一系列拦截filters中进行流转执行(注意这是在spring security内部管理一系列拦截器
        并不是servlet级别的拦截器)，当这些spring security配置的一系列拦截器filters执行完了之后，再继续回到servlet级别的filter拦截器接着后面chain执行。在这一系列的拦截器filters中，最终是BasicAuthenticationFilter这个拦截来进行处理；

    2.4、程序进入到BasicAuthenticationFilter，由他来处理获token时客户端信息的验证(在Http的Header中配置的Authentication认证信息)，首先从Http的Header中获取Authorization，并且从中导出包含客户端账号信息的token(这个token不是我们要获取的token)，并获取username,
        然后将username和获得token进行封装成UsernamePasswordAuthenticationToken对象，然后将UsernamePasswordAuthenticationToken对象传递给AuthenticationManager接口实现类类进行客户端账号信息的验证即验证用户名和密码是否正确；

    2.5、AuthenticationManager的实际对象是ProviderManager对象，而ProviderManager并不处理实际的验证逻辑，而是将具体的验证逻辑交给他内部包含的多个AuthenticationProvider的子类，从中挑选符合条件AuthenticationProvider来验证具体的客户端账号和密码信息，具体是
        DaoAuthenticationProvider  --(AnonymousAuthenticationProvider)最终来处理具体的验证逻辑；具体的挑选逻辑是判断具体AuthenticationProvider子类的isSupport方法是否支持UsernamePasswordAuthenticationToken这个类，如果是isAssignFrom返回的是true,
        则匹配成功，交由匹配上的AuthenticationProvider来处理

    2.6、DaoAuthenticationProvider类的authenticate方法来处理具体的验证逻辑；
        2.6.1、根据用户名获取用户信息：
            首先获取客户端信息中的用户名，然后先从缓存中根据用户名查找用户信息，如果为空，则交由UserDetailsSevice的实现类ClientDetailsUserDetailsService对象的ClientDetailsService属性对象来处理，
            而这里就是框架提供的可扩展点，我们可以自定义ClientDetailsService的实现类来自定义个性化的获取用户信息的具体方式方法(如从内存中，亦或是数据库中，可由用户具体需求定制)，spring oauth2.0已经提供了两种方式InMemoryClientDetailsService以及JdbcClientDetailsService
            的实现方式，只需要按照需要选择注册即可使用。注意DaoAuthenticationProvider的retrieveUser方法负责根据用户名“查找”客户端账号信息；如果retrieveUser获取的账户信息为空则抛出异常

        2.6.2、前置验证器preAuthenticationChecks.check(user)验证获取的用户账号信息是否被锁定、是否能使用等，如果有一个不符合条件，直接抛出对应的异常，如LockedException、或者是DisabledException等等，这些都是spring security 自定义异常AuthenticationException的子类；

        2.6.3、附加验证器additionalAuthenticationChecks(user,(UsernamePasswordAuthenticationToken) authentication)验证具体的密码是否能够匹配，这里不验证用户名，因为如果前已经根据传入的用户名来查找用户信息，如果查找的为null，则已经抛出了异常，如果不为null，说明用户名肯定可以
            匹配上，所以这里只需要验证密码是否能够匹配。如果不能匹配上，直接抛出BadCredentialsException异常

        2.6.4、后置验证器postAuthenticationChecks.check(user);验证账号是否过期，如果过期直接抛出异常CredentialsExpiredException账号过期异常

        2.6.5、返回经过验证的UsernamePasswordAuthenticationToken对象，这里比之前的UsernamePasswordAuthenticationToken对象多了权限信息，因为验证通过后才会有相应的权限，刚开始验证时，只有用户名和密码；

        2.6.6、将返回的包含已认证的信息的UsernamePasswordAuthenticationToken放入SecurityContext中(SecurityContextHolder.getContext().setAuthentication(authResult))

        2.6.7、如果不抛出异常将继续剩下的spring security组织的filter，以及后面servlet级别的filter，直到最后请求到达DispatcherServlet中，然后被分发到/oauth/token的controller上执行用户名和密码的验证；如果抛出异常并捕获后直接返回相关信息

    2.7、程序回到FilterChainProxy，接着后面的拦截器进行执行，直到ExceptionTranslationFilter过滤器，该过滤器啥也不做，直接将请求转发给FilterSecurityInterceptor过滤器，该过滤器负责操作权限验证(如有没有访问该请求的权限)，如果没有则会抛出异常，该异常将会被ExceptionTranslationFilter
        拦截器捕获到，并根据不同的异常，做不同的处理，如重定向到登录页面等等

    2.8、请求最后到了spring mvc的分发servlet的DispatcherServlet，最终请求会分发到/oauth/token的Contoller请求处理类TokenEndpoint，负责处理用户名和密码是否匹配，这里的用户信息指的request参数中传递的，非Http的Header中传递的客户端账号信息(具体理解oauth中的两个账户信息)
        2.8.1、进入TokenEndpoint的postAccessToken方法，首先判断principal即客户端账户信息是否为null,为null直接抛出异常，从principal获取clientId，根据clientId获取客户端信息，然后校验客户端对应的配置信息是否能够匹配上；

        2.8.2、通过getTokenGranter方法获得CompositeTokenGranter对象来处理token的生成工作，CompositeTokenGranter对象不负责处理实际的工作，CompositeTokenGranter根据根据grand_type授权类型从5种('authorization_code','refresh_token','implicit','client_credentials','password')授权模型对应的tokenGranter处理，
            最后处理token工作的根据grant_type会选择ResourceOwnerPasswordTokenGranter类来处理(即Oauth2.0中的密码模式)来处理

        2.8.3、ResourceOwnerPasswordTokenGranter的getOAuth2Authentication方法来获取认证信息，首先从request的请求参数中获取用户名和密码，并封装成UsernamePasswordAuthenticationToken对象，然后将UsernamePasswordAuthenticationToken传递给authenticationManager(ProviderManager)的authenticate方法，ProviderManager
        的authenticate方法，并不处理具体的认证逻辑，而是从多个AuthenticationProvider中根据supports方法返回值来判断指定AuthenticationProvicer是否能处理认证工作，最后将工作传递给DaoAuthenticationProvider类来处理；

        2.8.4、后面的工作将会与2.6中描述的差不多，唯一不同的是这里的DaoAuthenticationProvider对象里面的UserDetailsSevice对象是我们扩展的自定义的DseUserDetailsService对象来根据用户名查询用户账号信息，后面将会一样；

    2.9、从spring boot 1.5x升级到spring boot 2.x版本的区别如下(这里主要是指spring-cloud-starter-oauth2版本的升级)：
        2.9.1、AuthenticationManager这个bean无法自动的配置到spring容器中，需要手动配置，参见DseWebSecurityConfig.authenticationManagerBean方法；
        2.9.2、1.5x版本中客户端信息的通过配置文件指定时，没有用加密器加密，2.x版本用了加密器加密，固在spring boot2.x的版本时，通过配置文件指定客户端信息的密码时，请先加密，然后再保存；

