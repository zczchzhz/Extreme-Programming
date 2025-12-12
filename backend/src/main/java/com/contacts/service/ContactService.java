package com.contacts.service;

import com.contacts.entity.Contact;
import java.util.List;

/**
 * 联系人业务逻辑接口
 * 定义联系人相关的业务操作方法
 */
public interface ContactService {

    /**
     * 获取所有联系人列表
     * @return 联系人列表
     */
    List<Contact> getAllContacts();

    /**
     * 获取收藏的联系人列表
     * @return 收藏的联系人列表
     */
    List<Contact> getBookmarkedContacts();

    /**
     * 根据ID获取联系人详情
     * @param id 联系人ID
     * @return 联系人对象
     * @throws RuntimeException 当联系人不存在时抛出异常
     */
    Contact getContactById(Long id);

    /**
     * 创建新联系人
     * @param contact 联系人信息
     * @return 创建后的联系人对象
     * @throws RuntimeException 当电话号码已存在时抛出异常
     */
    Contact createContact(Contact contact);

    /**
     * 更新联系人信息
     * @param id 联系人ID
     * @param contact 更新的联系人信息
     * @return 更新后的联系人对象
     * @throws RuntimeException 当联系人不存在时抛出异常
     */
    Contact updateContact(Long id, Contact contact);

    /**
     * 删除联系人
     * @param id 联系人ID
     * @throws RuntimeException 当联系人不存在时抛出异常
     */
    void deleteContact(Long id);

    /**
     * 搜索联系人
     * @param keyword 搜索关键词（姓名、电话）
     * @return 匹配的联系人列表
     */
    List<Contact> searchContacts(String keyword);

    /**
     * 根据姓名搜索联系人
     * @param name 姓名关键词
     * @return 匹配的联系人列表
     */
    List<Contact> searchContactsByName(String name);

    /**
     * 根据电话搜索联系人
     * @param phone 电话关键词
     * @return 匹配的联系人列表
     */
    List<Contact> searchContactsByPhone(String phone);

    /**
     * 检查电话号码是否已存在
     * @param phone 电话号码
     * @return 如果存在返回true，否则返回false
     */
    boolean isPhoneExists(String phone);

    /**
     * 检查电话号码是否已存在（排除指定ID）
     * @param phone 电话号码
     * @param excludeId 要排除的联系人ID
     * @return 如果存在返回true，否则返回false
     */
    boolean isPhoneExists(String phone, Long excludeId);

    /**
     * 收藏联系人
     * @param id 联系人ID
     * @return 更新后的联系人对象
     * @throws RuntimeException 当联系人不存在时抛出异常
     */
    Contact bookmarkContact(Long id);

    /**
     * 取消收藏联系人
     * @param id 联系人ID
     * @return 更新后的联系人对象
     * @throws RuntimeException 当联系人不存在时抛出异常
     */
    Contact unbookmarkContact(Long id);

    /**
     * 切换收藏状态
     * @param id 联系人ID
     * @return 更新后的联系人对象
     * @throws RuntimeException 当联系人不存在时抛出异常
     */
    Contact toggleBookmark(Long id);
}
