package com.school.utils; // 放置在 utils 包下

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.ExcelWriter;
import com.alibaba.excel.read.listener.PageReadListener;
import com.alibaba.excel.write.metadata.WriteSheet;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Consumer;

/**
 * EasyExcel 工具类
 */
public class ExcelUtil {

    private static final Logger log = LoggerFactory.getLogger(ExcelUtil.class);

    /**
     * 通用列表导出
     *
     * @param response HttpServletResponse 用于设置响应头
     * @param filename 导出的文件名 (例如: "用户信息.xlsx")
     * @param sheetName Sheet 名称 (例如: "用户列表")
     * @param head     表头对应的实体类 (使用 @ExcelProperty 注解)
     * @param data     要导出的数据列表
     * @param <T>      数据类型
     * @throws IOException IO异常
     */
    public static <T> void exportExcel(HttpServletResponse response, String filename, String sheetName, Class<T> head, List<T> data) throws IOException {
        setExcelResponseHeaders(response, filename);
        EasyExcel.write(response.getOutputStream(), head)
                .sheet(sheetName)
                .doWrite(data);
        log.info("Exported Excel file: {}, Sheet: {}, Records: {}", filename, sheetName, data.size());
    }

    /**
     * 通用导入 (同步读取所有数据)
     * 注意: 对于非常大的文件，一次性读取所有数据可能导致内存溢出，建议使用 readExcelByListener
     *
     * @param file   上传的 Excel 文件
     * @param head   表头对应的实体类 (使用 @ExcelProperty 注解)
     * @param <T>    数据类型
     * @return 读取到的数据列表
     * @throws IOException IO异常
     */
    public static <T> List<T> importExcel(MultipartFile file, Class<T> head) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            List<T> list = EasyExcel.read(inputStream, head, null).sheet().doReadSync();
            log.info("Imported Excel file: {}, Records read: {}", file.getOriginalFilename(), list.size());
            return list;
        } catch (Exception e) {
            log.error("Error importing Excel file: {}", file.getOriginalFilename(), e);
            throw new IOException("读取 Excel 文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 通用导入 (使用监听器逐行处理)
     * 适用于大数据量导入，避免 OOM
     *
     * @param file     上传的 Excel 文件
     * @param head     表头对应的实体类 (使用 @ExcelProperty 注解)
     * @param consumer 对每批读取到的数据进行处理的逻辑 (例如: 保存到数据库)
     *                 可以使用 Lambda 表达式: dataList -> { your logic here... }
     * @param <T>      数据类型
     * @throws IOException IO异常
     */
    public static <T> void importExcelByListener(MultipartFile file, Class<T> head, Consumer<List<T>> consumer) throws IOException {
        importExcelByListener(file, head, consumer, 100); // 默认每批处理 100 条
    }

    /**
     * 通用导入 (使用监听器逐行处理，可指定批处理大小)
     *
     * @param file        上传的 Excel 文件
     * @param head        表头对应的实体类 (使用 @ExcelProperty 注解)
     * @param consumer    对每批读取到的数据进行处理的逻辑
     * @param batchSize   每批处理的数据条数
     * @param <T>         数据类型
     * @throws IOException IO异常
     */
    public static <T> void importExcelByListener(MultipartFile file, Class<T> head, Consumer<List<T>> consumer, int batchSize) throws IOException {
        try (InputStream inputStream = file.getInputStream()) {
            EasyExcel.read(inputStream, head, new PageReadListener<>(consumer, batchSize))
                    .sheet()
                    .doRead();
            log.info("Importing Excel file using listener: {}", file.getOriginalFilename());
        } catch (Exception e) {
            log.error("Error importing Excel file with listener: {}", file.getOriginalFilename(), e);
            throw new IOException("读取 Excel 文件失败: " + e.getMessage(), e);
        }
    }

    /**
     * 设置 Excel 导出响应头
     *
     * @param response HttpServletResponse
     * @param filename 文件名
     * @throws IOException IO异常
     */
    private static void setExcelResponseHeaders(HttpServletResponse response, String filename) throws IOException {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        // 防止中文文件名乱码
        String encodedFilename = URLEncoder.encode(filename, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        response.setHeader("Content-disposition", "attachment;filename*=utf-8''" + encodedFilename);
        response.setHeader("Access-Control-Expose-Headers", "Content-Disposition"); // 允许前端获取 Content-Disposition
    }

    // TODO: 添加更复杂的功能
    // 1. 动态表头导出
    // 2. 模板填充导出
    // 3. 复杂表头导入/导出 (多行表头、合并单元格等)
    // 4. 数据校验 (结合 Validation 或自定义校验逻辑)
    // 5. 导入结果反馈 (例如，返回成功/失败条数及错误信息)
} 