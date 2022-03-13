package cn.torna.service.login.form.impl;

import cn.torna.common.enums.UserInfoSourceEnum;
import cn.torna.common.exception.BizException;
import cn.torna.service.login.form.LoginForm;
import cn.torna.service.login.form.LoginResult;
import cn.torna.service.login.form.ThirdPartyLoginManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
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
public class LdapLoginManager implements ThirdPartyLoginManager, InitializingBean {


    @Autowired
    private LdapTemplate ldapTemplate;

    @Value("${torna.email-domain:}")
    private String emailDomain;

    @Value("${torna.ldap.custom-search-filter:(&(objectClass=inetOrgPerson)(uid=%s))}")
    private String ldapSearchFilter;

    @Value("${torna.ladp.custom-url:${torna.ldap.url:}/${torna.ldap.base:}}")
    private String customUrl;

    @Value("${torna.ldap.custom-base-dn:${torna.ldap.base:}}")
    private String customBaseDn;

    @Value("${torna.ldap.custom-username:${torna.ldap.username:}}")
    private String customUsername;

    @Value("${torna.ldap.custom-password:${torna.ldap.password:}}")
    private String customPassword;

    @Value("${torna.ldap.custom-context-factory:com.sun.jndi.ldap.LdapCtxFactory}")
    private String factory;


    private LdapContext ldapContext;

    @Override
    public LoginResult login(LoginForm loginForm) throws Exception {
        String username = loginForm.getUsername();
        String password = loginForm.getPassword();
        LdapUser ldapUser = authNormal(username, password);
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
                    LdapQueryBuilder.query().where("uid").is(uid),
                    password,
                    (dirContext, ldapEntryIdentification) ->
                            ldapTemplate.findOne(LdapQueryBuilder.query().where("uid").is(uid), LdapUser.class));
        } catch (Exception e) {
            log.error("LDAP登录失败，username={}", uid, e);
            return null;
        }
    }

    public LdapUser authNormal(String uid, String password) {
        if (ldapContext == null) {
            return null;
        }
        SearchResult searchResult = getUserInfo(uid);
        if (searchResult == null) {
            return null;
        }
        String userDN = searchResult.getName() + "," + customBaseDn;
        try {
            ldapContext.addToEnvironment(Context.SECURITY_PRINCIPAL, userDN);
            ldapContext.addToEnvironment(Context.SECURITY_CREDENTIALS, password);
            ldapContext.reconnect(null);
            // 成功表示用户名密码正确
            return buildLdapUser(searchResult);
        } catch (Exception e) {
            log.error("LDAP认证失败, uid={}", uid, e);
            return null;
        }
    }

    private LdapUser buildLdapUser(SearchResult searchResult) throws NamingException {
        Attributes attributes = searchResult.getAttributes();
        String uid = String.valueOf(attributes.get("uid").get());
        String displayName = String.valueOf(attributes.get("displayName").get());
        String mail = String.valueOf(attributes.get("mail").get());
        LdapUser ldapUser = new LdapUser();
        ldapUser.setUid(uid);
        ldapUser.setDisplayName(displayName);
        ldapUser.setMail(mail);
        log.debug("LDAP自定义配置登录成功，ldapUser={}", ldapUser);
        return ldapUser;
    }

    private SearchResult getUserInfo(String uid) {
        try {
            SearchControls controls = new SearchControls();
            controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
            NamingEnumeration<SearchResult> results = ldapContext.search("", String.format(ldapSearchFilter, uid), controls);
            if (results == null || !results.hasMoreElements()) {
                return null;
            }
            return results.nextElement();
        } catch (Exception e) {
            log.error("查找用户{}出错", uid, e);
            return null;
        }
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (StringUtils.hasText(customBaseDn)) {
            log.info("LDAP配置，url:{}, baseDN:{}, username:{}", customUrl, customBaseDn, customUsername);
            Hashtable<String, String> env = new Hashtable<>();

            env.put(Context.INITIAL_CONTEXT_FACTORY, factory);
            // ldap://192.168.153.129:389/dc=contoso,dc=com
            env.put(Context.PROVIDER_URL, customUrl);
            env.put(Context.SECURITY_PRINCIPAL, customUsername);
            env.put(Context.SECURITY_CREDENTIALS, customPassword);
            try {
                ldapContext = new InitialLdapContext(env, null);
            } catch (Exception e) {
                log.error("初始化LDAP失败", e);
            }
        }
    }

}
