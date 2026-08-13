package {{PACKAGE}}.service;

import {{PACKAGE}}.model.{{NAME}}DTO;
import {{PACKAGE}}.persistence.{{NAME}}Entity;
import {{PACKAGE}}.persistence.{{NAME}}Repository;
import org.athena.framework.data.jdbc.convert.IConvert;
import org.athena.framework.data.jpa.repository.BaseRepository;
import org.athena.framework.data.jpa.service.BaseMapperService;
import org.springframework.stereotype.Service;

@Service
public class {{NAME}}Service extends BaseMapperService<{{NAME}}Entity, {{NAME}}DTO> {

    private final {{NAME}}Repository repository;
    private final {{NAME}}Convert convert;

    public {{NAME}}Service({{NAME}}Repository repository, {{NAME}}Convert convert) {
        this.repository = repository;
        this.convert = convert;
    }

    @Override
    protected Class<?> entityType() {
        return {{NAME}}Entity.class;
    }

    @Override
    protected IConvert<{{NAME}}Entity, {{NAME}}DTO> convert() {
        return convert;
    }

    @Override
    protected BaseRepository<{{NAME}}Entity> repository() {
        return repository;
    }
}
