package com.contacts.controller;

import com.contacts.entity.Contact;
import com.contacts.service.ContactService;
import com.contacts.utils.ExcelUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/contacts")
@RequiredArgsConstructor
public class ContactController {

    private final ContactService contactService;

    /**
     * 获取所有联系人
     */
    @GetMapping
    public ResponseEntity<List<Contact>> getAllContacts() {
        try {
            List<Contact> contacts = contactService.getAllContacts();
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            log.error("获取联系人列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 获取收藏的联系人
     */
    @GetMapping("/bookmarked")
    public ResponseEntity<List<Contact>> getBookmarkedContacts() {
        try {
            List<Contact> contacts = contactService.getBookmarkedContacts();
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            log.error("获取收藏联系人列表失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 根据ID获取联系人
     */
    @GetMapping("/{id}")
    public ResponseEntity<Contact> getContactById(@PathVariable Long id) {
        try {
            Contact contact = contactService.getContactById(id);
            return ResponseEntity.ok(contact);
        } catch (RuntimeException e) {
            log.error("获取联系人失败，ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("获取联系人失败，ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 创建联系人
     */
    @PostMapping
    public ResponseEntity<Contact> createContact(@RequestBody Contact contact) {
        try {
            Contact createdContact = contactService.createContact(contact);
            return ResponseEntity.status(HttpStatus.CREATED).body(createdContact);
        } catch (RuntimeException e) {
            log.error("创建联系人失败", e);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("创建联系人失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 更新联系人
     */
    @PutMapping("/{id}")
    public ResponseEntity<Contact> updateContact(@PathVariable Long id, @RequestBody Contact contact) {
        try {
            Contact updatedContact = contactService.updateContact(id, contact);
            return ResponseEntity.ok(updatedContact);
        } catch (RuntimeException e) {
            log.error("更新联系人失败，ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("更新联系人失败，ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 删除联系人
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContact(@PathVariable Long id) {
        try {
            contactService.deleteContact(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            log.error("删除联系人失败，ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("删除联系人失败，ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 搜索联系人
     */
    @GetMapping("/search")
    public ResponseEntity<List<Contact>> searchContacts(@RequestParam(required = false) String keyword) {
        try {
            List<Contact> contacts = contactService.searchContacts(keyword);
            return ResponseEntity.ok(contacts);
        } catch (Exception e) {
            log.error("搜索联系人失败，关键词: {}", keyword, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 收藏联系人
     */
    @PutMapping("/{id}/bookmark")
    public ResponseEntity<Contact> bookmarkContact(@PathVariable Long id) {
        try {
            Contact updatedContact = contactService.bookmarkContact(id);
            return ResponseEntity.ok(updatedContact);
        } catch (RuntimeException e) {
            log.error("收藏联系人失败，ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("收藏联系人失败，ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 取消收藏联系人
     */
    @DeleteMapping("/{id}/bookmark")
    public ResponseEntity<Contact> unbookmarkContact(@PathVariable Long id) {
        try {
            Contact updatedContact = contactService.unbookmarkContact(id);
            return ResponseEntity.ok(updatedContact);
        } catch (RuntimeException e) {
            log.error("取消收藏联系人失败，ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("取消收藏联系人失败，ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 切换收藏状态
     */
    @PatchMapping("/{id}/bookmark/toggle")
    public ResponseEntity<Contact> toggleBookmark(@PathVariable Long id) {
        try {
            Contact updatedContact = contactService.toggleBookmark(id);
            return ResponseEntity.ok(updatedContact);
        } catch (RuntimeException e) {
            log.error("切换收藏状态失败，ID: {}", id, e);
            return ResponseEntity.notFound().build();
        } catch (Exception e) {
            log.error("切换收藏状态失败，ID: {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportToExcel() {
        try {
            List<Contact> contacts = contactService.getAllContacts();

            if (contacts.isEmpty()) {
                return ResponseEntity.noContent().build();
            }

            byte[] excelBytes = ExcelUtil.exportToExcel(contacts);

            String filename = "contacts_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss")) +
                    ".xlsx";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", filename);
            headers.setContentLength(excelBytes.length);

            return new ResponseEntity<>(excelBytes, headers, HttpStatus.OK);

        } catch (IOException e) {
            log.error("导出Excel失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 从Excel导入联系人
     */
    @PostMapping("/import/excel")
    public ResponseEntity<String> importFromExcel(@RequestParam("file") MultipartFile file) {
        try {
            if (file.isEmpty()) {
                return ResponseEntity.badRequest().body("文件不能为空");
            }

            String filename = file.getOriginalFilename();
            if (filename == null ||
                    (!filename.endsWith(".xlsx") && !filename.endsWith(".xls"))) {
                return ResponseEntity.badRequest().body("只支持Excel文件 (.xlsx, .xls)");
            }

            List<Contact> contacts = ExcelUtil.importFromExcel(file);

            if (contacts.isEmpty()) {
                return ResponseEntity.badRequest().body("Excel文件中没有有效的联系人数据");
            }

            int successCount = 0;
            int errorCount = 0;
            StringBuilder errors = new StringBuilder();

            for (int i = 0; i < contacts.size(); i++) {
                Contact contact = contacts.get(i);
                try {
                    contactService.createContact(contact);
                    successCount++;
                } catch (Exception e) {
                    errorCount++;
                    errors.append("第").append(i + 2).append("行: ")
                            .append(contact.getName()).append(" - ")
                            .append(e.getMessage()).append("\n");
                }
            }

            String message = String.format(
                    "导入完成！成功导入 %d 个联系人，失败 %d 个",
                    successCount, errorCount
            );

            if (errorCount > 0) {
                message += "\n\n错误详情:\n" + errors.toString();
            }

            return ResponseEntity.ok(message);

        } catch (IOException e) {
            log.error("导入Excel失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("导入失败: " + e.getMessage());
        } catch (Exception e) {
            log.error("处理Excel文件失败", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("处理文件失败: " + e.getMessage());
        }
    }
}
