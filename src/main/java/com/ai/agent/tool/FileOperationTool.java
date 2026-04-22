package com.ai.agent.tool;

import cn.hutool.core.io.FileUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

/**
 * @author alh
 * @description 操作文件工具定义
 * @date 2026/4/20 20:51
 */
public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR + "/file";

    @Tool(description = "Read content of a file")
    public String readFile(@ToolParam(description = "Name of the file to read") String fileName) {
        String path = FILE_DIR + "/" + fileName;
        try {
            return FileUtil.readUtf8String(path);
        } catch (Exception e) {
            return "File not found" + e.getMessage();
        }
    }

    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "Name of the file to write") String fileName,
                            @ToolParam(description = "Content to write to the file") String content) {
        String path = FILE_DIR + "/" + fileName;
        // 创建目录
        try {
            FileUtil.mkdir(FILE_DIR);
            FileUtil.writeUtf8String(content, path);
            return "File written successfully to " + path;
        } catch (Exception e) {
            return "Failed to write file" + e.getMessage();
        }
    }
}
