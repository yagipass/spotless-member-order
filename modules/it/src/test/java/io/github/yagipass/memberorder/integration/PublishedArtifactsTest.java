package io.github.yagipass.memberorder.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import org.junit.jupiter.api.Test;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

class PublishedArtifactsTest {

  @Test
  void pomDeclaresJdtCoreAsTheOnlyRuntimeDependencyBecauseTheBuildToolAlreadyProvidesSpotless()
      throws Exception {
    Element project =
        DocumentBuilderFactory.newInstance()
            .newDocumentBuilder()
            .parse(Environment.publishedFile(".pom").toFile())
            .getDocumentElement();
    NodeList dependencies = project.getElementsByTagName("dependency");
    List<String> declared = new ArrayList<>();
    for (int i = 0; i < dependencies.getLength(); i++) {
      Element dependency = (Element) dependencies.item(i);
      declared.add(
          text(dependency, "groupId")
              + ":"
              + text(dependency, "artifactId")
              + ":"
              + text(dependency, "scope"));
    }

    assertEquals(List.of("org.eclipse.jdt:org.eclipse.jdt.core:runtime"), declared);
  }

  @Test
  void gradleModuleMetadataDeclaresJdtCoreButNotSpotlessBecauseGradleReadsItInsteadOfThePom()
      throws Exception {
    String module = Files.readString(Environment.publishedFile(".module"));

    assertTrue(module.contains("\"module\": \"org.eclipse.jdt.core\""), module);
    assertFalse(module.contains("com.diffplug.spotless"), module);
  }

  private static String text(Element parent, String name) {
    NodeList elements = parent.getElementsByTagName(name);
    return elements.getLength() == 0 ? "" : elements.item(0).getTextContent();
  }
}
