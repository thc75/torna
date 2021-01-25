package cn.torna.common.util;

import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 文件上传工具类
 *
 * @author tanghc
 */
public class UploadUtil {

    /**
     * 获取上传文件
     *
     * @param request
     * @return
     */
    public static Collection<MultipartFile> getUploadFiles(HttpServletRequest request) {
        MultiValueMap<String, MultipartFile> fileMap = null;
        //检查form中是否有enctype="multipart/form-data"
        String contentType = request.getContentType();
        if (contentType != null && contentType.toLowerCase().contains("multipart")) {
            //将request变成多部分request
            MultipartHttpServletRequest multiRequest = (MultipartHttpServletRequest) request;
            fileMap = multiRequest.getMultiFileMap();
        }
        return Optional.ofNullable(fileMap)
                .map(Map::entrySet)
                .map(entry -> entry.stream()
                        .flatMap(e -> e.getValue().stream())
                        .collect(Collectors.toList()))
                .orElse(Collections.emptyList());
    }
}
