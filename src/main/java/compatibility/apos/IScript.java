package compatibility.apos;

public interface IScript {

  public void init(String params);

  public int main();

  public void paint();

  public void onServerMessage(String str);

  public void onTradeRequest(String name);

  public void onChatMessage(String msg, String name, boolean mod, boolean admin);

  public void onPrivateMessage(String msg, String name, boolean mod, boolean admin);

  public void onKeyPress(int keycode);

  public boolean isSleeping();

  public int getFatigue();

  public boolean isTricking();

  public void logout();
}
