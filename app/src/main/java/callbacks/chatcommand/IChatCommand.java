package callbacks.chatcommand;

import controller.Controller;

@FunctionalInterface
public interface IChatCommand {
  void execute(String[] args, Controller c);

  default String helpString() {
    return "";
  }

  /** Full lambda with args */
  static IChatCommand of(CommandLambda lambda, String help) {
    return new IChatCommand() {
      @Override
      public void execute(String[] args, Controller c) {
        lambda.run(args, c);
      }

      @Override
      public String helpString() {
        return help;
      }
    };
  }

  /** Convenience lambda: no args array needed */
  static IChatCommand ofNoArgs(NoArgCommandLambda lambda, String help) {
    return new IChatCommand() {
      @Override
      public void execute(String[] args, Controller c) {
        lambda.run(c);
      }

      @Override
      public String helpString() {
        return help;
      }
    };
  }

  @FunctionalInterface
  interface CommandLambda {
    void run(String[] args, Controller c);
  }

  @FunctionalInterface
  interface NoArgCommandLambda {
    void run(Controller c);
  }
}
