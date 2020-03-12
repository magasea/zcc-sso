package com.wensheng.sso.config;

import com.wensheng.sso.service.UserService;
import java.util.Arrays;
import java.util.Collection;
import javax.sql.DataSource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.core.userdetails.UserDetailsByNameServiceWrapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerSecurityConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultTokenServices;
import org.springframework.security.oauth2.provider.token.TokenEnhancer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JdbcTokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationProvider;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.transaction.annotation.Transactional;

@Configuration
@EnableAuthorizationServer
@ComponentScan(value = "com.wensheng.sso")

@Slf4j
public class OAuth2AuthorizationServerConfig extends AuthorizationServerConfigurerAdapter {

    @Autowired
    MyWebResponseExceptionTranslator myWebResponseExceptionTranslator;

    @Autowired
    @Qualifier("authenticationManagerBean")
    private AuthenticationManager authenticationManager;

    private int accessTokenValidSeconds = 43200;
//    private int accessTokenValidSeconds = 150;

    private int refreshTokenValidSeconds = 2592000;

    @Value("${spring.security.oauth2.client.registration.amc-admin.client-id}")
    private String amcAdminClientId;


    @Value("${spring.security.oauth2.client.registration.amc-admin.secret}")
    private String amcAdminSecret;


    @Value("${spring.security.oauth2.client.registration.amc-admin.scopes}")
    private String amcAdminScopes;

    @Value("${spring.security.oauth2.client.registration.amc-admin.authorizedGrantTypes}")
    private String amcAdminAuthorizedGrantTypes;

    @Value("${spring.security.oauth2.client.registration.amc-admin.redirectUris}")
    private String amcAdminRedirectUris;



    @Value("${spring.security.oauth2.client.registration.amc-client-thirdpart.client-id}")
    private String amcWechatClientId;

    @Value("${spring.security.oauth2.client.registration.amc-client-thirdpart.secret}")
    private String amcWechatSecret;


    @Value("${spring.security.oauth2.client.registration.amc-client-thirdpart.scopes}")
    private String amcWechatScopes;

    @Value("${spring.security.oauth2.client.registration.amc-client-thirdpart.authorizedGrantTypes}")
    private String amcWechatAuthorizedGrantTypes;

    @Value("${spring.security.oauth2.client.registration.amc-client-thirdpart.redirectUris}")
    private String amcWechatRedirectUris;

    @Autowired
    PasswordEncoder passwordEncoder;

    @Autowired
    DataSource dataSource;


    @Autowired
    UserService userService;

    @Override
    public void configure(final AuthorizationServerSecurityConfigurer oauthServer) throws Exception {
        oauthServer.tokenKeyAccess("permitAll()").checkTokenAccess("isAuthenticated()");

    }

    @Override
    public void configure(final ClientDetailsServiceConfigurer clients) throws Exception {
        clients.inMemory().withClient("sampleClientId").authorizedGrantTypes("implicit").scopes("read", "write", "foo", "bar").autoApprove(false).accessTokenValiditySeconds(3600).redirectUris("http://localhost:8083/","http://localhost:8086/")

                .and().withClient("testImplicitClientId").authorizedGrantTypes("implicit").scopes("read", "write", "foo", "bar").autoApprove(true).redirectUris("xxx")
//            .and().withClient(amcWechatClientId).authorizedGrantTypes(amcWechatAuthorizedGrantTypes.replace(" ","").split(",")).scopes(amcWechatScopes.replace(" ", "").split(",")).autoApprove(true).redirectUris(amcWechatRedirectUris.split(","))
                .and().withClient(amcAdminClientId).secret(passwordEncoder.encode(amcAdminSecret))
            .and().withClient(amcWechatClientId).secret(passwordEncoder.encode(amcWechatSecret)).scopes(amcWechatScopes.replace(" ", "").split(",")).authorizedGrantTypes(amcWechatAuthorizedGrantTypes)
            .authorizedGrantTypes(amcAdminAuthorizedGrantTypes.replace(" ","").split(",")).scopes(amcAdminScopes.replace(" ","").split(
                ","))
            .autoApprove(false).redirectUris(amcAdminRedirectUris.split(","))
            .accessTokenValiditySeconds(accessTokenValidSeconds).refreshTokenValiditySeconds(refreshTokenValidSeconds)

        ;

    }



    @Bean(name = "ssoTokenServices" )
    @Primary
    public DefaultTokenServices ssoTokenServices() {
        final DefaultTokenServices defaultTokenServices = new DefaultTokenServices();
        defaultTokenServices.setTokenStore(tokenStore());
        defaultTokenServices.setSupportRefreshToken(true);
        defaultTokenServices.setReuseRefreshToken(false);
        addUserDetailsService(defaultTokenServices, userService);
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
        defaultTokenServices.setAccessTokenValiditySeconds(accessTokenValidSeconds);
        defaultTokenServices.setRefreshTokenValiditySeconds(refreshTokenValidSeconds);
        defaultTokenServices.setTokenEnhancer(tokenEnhancerChain);
        return defaultTokenServices;
    }

    private void addUserDetailsService(DefaultTokenServices tokenServices, UserService userService) {
        if (userService != null) {
            PreAuthenticatedAuthenticationProvider provider = new PreAuthenticatedAuthenticationProvider();
            provider.setPreAuthenticatedUserDetailsService(new UserDetailsByNameServiceWrapper<PreAuthenticatedAuthenticationToken>(
                userService));
            tokenServices
                .setAuthenticationManager(new ProviderManager(Arrays.<AuthenticationProvider> asList(provider)));
        }
    }



    @Override
    public void configure(final AuthorizationServerEndpointsConfigurer endpoints) throws Exception {
        final TokenEnhancerChain tokenEnhancerChain = new TokenEnhancerChain();
        tokenEnhancerChain.setTokenEnhancers(Arrays.asList(tokenEnhancer(), accessTokenConverter()));
        endpoints.tokenServices(ssoTokenServices()).tokenEnhancer(tokenEnhancerChain).authenticationManager(authenticationManager);
//        endpoints.pathMapping("oauth/token", "oauth/token");
        endpoints.exceptionTranslator(myWebResponseExceptionTranslator);
    }

    @Bean
    @Primary
    public TokenStore tokenStore() {
        JdbcTokenStore jdbcTokenStore =  new AmcJdbcTokenStore(dataSource);
        return jdbcTokenStore;
//        return new JwtTokenStore(accessTokenConverter());
    }

    @Bean
    public JwtAccessTokenConverter accessTokenConverter() {
        final JwtAccessTokenConverter converter = new JwtAccessTokenConverter();
        converter.setSigningKey("wenshengamc#1234567890");
        // final KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(new ClassPathResource("mytest.jks"), "mypass".toCharArray());
        // converter.setKeyPair(keyStoreKeyFactory.getKeyPair("mytest"));
        return converter;
    }

    @Bean
    public TokenEnhancer tokenEnhancer() {
        return new CustomTokenEnhancer();
    }

    @Bean
    public TokenEnhancer wechatTokenEnhancer(){ return new WechatTokenEnhancer(); }

    @CacheConfig(cacheNames = {"TOKEN"})
    class AmcJdbcTokenStore extends JdbcTokenStore{

        public AmcJdbcTokenStore(DataSource dataSource) {
            super(dataSource);
        }

        @Override
        @Cacheable
        public Collection<OAuth2AccessToken> findTokensByClientId(String clientId){
            log.info("query caches with clientId:{}", clientId);
            return super.findTokensByClientId(clientId);
        }

        @Transactional
        @CacheEvict(allEntries = true)
        public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
            super.removeAccessTokenUsingRefreshToken(refreshToken);
        }

        @Transactional
        @CacheEvict(allEntries = true)
        public void removeAccessTokenUsingRefreshToken(String refreshToken) {
            super.removeAccessTokenUsingRefreshToken(refreshToken);
        }
        @Transactional
        @CacheEvict(allEntries = true)
        public void removeAccessToken(OAuth2AccessToken token) {
            super.removeAccessToken(token);
        }
        @Transactional
        @CacheEvict(allEntries = true)
        public void removeAccessToken(String tokenValue) {
            super.removeAccessToken(tokenValue);
        }
        @Transactional
        @CacheEvict(allEntries = true)
        public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
            super.storeAccessToken(token, authentication);
        }

    }


}
