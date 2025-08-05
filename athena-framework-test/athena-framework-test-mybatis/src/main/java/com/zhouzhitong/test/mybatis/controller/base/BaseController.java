package com.zhouzhitong.test.mybatis.controller.base;

import org.athena.framework.data.mybatis.entity.dto.BaseDTO;
import org.athena.framework.data.mybatis.entity.BaseEntity;
import org.athena.framework.data.jdbc.req.BaseRequest;
import org.athena.framework.data.mybatis.service.IMapperService;
import org.athena.framework.data.jdbc.vo.PageResultVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.List;

/**
 * 基础的 controller 层
 *
 * @author zhouzhitong
 * @since 2023/1/7
 */
public class BaseController
        // 操作实体
        <Entity extends BaseEntity
                , DTO extends BaseDTO
                // 分页参数
                , Query extends BaseRequest
                // 操作service
                , Service extends IMapperService<Entity, DTO, Long>> {

    @Autowired
    private Service service;

    protected Service service() {
        return service;
    }

    @Value("${lib.mapper.size-limit:1000}")
    private Integer sizeLimit;

    /**
     * 查询所有(如果带了分页参数,则分页查询, 否则查询所有)
     * <p>
     * 但是为了安全起见, 查询所有的时候还是限制一千条
     *
     * @param page 分页参数
     * @return 分页结果
     */
    @GetMapping
    @ResponseStatus(HttpStatus.PARTIAL_CONTENT)
    public PageResultVO<DTO> page(Query page) {
        if (page.isPage()) {
            return service().page(page);
        } else {
            // 为了安全起见, 查询所有的时候还是限制一千条
            if (page.getSize() == null) {
                page.setSize(sizeLimit);
            }
            List<DTO> resList = service().queryAll(page);
            return PageResultVO.ok(resList, null);
        }
    }

    /**
     * 通过主键查询单条数据
     *
     * @param id 主键
     * @return 单条数据
     */
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DTO get(@PathVariable("id") Long id) {
        return this.service().get(id);
    }

    /**
     * 新增数据
     *
     * @param dto 实体
     * @return 新增结果
     */
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DTO add(@RequestBody DTO dto) {
        return this.service().add(dto);
    }

    /**
     * 编辑数据
     *
     * @param id  编号
     * @param dto 实体
     * @return 编辑结果
     */
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DTO update(@PathVariable("id") Long id, @RequestBody DTO dto) {
        return this.service().update(id, dto);
    }

    /**
     * 编辑部分字段
     *
     * @param id  编号
     * @param dto 实体
     * @return 编辑结果
     */
    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.ACCEPTED)
    public DTO edit(@PathVariable("id") Long id, @RequestBody DTO dto) {
        return this.service().edit(id, dto);
    }


    /**
     * 删除数据
     *
     * @param id 主键
     * @return 删除是否成功
     */
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Boolean delete(@PathVariable("id") Long id) {
        return this.service().remove(id);
    }

}
