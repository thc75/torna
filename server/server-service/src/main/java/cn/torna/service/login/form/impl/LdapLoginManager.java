package cn.torna.service.login.form.impl;

import cn.torna.common.enums.UserInfoSourceEnum;
import cn.torna.common.exception.BizException;
import cn.torna.service.login.form.LoginForm;
import cn.torna.service.login.form.LoginResult;
import cn.torna.service.login.form.ThirdPartyLoginManager;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.Hashtable;

/**
 * LDAP登录
 */
@Slf4j
@Service
public class LdapLoginManager implements ThirdPartyLoginManager {


    @Autowired
    private LdapTemplate ldapTemplate;

    @Value("${torna.email-domain:}")
    private String emailDomain;

    @Value("${torna.ldap.id-name:${torna.id-name:uid}}")
    private String idName;

    // 过滤器，默认：(&(objectClass=*)(uid=%s))
    // %s表示用户名
    @Value("${torna.ldap.filter:${torna.ldap.custom-search-filter:(&(objectClass=${torna.ldap.object-class:*})(${torna.ldap.id-name:uid}=%s))}}")
    private String filter;

    @Value("${torna.ladp.custom-url:${torna.ldap.url:}}")
    private String customUrl;

    @Value("${torna.ldap.custom-base-dn:${torna.ldap.base:}}")
    private String customBaseDn;

    @Value("${torna.ldap.custom-username:${torna.ldap.username:}}")
    private String customUsername;

    @Value("${torna.ldap.custom-password:${torna.ldap.password:}}")
    private String customPassword;

    @Value("${torna.ldap.custom-context-factory:com.sun.jndi.ldap.LdapCtxFactory}")
    private String factory;


    @Override
    public LoginResult login(LoginForm loginForm) throws Exception {
        String username = loginForm.getUsername();
        String password = loginForm.getPassword();
        LdapUser ldapUser = ldapLogin(username, password);
        // 正常流程登录失败，再使用spring封装的登录
        if (ldapUser == null) {
            ldapUser = ldapAuth(username, password);
        }
        if (ldapUser == null) {
            throw new BizException("登录失败");
        }
        LoginResult loginResult = new LoginResult();
        loginResult.setUsername(ldapUser.getUid());
        String displayName = ldapUser.getDisplayName();
        // 昵称没有则使用登录名
        if (StringUtils.isEmpty(displayName)) {
            displayName = ldapUser.getUid();
        }
        loginResult.setNickname(displayName);
        String mail = ldapUser.getMail();
        if (StringUtils.hasText(emailDomain)) {
            mail = ldapUser.getUid() + "@" + StringUtils.trimLeadingCharacter(emailDomain, '@');
        }
        loginResult.setEmail(mail);
        loginResult.setUserInfoSourceEnum(UserInfoSourceEnum.LDAP);
        return loginResult;
    }

    /**
     * LDAP登录
     *
     * @param uid      用户名
     * @param password 密码
     * @return 登录成功返回登录信息，否则返回null
     */
    public LdapUser ldapAuth(String uid, String password) {
        try {
            return ldapTemplate.authenticate(
                    LdapQueryBuilder.query().where(idName).is(uid),
                    password,
                    (dirContext, ldapEntryIdentification) ->
                            ldapTemplate.findOne(LdapQueryBuilder.query().where(idName).is(uid), LdapUser.class));
        } catch (Exception e) {
            log.error("LDAP登录失败，username={}", uid, e);
            return null;
        }
    }

    public LdapUser ldapLogin(String uid, String password) {
        LdapContext ldapContext = getLdapContext();
        if (ldapContext == null) {
            return null;
        }
        SearchResult searchResult = getUserInfo(ldapContext, uid);
        if (searchResult == null) {
            log.warn("没有找到用户,uid={}", uid);
            return null;
        }
        String userDN = searchResult.getName() + "," + customBaseDn;
        log.info("用户登录，customUrl={}, userDN={}", customUrl, userDN);
        try {
            ldapContext.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
            ldapContext.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ldapContext.reconnect(null);
            // 成功表示用户名密码正确
            return buildLdapUser(uid, searchResult);
        } catch (Exception e) {
            log.error("LDAP认证失败, url={}, uid={}", customUrl, uid, e);
            return null;
        } finally {
            try {
                ldapContext.close();
            } catch (NamingException e) {
                // ignore
            }
        }
    }

    private LdapUser buildLdapUser(String uid, SearchResult searchResult) {
        Attributes attributes = searchResult.getAttributes();
        log.info("LDAP登录信息：{}", attributes);
        String displayName = getAttributeValue(attributes.get("displayName"), uid);
        String mail = getAttributeValue(attributes.get("mail"), "");
        LdapUser ldapUser = new LdapUser();
        ldapUser.setUid(uid);
        ldapUser.setDisplayName(displayName);
        ldapUser.setMail(mail);
        return ldapUser;
    }

    private static String getAttributeValue(Attribute attribute, String defaultValue) {
        try {
            return attribute == null ? defaultValue : String.valueOf(attribute.get());
        } catch (NamingException e) {
            log.error("获取attribute失败, attribute={}", attribute, e);
            return defaultValue;
        }
    }

    private SearchResult getUserInfo(LdapContext ldapContext, String uid) {
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            String filter = String.format(this.filter, uid);
            NamingEnumeration<SearchResult> results = ldapContext.search(customBaseDn, filter, controls);
            if (results == null || !results.hasMoreElements()) {
                return null;
            }
            return results.nextElement();
        } catch (Exception e) {
            log.error("查找用户{}出错", uid, e);
            return null;
        }
    }

    private LdapContext getLdapContext() {
        if (StringUtils.hasText(customBaseDn)) {
            Hashtable<String, String> env = new Hashtable<>();
            env.put(Context.SECURITY_AUTHENTICATION, "simple");
            env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
            env.put(Context.PROVIDER_URL, customUrl);
            env.put(Context.SECURITY_PRINCIPAL, customUsername);
            env.put(Context.SECURITY_CREDENTIALS, customPassword);
            log.info("LDAP配置，env={}", JSON.toJSONString(env));
            try {
                return new InitialLdapContext(env, null);
            } catch (Exception e) {
                log.error("初始化LDAP失败, customBaseDn={}",customBaseDn, e);
                return null;
            }
        } else {
            log.info("未配置customBaseDn");
            return null;
        }
    }

}
