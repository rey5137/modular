package com.rey.modular;

import org.junit.jupiter.api.Test;
import org.springframework.modulith.core.ApplicationModules;
import org.springframework.modulith.docs.Documenter;

class ModularApplicationTest {

    ApplicationModules modules = ApplicationModules.of(ModularApplication.class);

    @Test
    void shouldBeCompliant() {
        modules.verify();
        modules.forEach(System.out::println);
    }

    @Test
    void writeDocumentationSnippets() {
        new Documenter(modules)
                .writeModuleCanvases()
                .writeModulesAsPlantUml()
                .writeIndividualModulesAsPlantUml();
    }

}