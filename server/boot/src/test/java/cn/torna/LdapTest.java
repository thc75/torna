package cn.torna;

import cn.torna.service.login.form.LoginForm;
import cn.torna.service.login.form.LoginResult;
import cn.torna.service.login.form.impl.LdapLoginManager;
import cn.torna.service.login.form.impl.LdapUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;

/**
 * application.properties:
 * <pre>
 * torna.ldap.url=ldap://ip:port
 * # ldap域名信息，如：dc=your_domain,dc=com
 * torna.ldap.base=dc=your_domain,dc=com
 * # 管理员账号，如：cn=Manager,dc=your_domain,dc=com
 * torna.ldap.username=cn=Manager,dc=your_domain,dc=com
 * # 管理员密码
 * torna.ldap.password=xxx
 * </pre>
 *
 */
@SpringBootTest(classes = TornaApplication.class, properties = {
        "torna.ldap.url=ldap://10.0.1.178:389",
        "torna.ldap.base=dc=torna,dc=cn",
        "torna.ldap.username=cn=Manager,dc=torna,dc=cn",
        "torna.ldap.password=123456"
})
public class LdapTest {

    @Autowired
    private LdapTemplate ldapTemplate;

    @Autowired
    private LdapLoginManager ldapLoginManager;

    @Test
    public void login() throws Exception {
        LoginForm loginForm = new LoginForm();
        loginForm.setUsername("zhangsan");
        loginForm.setPassword("123456");
        LoginResult loginResult = ldapLoginManager.login(loginForm);
        System.out.println(loginResult);
    }

    @Test
    public void authenticationTest() {
        String uid = "zhangsan";
        String password = "123456";
        ldapTemplate.authenticate(LdapQueryBuilder.query().where("uid").is(uid), password);
    }

    @Test
    public void get() {
        String uid = "zhangsan";
        LdapUser ldapUser = ldapTemplate.findOne(LdapQueryBuilder.query().where("uid").is(uid), LdapUser.class);
        System.out.println(ldapUser);
    }

    @Test
    public void authenticationTest2() {
        String uid = "ldapUser";
        String password = "123456";
        LdapUser authenticate = ldapTemplate.authenticate(
                LdapQueryBuilder.query().where("uid").is(uid),
                password,
                (dirContext, ldapEntryIdentification) ->
                        ldapTemplate.findOne(LdapQueryBuilder.query().where("uid").is(uid), LdapUser.class));
        System.out.println(authenticate);

    }

}
