package cn.torna.dao.entity;

import java.util.Date;

import com.gitee.fastmybatis.annotation.Pk;
import com.gitee.fastmybatis.annotation.PkStrategy;
import com.gitee.fastmybatis.annotation.Table;
import lombok.Data;


/**
 * 表名：doc_comment
 * 备注：文档评论表
 *
 * @author tanghc
 */
@Table(name = "doc_comment", pk = @Pk(name = "id", strategy = PkStrategy.INCREMENT))
@Data
public class DocComment {

    private Long id;

    /** 
     * doc_info.id
     */
    private Long docId;

    /** 
     * 评论人
     */
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

    @com.gitee.fastmybatis.annotation.Column(logicDelete = true)
    private Byte isDeleted;

    private Date gmtCreate;

    private Date gmtModified;


}