package cn.torna.service;

import cn.torna.common.bean.User;
import cn.torna.common.enums.AttachmentSourceTypeEnum;
import cn.torna.common.support.BaseService;
import cn.torna.dao.entity.Attachment;
import cn.torna.dao.mapper.AttachmentMapper;
import com.gitee.fastmybatis.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class AttachmentService extends BaseService<Attachment, AttachmentMapper> {

    /**
     * 获取文档附件
     * @param docId
     * @return
     */
    public List<Attachment> listDocAttachment(long docId) {
        return listAttachment(AttachmentSourceTypeEnum.DOC, docId);
    }

    /**
     * 获取文档附件
     * @param commentId
     * @return
     */
    public List<Attachment> listCommentAttachment(long commentId) {
        return listAttachment(AttachmentSourceTypeEnum.COMMENT, commentId);
    }

    /**
     * 上传文档附件
     * @param docId 文档id
     * @param files 文件
     * @param user 上传人
     */
    public void uploadDocAttachment(long docId, List<MultipartFile> files, User user) {

    }

    /**
     * 上传评论附件
     * @param commentId 评论id
     * @param files 文件
     * @param user 上传人
     */
    public void uploadCommentAttachment(long commentId, List<MultipartFile> files, User user) {

    }

    private List<Attachment> listAttachment(AttachmentSourceTypeEnum sourceTypeEnum, long refId) {
        Query query = new Query()
                .eq("source_type", sourceTypeEnum.getSourceType())
                .eq("ref_id", refId);
        return this.list(query);
    }

}