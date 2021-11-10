package cn.torna.service.login.form.impl;

import org.springframework.ldap.core.AttributesMapper;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;

public class LdapUserAttributeMapper implements AttributesMapper<LdapUser> {

    /**
     * 将单个Attributes转成单个对象
     *
     * @param attrs
     * @return
     * @throws NamingException
     */
    public LdapUser mapFromAttributes(Attributes attrs) throws NamingException {
        LdapUser user = new LdapUser();
        if (attrs.get("uid") != null) {
            user.setUid(attrs.get("uid").get().toString());
        }
        if (attrs.get("mail") != null) {
            user.setMail(attrs.get("mail").get().toString());
        }
        return user;
    }
}