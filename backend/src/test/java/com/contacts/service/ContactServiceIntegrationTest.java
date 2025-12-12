package com.contacts.service;

import com.contacts.entity.Contact;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
@Transactional
public class ContactServiceIntegrationTest {

    @Autowired
    private ContactService contactService;

    @Test
    public void testCreateAndRetrieveContact() {
        // 创建联系人
        Contact contact = new Contact();
        contact.setName("集成测试用户");
        contact.setPhone("15012345678");
        contact.setEmail("test@example.com");
        contact.setAddress("测试地址");
        contact.setCompany("测试公司");

        // 保存联系人
        Contact savedContact = contactService.createContact(contact);

        // 验证保存成功
        assertNotNull(savedContact.getId());
        assertEquals("集成测试用户", savedContact.getName());

        // 根据ID检索
        Contact retrievedContact = contactService.getContactById(savedContact.getId());
        assertNotNull(retrievedContact);
        assertEquals(savedContact.getId(), retrievedContact.getId());
        assertEquals("集成测试用户", retrievedContact.getName());
    }

    @Test
    public void testUpdateContact() {
        // 先创建联系人
        Contact contact = new Contact();
        contact.setName("原始名称");
        contact.setPhone("15112345678");
        Contact savedContact = contactService.createContact(contact);

        // 更新联系人
        Contact updateData = new Contact();
        updateData.setName("更新后的名称");
        updateData.setPhone("15212345678");
        updateData.setEmail("updated@example.com");

        Contact updatedContact = contactService.updateContact(savedContact.getId(), updateData);

        // 验证更新成功
        assertEquals("更新后的名称", updatedContact.getName());
        assertEquals("15212345678", updatedContact.getPhone());
        assertEquals("updated@example.com", updatedContact.getEmail());
    }

    @Test
    public void testDeleteContact() {
        // 创建联系人
        Contact contact = new Contact();
        contact.setName("待删除用户");
        contact.setPhone("15312345678");
        Contact savedContact = contactService.createContact(contact);

        Long contactId = savedContact.getId();

        // 删除联系人
        contactService.deleteContact(contactId);

        // 验证删除后无法找到
        assertThrows(RuntimeException.class, () -> {
            contactService.getContactById(contactId);
        });
    }

    @Test
    public void testSearchContacts() {
        // 创建测试数据
        Contact contact1 = new Contact("搜索测试张三", "15412345678", "zhangsan@example.com", "北京", "A公司");
        Contact contact2 = new Contact("搜索测试李四", "15512345678", "lisi@example.com", "上海", "B公司");

        contactService.createContact(contact1);
        contactService.createContact(contact2);

        // 搜索测试
        List<Contact> results = contactService.searchContacts("搜索测试");
        assertTrue(results.size() >= 2);

        List<Contact> phoneResults = contactService.searchContacts("15412345678");
        assertEquals(1, phoneResults.size());
        assertEquals("搜索测试张三", phoneResults.get(0).getName());
    }
}