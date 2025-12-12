package com.contacts.repository;

import com.contacts.entity.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 联系人数据访问接口
 * 继承 JpaRepository 获得基本的CRUD操作
 */
@Repository
public interface ContactRepository extends JpaRepository<Contact, Long> {

    /**
     * 根据姓名模糊查询联系人
     * Spring Data JPA 会自动实现这个方法
     * @param name 姓名关键词
     * @return 匹配的联系人列表
     */
    List<Contact> findByNameContaining(String name);

    /**
     * 根据电话号码模糊查询联系人
     * @param phone 电话号码关键词
     * @return 匹配的联系人列表
     */
    List<Contact> findByPhoneContaining(String phone);

    /**
     * 根据邮箱查询联系人
     * @param email 邮箱地址
     * @return 匹配的联系人列表
     */
    List<Contact> findByEmail(String email);

    /**
     * 根据公司名称模糊查询联系人
     * @param company 公司名称关键词
     * @return 匹配的联系人列表
     */
    List<Contact> findByCompanyContaining(String company);

    /**
     * 根据收藏状态查询联系人
     * @param bookmarked 是否收藏
     * @return 匹配的联系人列表
     */
    List<Contact> findByBookmarked(Boolean bookmarked);

    /**
     * 查询收藏的联系人
     * @return 收藏的联系人列表
     */
    List<Contact> findByBookmarkedTrue();

    /**
     * 查询未收藏的联系人
     * @return 未收藏的联系人列表
     */
    List<Contact> findByBookmarkedFalse();

    /**
     * 自定义查询：根据姓名和电话查询联系人
     * @param name 姓名关键词
     * @param phone 电话号码关键词
     * @return 匹配的联系人列表
     */
    @Query("SELECT c FROM Contact c WHERE c.name LIKE %:name% AND c.phone LIKE %:phone%")
    List<Contact> findByNameAndPhone(@Param("name") String name, @Param("phone") String phone);

    /**
     * 自定义查询：搜索姓名或电话包含关键词的联系人
     * @param keyword 搜索关键词
     * @return 匹配的联系人列表
     */
    @Query("SELECT c FROM Contact c WHERE c.name LIKE %:keyword% OR c.phone LIKE %:keyword%")
    List<Contact> searchByNameOrPhone(@Param("keyword") String keyword);

    /**
     * 自定义查询：搜索姓名、电话或邮箱包含关键词的联系人
     * @param keyword 搜索关键词
     * @return 匹配的联系人列表
     */
    @Query("SELECT c FROM Contact c WHERE c.name LIKE %:keyword% OR c.phone LIKE %:keyword% OR c.email LIKE %:keyword%")
    List<Contact> searchByNameOrPhoneOrEmail(@Param("keyword") String keyword);

    /**
     * 检查电话号码是否已存在（用于新增时的验证）
     * @param phone 电话号码
     * @return 如果存在返回true，否则返回false
     */
    boolean existsByPhone(String phone);

    /**
     * 检查电话号码是否已存在（排除指定ID，用于更新时的验证）
     * @param phone 电话号码
     * @param id 要排除的联系人ID
     * @return 如果存在返回true，否则返回false
     */
    @Query("SELECT CASE WHEN COUNT(c) > 0 THEN true ELSE false END FROM Contact c WHERE c.phone = :phone AND c.id != :id")
    boolean existsByPhoneAndIdNot(@Param("phone") String phone, @Param("id") Long id);
}
