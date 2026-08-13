package {{PACKAGE}}.web;

import {{PACKAGE}}.model.{{NAME}}DTO;
import {{PACKAGE}}.model.{{NAME}}Query;
import {{PACKAGE}}.service.{{NAME}}Service;
import org.athena.framework.data.jdbc.web.BaseController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/{{NAME_LOWER}}")
public class {{NAME}}Controller extends BaseController<{{NAME}}DTO, {{NAME}}Query, {{NAME}}Service> {

    private final {{NAME}}Service service;

    public {{NAME}}Controller({{NAME}}Service service) {
        this.service = service;
    }

    @Override
    protected {{NAME}}Service service() {
        return service;
    }
}
