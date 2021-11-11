package cn.torna;

import cn.torna.service.login.form.impl.LdapUser;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.query.LdapQueryBuilder;

/**
 * application.properties:
 *
 * spring.ldap.urls=ldap://172.16.6.64:389
 * spring.ldap.base=dc=torna,dc=cn
 * spring.ldap.username=cn=Manager,${spring.ldap.base}
 * spring.ldap.password=123456
 *
 */
public class LdapTest extends TornaApplicationTests {

    @Autowired
    private LdapTemplate ldapTemplate;

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
