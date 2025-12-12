package com.contacts.service.impl;

import com.contacts.entity.Contact;
import com.contacts.repository.ContactRepository;
import com.contacts.service.ContactService;
import com.contacts.exception.ContactNotFoundException;
import com.contacts.exception.DuplicatePhoneException;
import com.contacts.exception.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 联系人业务逻辑实现类
 * 实现 ContactService 接口中定义的所有业务方法
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class ContactServiceImpl implements ContactService {

    // 使用构造器注入 Repository
    private final ContactRepository contactRepository;

    /**
     * 获取所有联系人列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<Contact> getAllContacts() {
        log.info("Service: 开始获取所有联系人列表");
        try {
            List<Contact> contacts = contactRepository.findAll();
            log.info("Service: 成功获取 {} 个联系人", contacts.size());
            return contacts;
        } catch (Exception e) {
            log.error("Service: 获取联系人列表失败", e);
            throw new RuntimeException("获取联系人列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取收藏的联系人列表
     */
    @Override
    @Transactional(readOnly = true)
    public List<Contact> getBookmarkedContacts() {
        log.info("Service: 开始获取收藏的联系人列表");
        try {
            List<Contact> contacts = contactRepository.findByBookmarkedTrue();
            log.info("Service: 成功获取 {} 个收藏的联系人", contacts.size());
            return contacts;
        } catch (Exception e) {
            log.error("Service: 获取收藏联系人列表失败", e);
            throw new RuntimeException("获取收藏联系人列表失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据ID获取联系人详情
     */
    @Override
    @Transactional(readOnly = true)
    public Contact getContactById(Long id) {
        log.info("Service: 根据ID获取联系人，ID: {}", id);

        // 参数验证
        if (id == null) {
            throw new ValidationException("联系人ID不能为空");
        }
        if (id <= 0) {
            throw new ValidationException("联系人ID必须大于0");
        }

        try {
            Optional<Contact> contact = contactRepository.findById(id);
            if (contact.isPresent()) {
                log.info("Service: 成功获取联系人: {} (ID: {})", contact.get().getName(), id);
                return contact.get();
            } else {
                log.warn("Service: 联系人不存在，ID: {}", id);
                throw new ContactNotFoundException(id);
            }
        } catch (ContactNotFoundException e) {
            // 重新抛出自定义异常
            throw e;
        } catch (Exception e) {
            log.error("Service: 获取联系人失败，ID: {}", id, e);
            throw new RuntimeException("获取联系人失败: " + e.getMessage(), e);
        }
    }

    /**
     * 创建新联系人
     */
    @Override
    public Contact createContact(Contact contact) {
        log.info("Service: 开始创建新联系人: {}", contact.getName());

        // 参数验证
        validateContact(contact);

        // 检查电话号码是否已存在
        if (contactRepository.existsByPhone(contact.getPhone())) {
            log.warn("Service: 电话号码已存在: {}", contact.getPhone());
            throw new DuplicatePhoneException(contact.getPhone());
        }

        try {
            // 确保ID为null，让数据库自动生成
            contact.setId(null);
            // 确保收藏状态有默认值
            if (contact.getBookmarked() == null) {
                contact.setBookmarked(false);
            }

            Contact savedContact = contactRepository.save(contact);
            log.info("Service: 成功创建联系人: {} (ID: {})", savedContact.getName(), savedContact.getId());
            return savedContact;
        } catch (Exception e) {
            log.error("Service: 创建联系人失败: {}", contact.getName(), e);
            throw new RuntimeException("创建联系人失败: " + e.getMessage(), e);
        }
    }

    /**
     * 更新联系人信息
     */
    @Override
    public Contact updateContact(Long id, Contact contactDetails) {
        log.info("Service: 开始更新联系人，ID: {}", id);

        // 参数验证
        if (id == null || id <= 0) {
            throw new ValidationException("联系人ID不能为空且必须大于0");
        }
        validateContact(contactDetails);

        try {
            // 检查联系人是否存在
            Optional<Contact> optionalContact = contactRepository.findById(id);
            if (optionalContact.isEmpty()) {
                log.warn("Service: 要更新的联系人不存在，ID: {}", id);
                throw new ContactNotFoundException(id);
            }

            Contact existingContact = optionalContact.get();

            // 如果电话号码有变化，检查新号码是否已被其他联系人使用
            if (!existingContact.getPhone().equals(contactDetails.getPhone())) {
                if (contactRepository.existsByPhoneAndIdNot(contactDetails.getPhone(), id)) {
                    log.warn("Service: 电话号码已被其他联系人使用: {}", contactDetails.getPhone());
                    throw new DuplicatePhoneException(contactDetails.getPhone());
                }
            }

            // 更新联系人信息（保留收藏状态）
            updateContactFields(existingContact, contactDetails);

            Contact updatedContact = contactRepository.save(existingContact);
            log.info("Service: 成功更新联系人: {} (ID: {})", updatedContact.getName(), updatedContact.getId());
            return updatedContact;
        } catch (ContactNotFoundException | DuplicatePhoneException e) {
            // 重新抛出自定义异常
            throw e;
        } catch (Exception e) {
            log.error("Service: 更新联系人失败，ID: {}", id, e);
            throw new RuntimeException("更新联系人失败: " + e.getMessage(), e);
        }
    }

    /**
     * 删除联系人
     */
    @Override
    public void deleteContact(Long id) {
        log.info("Service: 开始删除联系人，ID: {}", id);

        // 参数验证
        if (id == null || id <= 0) {
            throw new ValidationException("联系人ID不能为空且必须大于0");
        }

        try {
            // 检查联系人是否存在
            if (!contactRepository.existsById(id)) {
                log.warn("Service: 要删除的联系人不存在，ID: {}", id);
                throw new ContactNotFoundException(id);
            }

            contactRepository.deleteById(id);
            log.info("Service: 成功删除联系人，ID: {}", id);
        } catch (ContactNotFoundException e) {
            // 重新抛出自定义异常
            throw e;
        } catch (Exception e) {
            log.error("Service: 删除联系人失败，ID: {}", id, e);
            throw new RuntimeException("删除联系人失败: " + e.getMessage(), e);
        }
    }

    /**
     * 搜索联系人
     */
    @Override
    @Transactional(readOnly = true)
    public List<Contact> searchContacts(String keyword) {
        log.info("Service: 开始搜索联系人，关键词: '{}'", keyword);

        if (keyword == null || keyword.trim().isEmpty()) {
            log.info("Service: 搜索关键词为空，返回所有联系人");
            return getAllContacts();
        }

        String trimmedKeyword = keyword.trim();
        try {
            List<Contact> contacts = contactRepository.searchByNameOrPhone(trimmedKeyword);
            log.info("Service: 搜索到 {} 个匹配的联系人", contacts.size());
            return contacts;
        } catch (Exception e) {
            log.error("Service: 搜索联系人失败，关键词: {}", trimmedKeyword, e);
            throw new RuntimeException("搜索联系人失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据姓名搜索联系人
     */
    @Override
    @Transactional(readOnly = true)
    public List<Contact> searchContactsByName(String name) {
        log.info("Service: 根据姓名搜索联系人: '{}'", name);

        if (name == null || name.trim().isEmpty()) {
            return getAllContacts();
        }

        try {
            List<Contact> contacts = contactRepository.findByNameContaining(name.trim());
            log.info("Service: 根据姓名搜索到 {} 个联系人", contacts.size());
            return contacts;
        } catch (Exception e) {
            log.error("Service: 根据姓名搜索联系人失败: {}", name, e);
            throw new RuntimeException("根据姓名搜索联系人失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据电话搜索联系人
     */
    @Override
    @Transactional(readOnly = true)
    public List<Contact> searchContactsByPhone(String phone) {
        log.info("Service: 根据电话搜索联系人: '{}'", phone);

        if (phone == null || phone.trim().isEmpty()) {
            return getAllContacts();
        }

        try {
            List<Contact> contacts = contactRepository.findByPhoneContaining(phone.trim());
            log.info("Service: 根据电话搜索到 {} 个联系人", contacts.size());
            return contacts;
        } catch (Exception e) {
            log.error("Service: 根据电话搜索联系人失败: {}", phone, e);
            throw new RuntimeException("根据电话搜索联系人失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查电话号码是否已存在
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isPhoneExists(String phone) {
        log.debug("Service: 检查电话号码是否存在: '{}'", phone);

        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        try {
            boolean exists = contactRepository.existsByPhone(phone.trim());
            log.debug("Service: 电话号码 '{}' 存在: {}", phone, exists);
            return exists;
        } catch (Exception e) {
            log.error("Service: 检查电话号码是否存在失败: {}", phone, e);
            throw new RuntimeException("检查电话号码失败: " + e.getMessage(), e);
        }
    }

    /**
     * 检查电话号码是否已存在（排除指定ID）
     */
    @Override
    @Transactional(readOnly = true)
    public boolean isPhoneExists(String phone, Long excludeId) {
        log.debug("Service: 检查电话号码是否存在（排除ID: {}）: '{}'", excludeId, phone);

        if (phone == null || phone.trim().isEmpty()) {
            return false;
        }

        if (excludeId == null || excludeId <= 0) {
            return isPhoneExists(phone);
        }

        try {
            boolean exists = contactRepository.existsByPhoneAndIdNot(phone.trim(), excludeId);
            log.debug("Service: 电话号码 '{}' 存在（排除ID: {}）: {}", phone, excludeId, exists);
            return exists;
        } catch (Exception e) {
            log.error("Service: 检查电话号码是否存在失败: {} (排除ID: {})", phone, excludeId, e);
            throw new RuntimeException("检查电话号码失败: " + e.getMessage(), e);
        }
    }

    /**
     * 收藏联系人
     */
    @Override
    public Contact bookmarkContact(Long id) {
        log.info("Service: 开始收藏联系人，ID: {}", id);

        // 参数验证
        if (id == null || id <= 0) {
            throw new ValidationException("联系人ID不能为空且必须大于0");
        }

        try {
            // 检查联系人是否存在
            Optional<Contact> optionalContact = contactRepository.findById(id);
            if (optionalContact.isEmpty()) {
                log.warn("Service: 要收藏的联系人不存在，ID: {}", id);
                throw new ContactNotFoundException(id);
            }

            Contact contact = optionalContact.get();

            // 如果已经是收藏状态，直接返回
            if (Boolean.TRUE.equals(contact.getBookmarked())) {
                log.info("Service: 联系人已经是收藏状态，ID: {}", id);
                return contact;
            }

            // 设置为收藏
            contact.setBookmarked(true);
            Contact updatedContact = contactRepository.save(contact);

            log.info("Service: 成功收藏联系人: {} (ID: {})", updatedContact.getName(), updatedContact.getId());
            return updatedContact;
        } catch (ContactNotFoundException e) {
            // 重新抛出自定义异常
            throw e;
        } catch (Exception e) {
            log.error("Service: 收藏联系人失败，ID: {}", id, e);
            throw new RuntimeException("收藏联系人失败: " + e.getMessage(), e);
        }
    }

    /**
     * 取消收藏联系人
     */
    @Override
    public Contact unbookmarkContact(Long id) {
        log.info("Service: 开始取消收藏联系人，ID: {}", id);

        // 参数验证
        if (id == null || id <= 0) {
            throw new ValidationException("联系人ID不能为空且必须大于0");
        }

        try {
            // 检查联系人是否存在
            Optional<Contact> optionalContact = contactRepository.findById(id);
            if (optionalContact.isEmpty()) {
                log.warn("Service: 要取消收藏的联系人不存在，ID: {}", id);
                throw new ContactNotFoundException(id);
            }

            Contact contact = optionalContact.get();

            // 如果已经是不收藏状态，直接返回
            if (Boolean.FALSE.equals(contact.getBookmarked())) {
                log.info("Service: 联系人已经是不收藏状态，ID: {}", id);
                return contact;
            }

            // 设置为不收藏
            contact.setBookmarked(false);
            Contact updatedContact = contactRepository.save(contact);

            log.info("Service: 成功取消收藏联系人: {} (ID: {})", updatedContact.getName(), updatedContact.getId());
            return updatedContact;
        } catch (ContactNotFoundException e) {
            // 重新抛出自定义异常
            throw e;
        } catch (Exception e) {
            log.error("Service: 取消收藏联系人失败，ID: {}", id, e);
            throw new RuntimeException("取消收藏联系人失败: " + e.getMessage(), e);
        }
    }

    /**
     * 切换收藏状态
     */
    @Override
    public Contact toggleBookmark(Long id) {
        log.info("Service: 开始切换联系人收藏状态，ID: {}", id);

        // 参数验证
        if (id == null || id <= 0) {
            throw new ValidationException("联系人ID不能为空且必须大于0");
        }

        try {
            // 检查联系人是否存在
            Optional<Contact> optionalContact = contactRepository.findById(id);
            if (optionalContact.isEmpty()) {
                log.warn("Service: 要切换收藏状态的联系人不存在，ID: {}", id);
                throw new ContactNotFoundException(id);
            }

            Contact contact = optionalContact.get();

            // 切换收藏状态
            boolean newBookmarkState = !Boolean.TRUE.equals(contact.getBookmarked());
            contact.setBookmarked(newBookmarkState);
            Contact updatedContact = contactRepository.save(contact);

            String action = newBookmarkState ? "收藏" : "取消收藏";
            log.info("Service: 成功{}联系人: {} (ID: {})", action, updatedContact.getName(), updatedContact.getId());
            return updatedContact;
        } catch (ContactNotFoundException e) {
            // 重新抛出自定义异常
            throw e;
        } catch (Exception e) {
            log.error("Service: 切换收藏状态失败，ID: {}", id, e);
            throw new RuntimeException("切换收藏状态失败: " + e.getMessage(), e);
        }
    }

    // ========== 私有辅助方法 ==========

    /**
     * 验证联系人信息的有效性
     * @param contact 联系人对象
     */
    private void validateContact(Contact contact) {
        if (contact == null) {
            throw new ValidationException("联系人信息不能为空");
        }

        // 验证姓名
        if (contact.getName() == null || contact.getName().trim().isEmpty()) {
            throw new ValidationException("联系人姓名不能为空");
        }
        String name = contact.getName().trim();
        if (name.length() > 100) {
            throw new ValidationException("联系人姓名长度不能超过100个字符");
        }
        contact.setName(name); // 去除前后空格

        // 验证电话
        if (contact.getPhone() == null || contact.getPhone().trim().isEmpty()) {
            throw new ValidationException("联系人电话不能为空");
        }

        String phone = contact.getPhone().trim();
        // 简单的电话格式验证：支持手机号和座机号
        if (!isValidPhoneNumber(phone)) {
            throw new ValidationException("电话号码格式不正确");
        }
        contact.setPhone(phone); // 去除前后空格

        // 验证邮箱（如果提供了邮箱）
        if (contact.getEmail() != null && !contact.getEmail().trim().isEmpty()) {
            String email = contact.getEmail().trim();
            if (!isValidEmail(email)) {
                throw new ValidationException("邮箱格式不正确");
            }
            contact.setEmail(email); // 去除前后空格
        } else {
            contact.setEmail(null); // 确保空字符串转为null
        }

        // 验证微信账号
        if (contact.getWechat() != null && !contact.getWechat().trim().isEmpty()) {
            String wechat = contact.getWechat().trim();
            if (wechat.length() > 50) {
                throw new ValidationException("微信账号长度不能超过50个字符");
            }
            contact.setWechat(wechat);
        } else {
            contact.setWechat(null);
        }

        // 验证QQ账号
        if (contact.getQq() != null && !contact.getQq().trim().isEmpty()) {
            String qq = contact.getQq().trim();
            if (!isValidQQ(qq)) {
                throw new ValidationException("QQ账号格式不正确");
            }
            if (qq.length() > 20) {
                throw new ValidationException("QQ账号长度不能超过20个字符");
            }
            contact.setQq(qq);
        } else {
            contact.setQq(null);
        }

        // 验证地址长度
        if (contact.getAddress() != null) {
            String address = contact.getAddress().trim();
            if (address.length() > 200) {
                throw new ValidationException("地址长度不能超过200个字符");
            }
            contact.setAddress(address.isEmpty() ? null : address); // 去除前后空格，空字符串转为null
        }

        // 验证公司名称长度
        if (contact.getCompany() != null) {
            String company = contact.getCompany().trim();
            if (company.length() > 100) {
                throw new ValidationException("公司名称长度不能超过100个字符");
            }
            contact.setCompany(company.isEmpty() ? null : company); // 去除前后空格，空字符串转为null
        }

        // 验证收藏状态（确保不为null）
        if (contact.getBookmarked() == null) {
            contact.setBookmarked(false);
        }
    }

    /**
     * 验证电话号码格式
     * @param phone 电话号码
     * @return 是否有效
     */
    private boolean isValidPhoneNumber(String phone) {
        // 手机号：1开头，11位数字
        String mobileRegex = "^1[3-9]\\d{9}$";
        // 座机号：区号-号码，如 010-12345678 或 010-1234567
        String landlineRegex = "^\\d{3,4}-\\d{7,8}$";
        // 简单数字验证（宽松模式）
        String simpleRegex = "^\\d{5,20}$";

        return phone.matches(mobileRegex) || phone.matches(landlineRegex) || phone.matches(simpleRegex);
    }

    /**
     * 验证邮箱格式
     * @param email 邮箱地址
     * @return 是否有效
     */
    private boolean isValidEmail(String email) {
        // 简单的邮箱格式验证
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";
        return email.matches(emailRegex);
    }

    // 添加QQ账号验证方法
    private boolean isValidQQ(String qq) {
        // QQ号规则：5-11位数字，不能以0开头
        String qqRegex = "^[1-9][0-9]{4,10}$";
        return qq.matches(qqRegex);
    }

    /**
     * 更新联系人字段
     * @param existing 已存在的联系人
     * @param updates 更新的数据
     */
    private void updateContactFields(Contact existing, Contact updates) {
        existing.setName(updates.getName());
        existing.setPhone(updates.getPhone());
        existing.setEmail(updates.getEmail());
        existing.setAddress(updates.getAddress());
        existing.setCompany(updates.getCompany());
        existing.setWechat(updates.getWechat());
        existing.setQq(updates.getQq());
        // 不更新收藏状态，收藏状态有专门的接口处理
        // existing.setBookmarked(updates.getBookmarked());

        // 如果更新了头像，也要更新
        if (updates.getAvatar() != null) {
            existing.setAvatar(updates.getAvatar());
        }
    }
}
