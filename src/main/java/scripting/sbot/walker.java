package scripting.sbot;

import compatibility.sbot.Script;

public class walker extends Script {
  public String[] getCommands() {
    return new String[] {
      "lumbytodray",
      "lumbytoedge",
      "lumbytovar",
      "lumbytofally",
      "draytofally",
      "draytobarb",
      "draytolumby",
      "draytoedge",
      "fallytoedge",
      "fallytodray",
      "fallytovar",
      "fallytobarb",
      "barbtodray",
      "barbtofally",
      "vartofally",
      "vartoedge",
      "vartolumby",
      "vartoalchard",
      "alchardtovar",
      "edgetovar",
      "edgetodray",
      "edgetofally"
    };
  }

  public void start(String command, String[] parameter) {
    DisplayMessage("@gre@Sbot: @ora@ L33et Walker By @red@Saron", 3);

    command = parameter[0]; // Fixing for IdleRSC

    {
      if (command.equalsIgnoreCase("lumbytodray")) {
        Walk(130, 641);
        Walk(142, 637);
        Walk(155, 637);
        Walk(171, 635);
        Walk(180, 635);
        Walk(193, 635);
        Walk(206, 636);
        Walk(218, 636);
      }

      if (command.equalsIgnoreCase("lumbytoedge")) {
        Walk(130, 641);
        Walk(142, 637);
        Walk(155, 637);
        Walk(171, 635);
        Walk(180, 635);
        Walk(193, 635);
        Walk(221, 634);
        Walk(231, 623);
        Walk(233, 608);
        Walk(236, 594);
        Walk(241, 578);
        Walk(239, 559);
        Walk(237, 540);
        Walk(238, 525);
        Walk(225, 511);
        Walk(218, 492);
        Walk(220, 478);
        Walk(224, 463);
      }

      if (command.equalsIgnoreCase("lumbytovar")) {
        Walk(114, 650);
        Walk(103, 655);
        Walk(101, 644);
        Walk(106, 630);
        Walk(113, 610);
        Walk(108, 594);
        Walk(94, 578);
        Walk(91, 574);
        Walk(92, 574);
        Walk(106, 572);
        Walk(121, 561);
        Walk(129, 542);
        Walk(131, 524);
        Walk(131, 510);
      }

      if (command.equalsIgnoreCase("lumbytofally")) {
        Walk(130, 641);
        Walk(142, 637);
        Walk(155, 637);
        Walk(171, 635);
        Walk(180, 635);
        Walk(193, 635);
        Walk(206, 636);
        Walk(218, 636);
        Walk(220, 633);
        Walk(234, 623);
        Walk(250, 610);
        Walk(266, 610);
        Walk(282, 600);
        Walk(290, 583);
        Walk(291, 566);
      }

      if (command.equalsIgnoreCase("draytofally")) {
        Walk(220, 633);
        Walk(234, 623);
        Walk(250, 610);
        Walk(266, 610);
        Walk(282, 600);
        Walk(290, 583);
        Walk(291, 566);
      }

      if (command.equalsIgnoreCase("draytobarb")) {
        Walk(228, 624);
        Walk(241, 606);
        Walk(240, 591);
        Walk(242, 575);
        Walk(241, 558);
        Walk(241, 541);
        Walk(231, 515);
      }

      if (command.equalsIgnoreCase("draytolumby")) {
        Walk(218, 636);
        Walk(206, 636);
        Walk(193, 635);
        Walk(180, 637);
        Walk(171, 635);
        Walk(155, 637);
        Walk(142, 637);
        Walk(130, 641);
      }

      if (command.equalsIgnoreCase("draytoedge")) {
        Walk(221, 634);
        Walk(231, 623);
        Walk(233, 608);
        Walk(236, 594);
        Walk(241, 578);
        Walk(239, 559);
        Walk(237, 540);
        Walk(238, 525);
        Walk(225, 511);
        Walk(218, 492);
        Walk(220, 478);
        Walk(224, 463);
      }

      if (command.equalsIgnoreCase("fallytoedge")) {
        Walk(314, 537);
        Walk(314, 521);
        Walk(300, 508);
        Walk(282, 504);
        Walk(265, 508);
        Walk(250, 515);
        Walk(243, 515);
        Walk(239, 499);
        Walk(231, 483);
        Walk(221, 474);
        Walk(221, 470);
        Walk(218, 451);
      }

      if (command.equalsIgnoreCase("fallytodray")) {
        Walk(291, 566);
        Walk(290, 583);
        Walk(282, 600);
        Walk(266, 610);
        Walk(250, 610);
        Walk(234, 623);
        Walk(220, 633);
      }

      if (command.equalsIgnoreCase("fallytovar")) {
        Walk(315, 539);
        Walk(315, 520);
        Walk(297, 506);
        Walk(280, 503);
        Walk(264, 513);
        Walk(245, 515);
        Walk(229, 512);
        Walk(212, 512);
        Walk(197, 514);
        Walk(181, 515);
        Walk(165, 508);
        Walk(147, 509);
      }
      if (command.equalsIgnoreCase("fallytobarb")) {
        Walk(314, 537);
        Walk(314, 521);
        Walk(300, 508);
        Walk(282, 504);
        Walk(265, 508);
        Walk(250, 515);
        Walk(243, 515);
      }

      if (command.equalsIgnoreCase("barbtodray")) {
        Walk(231, 515);
        Walk(241, 541);
        Walk(241, 558);
        Walk(241, 575);
        Walk(241, 591);
        Walk(241, 606);
        Walk(228, 624);
      }

      if (command.equalsIgnoreCase("barbtofally")) {

        Walk(205, 633);
        Walk(192, 634);
        Walk(180, 635);
        Walk(165, 635);
        Walk(153, 637);
        Walk(134, 638);
        Walk(125, 645);
      }

      if (command.equalsIgnoreCase("vartofally")) {
        Walk(147, 509);
        Walk(165, 508);
        Walk(181, 515);
        Walk(197, 514);
        Walk(212, 512);
        Walk(229, 512);
        Walk(245, 515);
        Walk(264, 513);
        Walk(280, 503);
        Walk(297, 506);
        Walk(315, 520);
        Walk(315, 539);
      }

      if (command.equalsIgnoreCase("vartoedge")) {
        Walk(152, 509);
        Walk(165, 507);
        Walk(183, 515);
        Walk(200, 515);
        Walk(216, 511);
        Walk(217, 502);
        Walk(218, 487);
        Walk(220, 477);
        Walk(224, 460);
      }

      if (command.equalsIgnoreCase("vartolumby")) {

        Walk(131, 510);
        Walk(131, 524);
        Walk(129, 542);
        Walk(121, 561);
        Walk(106, 572);
        Walk(92, 574);
        Walk(91, 574);
        Walk(94, 578);
        Walk(108, 594);
        Walk(113, 610);
        Walk(106, 630);
        Walk(101, 644);
        Walk(103, 655);
        Walk(114, 650);
      }

      if (command.equalsIgnoreCase("vartoalchard")) {
        Walk(117, 509);
        Walk(100, 508);
        Walk(79, 509);
        Walk(71, 536);
        Walk(66, 562);
        Walk(75, 575);
        Walk(64, 596);
        Walk(61, 609);
        Walk(64, 626);
        Walk(69, 639);
        Walk(75, 650);
        Walk(74, 664);
        Walk(80, 681);
        Walk(88, 695);
      }

      if (command.equalsIgnoreCase("alchardtovar")) {
        Walk(80, 680);
        Walk(83, 659);
        Walk(83, 635);
        Walk(82, 621);
        Walk(81, 605);
        Walk(79, 590);
        Walk(75, 578);
        Walk(72, 568);
        Walk(73, 559);
        Walk(72, 540);
        Walk(74, 525);
        Walk(83, 509);
      }

      if (command.equalsIgnoreCase("edgetovar")) {
        Walk(224, 460);
        Walk(220, 477);
        Walk(218, 487);
        Walk(217, 502);
        Walk(216, 511);
        Walk(200, 515);
        Walk(183, 515);
        Walk(165, 507);
        Walk(152, 509);
      }

      if (command.equalsIgnoreCase("edgetodray")) {
        Walk(224, 463);
        Walk(220, 478);
        Walk(218, 492);
        Walk(225, 511);
        Walk(238, 525);
        Walk(237, 540);
        Walk(239, 559);
        Walk(241, 578);
        Walk(236, 594);
        Walk(233, 608);
        Walk(231, 623);
        Walk(221, 634);
      }

      if (command.equalsIgnoreCase("edgetofally")) {
        Walk(218, 451);
        Walk(221, 470);
        Walk(221, 474);
        Walk(231, 483);
        Walk(239, 499);
        Walk(243, 515);
        Walk(250, 515);
        Walk(265, 508);
        Walk(282, 504);
        Walk(300, 508);
        Walk(314, 521);
        Walk(314, 537);
      }
    }
    DisplayMessage("@red@You have arrived", 3);
  }
}
