package cn.torna.service;

import cn.torna.common.bean.User;
import cn.torna.common.support.BaseService;
import cn.torna.common.util.CopyUtil;
import cn.torna.dao.entity.DocComment;
import cn.torna.dao.mapper.DocCommentMapper;
import cn.torna.service.dto.DocCommentDTO;
import org.springframework.stereotype.Service;


/**
 * @author tanghc
 */
@Service
public class DocCommentService extends BaseService<DocComment, DocCommentMapper> {


    public void addComment(DocCommentDTO docCommentDTO) {
        User user = docCommentDTO.getUser();
        DocComment docComment = CopyUtil.copyBean(docCommentDTO, DocComment::new);
        docComment.setUserId(user.getUserId());
        docComment.setNickname(user.getNickname());
        this.save(docComment);
    }

}