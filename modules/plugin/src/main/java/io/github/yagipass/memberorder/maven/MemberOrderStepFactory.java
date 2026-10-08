package io.github.yagipass.memberorder.maven;

import com.diffplug.spotless.FormatterStep;
import com.diffplug.spotless.maven.FormatterStepConfig;
import com.diffplug.spotless.maven.FormatterStepFactory;
import io.github.yagipass.memberorder.MemberOrderStep;
import org.jspecify.annotations.Nullable;

public final class MemberOrderStepFactory implements FormatterStepFactory {

  private final MemberOrderStep.Builder builder = MemberOrderStep.builder();

  public void setOrder(String order) {
    builder.order(order);
  }

  @Override
  public FormatterStep newFormatterStep(@Nullable FormatterStepConfig config) {
    return builder.build();
  }
}
