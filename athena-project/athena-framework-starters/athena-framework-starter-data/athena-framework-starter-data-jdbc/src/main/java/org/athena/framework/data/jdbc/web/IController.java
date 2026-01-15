package org.athena.framework.data.jdbc.web;

import org.athena.framework.data.jdbc.entity.IEntity;
import org.athena.framework.data.jdbc.entity.dto.IDTO;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.jdbc.vo.PageResultVO;
import org.athena.framework.web.annotation.web.ApiDeleteMapping;
import org.athena.framework.web.annotation.web.ApiGetMapping;
import org.athena.framework.web.annotation.web.ApiPostMapping;
import org.athena.framework.web.annotation.web.ApiPutMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 定义应用程序中控制器的契约。
 * 该接口的实现负责处理业务逻辑并协调模型和视图组件。
 * 它充当管理数据流、用户输入和用户界面更新的中介。
 */
public interface IController<
        Entity extends IEntity,
        DTO extends IDTO,
        Query extends BaseRequest> {

    /**
     * 根据提供的DTO向系统中添加一个新的实体。
     *
     * @param dto 包含要添加实体详细信息的数据传输对象
     * @return 表示新添加实体的DTO，包括任何生成或更新的字段，如ID
     */
    @ApiPostMapping
    DTO add(@RequestBody DTO dto);

    /**
     * 根据提供的DTO和ID更新系统中的现有实体。
     *
     * @param id  要更新的实体的唯一标识符
     * @param dto 包含实体更新详细信息的数据传输对象
     * @return 表示已更新实体的DTO，包括任何生成或更新的字段，如版本
     */
    @ApiPutMapping("/{id}")
    DTO update(@PathVariable("id") Long id, @RequestBody DTO dto);

    /**
     * 根据提供的DTO和ID部分更新系统中的现有实体。
     *
     * @param id  要部分更新的实体的唯一标识符
     * @param dto 包含实体部分更新的数据传输对象
     * @return 表示部分更新后的实体的DTO，包括任何生成或更新的字段，如版本
     */
    @PatchMapping("/{id}")
    DTO edit(@PathVariable("id") Long id, @RequestBody DTO dto);

    /**
     * 根据提供的ID从系统中删除一个实体。（软删除）
     *
     * @param id 要删除的实体的唯一标识符
     * @return 如果删除成功则返回true，否则返回false
     */
    @ApiDeleteMapping("/{id}")
    Boolean delete(@PathVariable("id") Long id);

    /**
     * 根据 ID 物理删除一个实体. (默认不支持)
     *
     * @param id 要删除的实体的唯一标识符
     * @return 如果删除成功则返回true，否则返回false
     */
    @ApiDeleteMapping("/physical/{id}")
    Boolean physicalDelete(@PathVariable("id") Long id);

    /**
     * 根据提供的查询参数检索分页的DTO列表。
     *
     * @param query 包含分页和过滤条件的查询对象
     * @return 包含与查询匹配的DTO列表及其分页信息的PageResultVO
     */
    @ApiGetMapping
    PageResultVO<DTO> page(Query query);

    /**
     * 根据提供的ID检索特定实体的DTO。
     *
     * @param id 要检索的实体的唯一标识符
     * @return 表示检索到的实体的DTO
     */
    @ApiGetMapping("/{id}")
    DTO get(@PathVariable("id") Long id);

}