package cn.torna.web.controller.doc.vo;

import cn.torna.common.bean.TreeAware;
import cn.torna.common.support.IdCodec;
import cn.torna.common.util.IdUtil;
import com.alibaba.fastjson.annotation.JSONField;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 表名：doc_comment
 * 备注：文档评论表
 *
 * @author tanghc
 */
@Data
public class DocCommentVO implements TreeAware<DocCommentVO, Long> {

    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long id;

    /** 
     * doc_info.id
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long docId;

    /** 
     * 评论人
     */
    @JSONField(serializeUsing = IdCodec.class, deserializeUsing = IdCodec.class)
    private Long userId;

    /** 
     * 评论人昵称
     */
    private String nickname;

    /** 
     * 评论内容
     */
    private String content;

    /** 
     * 回复id，即：parent_id
     */
    private Long replyId;

    private LocalDateTime gmtCreate;

    private LocalDateTime gmtModified;

    @JSONField(name = "childrenList")
    private List<DocCommentVO> children;

    @Override
    public Long getParentId() {
        return this.replyId;
    }

    @Override
    public void setChildren(List<DocCommentVO> children) {
        this.children = children;
    }

    public Map<String, Object> getCommentUser() {
        Map<String, Object> user = new HashMap<>(8);
        user.put("id", IdUtil.encode(this.userId));
        user.put("nickName", this.nickname);
        return user;
    }

    public LocalDateTime getCreateDate() {
        return this.gmtCreate;
    }

}