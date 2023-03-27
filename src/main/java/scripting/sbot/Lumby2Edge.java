package scripting.sbot;

import compatibility.sbot.Script;

public class Lumby2Edge extends Script {

  public String[] getCommands() {
    return new String[] {"Startwalkin"};
  }

  public void start(String command, String parameter[]) {
    DisplayMessage("@whi@YoungWun's Lumb2Edge", 3);
    while (Running()) {
      Walk(128, 641);
      Walk(127, 625);
      Walk(109, 618);
      Walk(112, 600);
      Walk(102, 581);
      Walk(99, 574);
      Walk(116, 565);
      Walk(131, 552);
      Walk(144, 543);
      Walk(164, 529);
      Walk(185, 517);
      Walk(204, 512);
      Walk(215, 497);
      Walk(216, 487);
      Walk(220, 471);
      Walk(224, 459);
      Walk(217, 450);
    }
    DisplayMessage("@Whi@Welcome to edgeville! :)", 3);
  }
}
