package {{PACKAGE}}.web;

import {{PACKAGE}}.model.{{NAME}}DTO;
import {{PACKAGE}}.service.{{NAME}}Service;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class {{NAME}}ControllerTest {

    @Test
    void delegatesGetToAthenaService() {
        {{NAME}}Service service = mock({{NAME}}Service.class);
        {{NAME}}DTO dto = new {{NAME}}DTO();
        dto.setId(1L);
        dto.setName("saved");
        when(service.get(1L)).thenReturn(dto);

        {{NAME}}DTO result = new {{NAME}}Controller(service).get(1L);

        assertThat(result.getName()).isEqualTo("saved");
    }
}
