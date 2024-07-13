package cn.torna.common.bean;

import lombok.Data;

import java.util.List;

/**
 * @author 六如
 */
@Data
public class TreeData {

    private Long id;

    private String label;

    private String parentId;

    private List<TreeData> children;

}
