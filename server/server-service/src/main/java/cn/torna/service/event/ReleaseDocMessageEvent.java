package cn.torna.service.event;

import cn.torna.common.enums.ModifyType;
import cn.torna.service.dto.DocInfoDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author qiuyu
 */
@Getter
public class ReleaseDocMessageEvent extends ApplicationEvent {

    private final DocInfoDTO docInfoDTO;

    private final ModifyType modifyType;


    public ReleaseDocMessageEvent(Object source, DocInfoDTO docInfoDTO, ModifyType modifyType) {
        super(source);
        this.docInfoDTO = docInfoDTO;
        this.modifyType = modifyType;
    }

}
