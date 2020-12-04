package torna.service;

import torna.common.support.BaseService;
import torna.dao.entity.EnumItem;
import torna.dao.mapper.EnumItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author tanghc
 */
@Service
public class EnumItemService extends BaseService<EnumItem, EnumItemMapper> {

}