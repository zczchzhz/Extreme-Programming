// 在 src/test/java/com/contacts/entity/ContactTest.java
package com.contacts.entity;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class ContactTest {

    @Test
    public void testContactCreation() {
        Contact contact = new Contact();
        contact.setName("测试姓名");
        contact.setPhone("13800138000");

        assertEquals("测试姓名", contact.getName());
        assertEquals("13800138000", contact.getPhone());
    }
}
