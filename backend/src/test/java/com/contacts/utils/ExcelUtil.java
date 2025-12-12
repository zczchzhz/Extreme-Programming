package com.contacts.utils;

import com.contacts.entity.Contact;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelUtil {

    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /**
     * 导出联系人到Excel
     */
    public static byte[] exportToExcel(List<Contact> contacts) throws IOException {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // 创建工作表
            Sheet sheet = workbook.createSheet("联系人");

            // 创建表头样式
            CellStyle headerStyle = createHeaderStyle(workbook);

            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {
                    "ID", "姓名", "电话", "邮箱", "微信", "QQ",
                    "地址", "公司", "收藏", "创建时间", "更新时间"
            };

            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
                sheet.setColumnWidth(i, 20 * 256); // 设置列宽
            }

            // 创建数据行样式
            CellStyle dataStyle = createDataStyle(workbook);

            // 填充数据
            int rowNum = 1;
            for (Contact contact : contacts) {
                Row row = sheet.createRow(rowNum++);

                // ID
                createCell(row, 0, contact.getId().toString(), dataStyle);
                // 姓名
                createCell(row, 1, contact.getName(), dataStyle);
                // 电话
                createCell(row, 2, contact.getPhone(), dataStyle);
                // 邮箱
                createCell(row, 3, contact.getEmail(), dataStyle);
                // 微信
                createCell(row, 4, contact.getWechat(), dataStyle);
                // QQ
                createCell(row, 5, contact.getQq(), dataStyle);
                // 地址
                createCell(row, 6, contact.getAddress(), dataStyle);
                // 公司
                createCell(row, 7, contact.getCompany(), dataStyle);
                // 收藏
                createCell(row, 8, contact.getBookmarked() ? "是" : "否", dataStyle);
                // 创建时间
                createCell(row, 9, formatDateTime(contact.getCreatedTime()), dataStyle);
                // 更新时间
                createCell(row, 10, formatDateTime(contact.getUpdatedTime()), dataStyle);
            }

            // 自动调整列宽（除固定宽度的列）
            for (int i = 0; i < headers.length; i++) {
                if (i != 2 && i != 4 && i != 5) { // 电话、微信、QQ列不调整
                    sheet.autoSizeColumn(i);
                }
            }

            workbook.write(outputStream);
            return outputStream.toByteArray();
        }
    }

    /**
     * 从Excel导入联系人
     */
    public static List<Contact> importFromExcel(MultipartFile file) throws IOException {
        List<Contact> contacts = new ArrayList<>();

        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {

            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rowIterator = sheet.iterator();

            // 跳过表头
            if (rowIterator.hasNext()) {
                rowIterator.next();
            }

            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                // 跳过空行
                if (isRowEmpty(row)) {
                    continue;
                }

                Contact contact = new Contact();

                // 读取数据（根据Excel列的顺序）
                // ID（忽略，由数据库自动生成）

                // 姓名
                if (row.getCell(1) != null) {
                    contact.setName(getCellValue(row.getCell(1)));
                }

                // 电话
                if (row.getCell(2) != null) {
                    contact.setPhone(getCellValue(row.getCell(2)));
                }

                // 邮箱
                if (row.getCell(3) != null) {
                    String email = getCellValue(row.getCell(3));
                    contact.setEmail(email.isEmpty() ? null : email);
                }

                // 微信
                if (row.getCell(4) != null) {
                    String wechat = getCellValue(row.getCell(4));
                    contact.setWechat(wechat.isEmpty() ? null : wechat);
                }

                // QQ
                if (row.getCell(5) != null) {
                    String qq = getCellValue(row.getCell(5));
                    contact.setQq(qq.isEmpty() ? null : qq);
                }

                // 地址
                if (row.getCell(6) != null) {
                    String address = getCellValue(row.getCell(6));
                    contact.setAddress(address.isEmpty() ? null : address);
                }

                // 公司
                if (row.getCell(7) != null) {
                    String company = getCellValue(row.getCell(7));
                    contact.setCompany(company.isEmpty() ? null : company);
                }

                // 收藏状态（从Excel读取，如果没有则为false）
                if (row.getCell(8) != null) {
                    String bookmarked = getCellValue(row.getCell(8));
                    contact.setBookmarked("是".equalsIgnoreCase(bookmarked));
                } else {
                    contact.setBookmarked(false);
                }

                // 只有姓名和电话都有的联系人才会被导入
                if (contact.getName() != null && !contact.getName().isEmpty() &&
                        contact.getPhone() != null && !contact.getPhone().isEmpty()) {
                    contacts.add(contact);
                }
            }
        }

        return contacts;
    }

    /**
     * 创建表头样式
     */
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) 12);
        style.setFont(font);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建数据样式
     */
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setFontHeightInPoints((short) 11);
        style.setFont(font);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setWrapText(true);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        return style;
    }

    /**
     * 创建单元格
     */
    private static void createCell(Row row, int column, String value, CellStyle style) {
        Cell cell = row.createCell(column);
        cell.setCellValue(value != null ? value : "");
        if (style != null) {
            cell.setCellStyle(style);
        }
    }

    /**
     * 获取单元格值
     */
    private static String getCellValue(Cell cell) {
        if (cell == null) {
            return "";
        }

        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    return String.valueOf((long) cell.getNumericCellValue());
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                return cell.getCellFormula();
            default:
                return "";
        }
    }

    /**
     * 判断行是否为空
     */
    private static boolean isRowEmpty(Row row) {
        if (row == null) {
            return true;
        }

        for (int i = 0; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = getCellValue(cell);
                if (value != null && !value.trim().isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * 格式化日期时间
     */
    private static String formatDateTime(LocalDateTime dateTime) {
        return dateTime != null ? dateTime.format(DATE_FORMATTER) : "";
    }
}
