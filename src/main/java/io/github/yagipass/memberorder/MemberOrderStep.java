package io.github.yagipass.memberorder;

import com.diffplug.spotless.FormatterFunc;
import com.diffplug.spotless.FormatterStep;
import java.io.Serializable;
import java.util.Objects;
import org.jspecify.annotations.Nullable;

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

    private String categoryOrder = MemberOrder.DEFAULT_CATEGORY_ORDER;
    private @Nullable String visibilityOrder;
    private boolean sortFields;

    private Builder() {}

    public Builder categoryOrder(String categoryOrder) {
      this.categoryOrder = Objects.requireNonNull(categoryOrder, "categoryOrder");
      return this;
    }

    public Builder visibilityOrder(String visibilityOrder) {
      this.visibilityOrder = Objects.requireNonNull(visibilityOrder, "visibilityOrder");
      return this;
    }

    public Builder sortFields(boolean sortFields) {
      this.sortFields = sortFields;
      return this;
    }

    public FormatterStep build() {
      MemberOrder order = MemberOrder.parse(categoryOrder, visibilityOrder, sortFields);
      return FormatterStep.create(NAME, new State(LibraryVersion.VALUE, order), State::toFormatter);
    }
  }

  private record State(String version, MemberOrder order) implements Serializable {

    FormatterFunc toFormatter() {
      return new MemberSorter(order)::sort;
    }
  }
}
