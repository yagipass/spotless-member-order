package io.github.yagipass.memberorder;

import com.diffplug.spotless.FormatterFunc;
import com.diffplug.spotless.FormatterStep;
import java.io.Serializable;
import java.util.Objects;

public final class MemberOrderStep {

  private static final String NAME = "memberOrder";

  private MemberOrderStep() {}

  public static FormatterStep create() {
    return builder().build();
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private String order = MemberOrder.DEFAULT_ORDER;

    private Builder() {}

    public Builder order(String order) {
      this.order = Objects.requireNonNull(order, "order");
      return this;
    }

    public FormatterStep build() {
      return FormatterStep.create(
          NAME, new State(LibraryVersion.VALUE, MemberOrder.parse(order)), State::toFormatter);
    }
  }

  private record State(String version, MemberOrder order) implements Serializable {

    FormatterFunc toFormatter() {
      return new MemberSorter(order)::sort;
    }
  }
}
