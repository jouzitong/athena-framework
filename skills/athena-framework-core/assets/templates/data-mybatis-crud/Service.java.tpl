package {{PACKAGE}}.service;

import {{PACKAGE}}.model.{{NAME}}DTO;
import {{PACKAGE}}.persistence.{{NAME}}Entity;
import {{PACKAGE}}.persistence.{{NAME}}Mapper;
import org.athena.framework.data.jdbc.convert.IConvert;
import org.athena.framework.data.mybatis.service.BaseMapperService;
import org.springframework.stereotype.Service;

@Service
public class {{NAME}}Service extends BaseMapperService<{{NAME}}Entity, {{NAME}}Mapper, {{NAME}}DTO> {

    private final {{NAME}}Convert convert;

    public {{NAME}}Service({{NAME}}Convert convert) {
        this.convert = convert;
    }

    @Override
    protected IConvert<{{NAME}}Entity, {{NAME}}DTO> convert() {
        return convert;
    }
}
