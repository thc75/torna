package cn.torna.service.login.form.impl;

import cn.torna.common.enums.UserInfoSourceEnum;
import cn.torna.common.exception.BizException;
import cn.torna.service.login.form.LoginForm;
import cn.torna.service.login.form.LoginResult;
import cn.torna.service.login.form.ThirdPartyLoginManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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


    @Override
    public LoginResult login(LoginForm loginForm) throws Exception {
        String username = loginForm.getUsername();
        String password = loginForm.getPassword();
        LdapUser ldapUser = ldapAuth(username, password);
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
     * @param uid 用户名
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

}
