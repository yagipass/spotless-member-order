package io.github.yagipass.memberorder.maven;

public final class Java extends com.diffplug.spotless.maven.java.Java {

  public void addMemberOrder(MemberOrderStepFactory memberOrder) {
    addStepFactory(memberOrder);
  }
}
