package io.github.yagipass.memberorder.maven;

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.maven.FormatterStepConfig;
import com.diffplug.spotless.maven.FormatterStepFactory;
import io.github.yagipass.memberorder.MemberOrderStep;
import org.jspecify.annotations.Nullable;

public final class MemberOrderStepFactory implements FormatterStepFactory {

  private final MemberOrderStep.Builder builder = MemberOrderStep.builder();

  public void setCategoryOrder(String categoryOrder) {
    builder.categoryOrder(categoryOrder);
  }

  public void setVisibilityOrder(String visibilityOrder) {
    builder.visibilityOrder(visibilityOrder);
  }

  public void setSortFields(String sortFields) {
    if (!sortFields.equalsIgnoreCase("true") && !sortFields.equalsIgnoreCase("false")) {
      throw new IllegalArgumentException(
          "sortFields \"" + sortFields + "\" is invalid: use true or false.");
    }
    builder.sortFields(Boolean.parseBoolean(sortFields));
  }

  @Override
  public FormatterStep newFormatterStep(@Nullable FormatterStepConfig config) {
    return builder.build();
  }
}
