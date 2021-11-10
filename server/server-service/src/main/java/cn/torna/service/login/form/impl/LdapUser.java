package cn.torna.service.login.form.impl;

import lombok.Data;
import org.springframework.ldap.odm.annotations.Attribute;
import org.springframework.ldap.odm.annotations.DnAttribute;
import org.springframework.ldap.odm.annotations.Entry;
import org.springframework.ldap.odm.annotations.Id;

import javax.naming.Name;

@Data
@Entry(objectClasses = "inetOrgPerson")
public class LdapUser {

    @Id
    private Name id;

    /**
     * 登录名
     */
    @DnAttribute(value = "uid")
    private String uid;

    /**
     * 显示名称，可当做昵称
     */
    @Attribute(name = "displayName")
    private String displayName;

    /**
     * 邮箱
     */
    @Attribute(name="mail")
    private String mail;






}
