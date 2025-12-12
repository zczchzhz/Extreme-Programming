package com.contacts.repository;

import com.contacts.entity.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
public class ContactRepositoryTest {

    @Autowired
    private ContactRepository contactRepository;

    @Test
    public void testSaveAndFindContact() {
        // 创建联系人
        Contact contact = new Contact();
        contact.setName("测试用户");
        contact.setPhone("13800138000");
        contact.setEmail("test@example.com");

        // 保存联系人
        Contact savedContact = contactRepository.save(contact);

        // 验证保存成功
        assertNotNull(savedContact.getId());
        assertEquals("测试用户", savedContact.getName());

        // 查找所有联系人
        List<Contact> contacts = contactRepository.findAll();
        assertFalse(contacts.isEmpty());
    }

    @Test
    public void testFindByNameContaining() {
        // 准备测试数据
        Contact contact1 = new Contact("张三", "13800138000", "zhangsan@example.com", "北京", "A公司");
        Contact contact2 = new Contact("张四", "13900139000", "lisi@example.com", "上海", "B公司");

        contactRepository.save(contact1);
        contactRepository.save(contact2);

        // 测试查询
        List<Contact> contacts = contactRepository.findByNameContaining("张");

        // 验证结果
        assertEquals(2, contacts.size());
    }
}
