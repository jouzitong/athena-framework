package {{PACKAGE}}.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "{{NAME_LOWER}}-service", path = "/api/{{NAME_LOWER}}")
public interface {{NAME}}Client {

    @GetMapping("/{id}")
    {{NAME}}Response get(@PathVariable("id") Long id);

    record {{NAME}}Response(Long id, String name) {
    }
}
