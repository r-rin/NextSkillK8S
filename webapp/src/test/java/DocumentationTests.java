import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;
import org.springframework.web.client.RestTemplate;
import ukma.springboot.nextskill.NextSkillApplication;

@SpringBootTest(classes = NextSkillApplication.class)
class DocumentationTests {
    static ApplicationModules modules = ApplicationModules.of(NextSkillApplication.class);

    @MockBean
    public RestTemplate restTemplate;

    @Test
    void shouldBeCompliant() {
        modules.verify();
    }

    @Test
    void writeDocumentationSnippets() {
        new Documenter(modules)
                .writeModuleCanvases()
                .writeModulesAsPlantUml()
                .writeDocumentation()
                .writeIndividualModulesAsPlantUml();
    }
}
