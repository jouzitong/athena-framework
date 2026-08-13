package {{PACKAGE}}.web;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/{{NAME_LOWER}}")
public class {{NAME}}Controller {

    private final UseCase useCase;

    public {{NAME}}Controller(UseCase useCase) {
        this.useCase = useCase;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public {{NAME}}Response create(@Valid @RequestBody {{NAME}}Request request) {
        return useCase.create(request);
    }

    @GetMapping("/{id}")
    public {{NAME}}Response get(@PathVariable Long id) {
        return useCase.get(id);
    }

    public interface UseCase {
        {{NAME}}Response create({{NAME}}Request request);

        {{NAME}}Response get(Long id);
    }
}
