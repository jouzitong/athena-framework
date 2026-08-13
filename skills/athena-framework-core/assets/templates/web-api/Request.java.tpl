package {{PACKAGE}}.web;

import jakarta.validation.constraints.NotBlank;

public record {{NAME}}Request(
        @NotBlank String name
) {
}
