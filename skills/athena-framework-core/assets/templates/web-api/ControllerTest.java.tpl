package {{PACKAGE}}.web;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class {{NAME}}ControllerTest {

    @Test
    void delegatesToBusinessPort() {
        {{NAME}}Controller.UseCase useCase = new {{NAME}}Controller.UseCase() {
            @Override
            public {{NAME}}Response create({{NAME}}Request request) {
                return new {{NAME}}Response(1L, request.name());
            }

            @Override
            public {{NAME}}Response get(Long id) {
                return new {{NAME}}Response(id, "saved");
            }
        };
        {{NAME}}Controller controller = new {{NAME}}Controller(useCase);

        {{NAME}}Response response = controller.create(new {{NAME}}Request("created"));

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("created");
    }
}
