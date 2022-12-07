package cn.torna.web.controller.system;

import cn.torna.common.bean.Result;
import cn.torna.common.context.UploadContext;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.ResponseUtil;
import cn.torna.service.UploadService;
import cn.torna.web.controller.system.vo.UploadVO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;

/**
 * 上传文件
 *
 * @author thc
 */
@RestController
@Slf4j
public class UploadController {

    @Autowired
    private UploadService uploadService;

    /**
     * 文件上传
     *
     * @param file
     * @return
     */
    @PostMapping("uploadFile")
    public Result<UploadVO> upload(MultipartFile file) {
        try {
            String path = uploadService.saveFile(file);
            UploadVO uploadVO = new UploadVO(path);
            return Result.ok(uploadVO);
        } catch (IOException e) {
            log.error("保存文件失败", e);
            throw new BizException(e.getMessage());
        }
    }

    @RequestMapping(UploadContext.MAPPING)
    public void get(HttpServletRequest request, HttpServletResponse response) {
        String requestURI = request.getRequestURI();
        File file = uploadService.getFile(requestURI);
        try {
            ResponseUtil.writeImage(response, FileUtils.readFileToByteArray(file));
        } catch (IOException e) {
            log.error("write file error, uri={}", requestURI, e);
            ResponseUtil.writeText(response, "read file error");
        }
    }

}
