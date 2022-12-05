package cn.torna.web.controller.system;

import cn.torna.common.bean.Result;
import cn.torna.web.controller.system.vo.UploadVO;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 上传文件
 * @author thc
 */
@RestController
@RequestMapping("upload")
public class UploadController {

    @PostMapping("image")
    public Result<UploadVO> upload(MultipartFile file) {
        UploadVO uploadVO = new UploadVO("https://torna.cn/assets/images/logo_new.png");
        return Result.ok(uploadVO);
    }

}
