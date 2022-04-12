package cn.torna.web.controller.doc;

import cn.torna.common.annotation.HashId;
import cn.torna.common.bean.Result;
import cn.torna.common.bean.User;
import cn.torna.common.context.UserContext;
import cn.torna.common.exception.BizException;
import cn.torna.common.util.CopyUtil;
import cn.torna.common.util.TreeUtil;
import cn.torna.dao.entity.DocComment;
import cn.torna.service.DocCommentService;
import cn.torna.service.dto.DocCommentDTO;
import cn.torna.web.controller.doc.param.DocCommentAddParam;
import cn.torna.web.controller.doc.param.DocCommentUpdateParam;
import cn.torna.web.controller.doc.vo.DocCommentVO;
import com.gitee.fastmybatis.core.PageInfo;
import com.gitee.fastmybatis.core.query.Query;
import com.gitee.fastmybatis.core.query.Sort;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
import java.util.Objects;

/**
 * 文档评论接口
 * @author thc
 */
@RestController
@RequestMapping("doc/comment")
public class DocCommentController {

    @Autowired
    private DocCommentService docCommentService;

    /**
     * 提交评论
     * @param param
     * @return
     */
    @PostMapping("add")
    public Result<?> add(@RequestBody @Valid DocCommentAddParam param) {
        DocCommentDTO docCommentDTO = CopyUtil.copyBean(param, DocCommentDTO::new);
        User user = UserContext.getUser();
        docCommentDTO.setUser(user);
        docCommentService.addComment(docCommentDTO);
        return Result.ok();
    }

    /**
     * 修改评论
     * @param param
     * @return
     */
    @PostMapping("update")
    public Result<?> update(@RequestBody @Valid DocCommentUpdateParam param) {
        Long id = param.getId();
        DocComment docComment = docCommentService.getById(id);
        if (!Objects.equals(docComment.getUserId(), UserContext.getUser().getUserId())) {
            throw new BizException("无法评论");
        }
        docComment.setContent(param.getContent());
        docCommentService.update(docComment);
        return Result.ok();
    }


    /**
     * 查询文档评论
     * @param docId
     * @return
     */
    @GetMapping("list")
    public Result<PageInfo<DocCommentVO>> listComment(@HashId Long docId) {
        Query query = new Query()
                .eq("doc_id",docId)
                .orderby("gmt_create", Sort.DESC);
        List<DocComment> docComments = docCommentService.list(query);
        int size = docComments.size();
        List<DocCommentVO> docCommentVOS = CopyUtil.copyList(docComments, DocCommentVO::new);
        List<DocCommentVO> docCommentsTree = TreeUtil.convertTree(docCommentVOS, 0L);
        PageInfo<DocCommentVO> pageInfo = new PageInfo<>();
        pageInfo.setList(docCommentsTree);
        pageInfo.setTotal(size);
        return Result.ok(pageInfo);
    }

}
