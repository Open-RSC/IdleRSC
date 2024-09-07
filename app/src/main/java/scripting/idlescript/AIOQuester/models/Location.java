package scripting.idlescript.AIOQuester.models;

import bot.Main;
import controller.Controller;
import controller.WebWalker.WebWalker;
import controller.WebWalker.WebwalkGraph;
import java.util.*;
import java.util.stream.Collectors;

/* TODO: Places left to add:
 * Al-Kharid
 * Desert
 * Catherby
 * Entrana
 * The entirety of Karamja
 * Seer's Village
 * Camelot
 * Ardougne
 * Tree Gnome Stronghold
 * Tree Gnome Village
 * Port Khazard
 * Gu'Tanoth
 * Feldip Hills
 * Lots of one-off locations */

// TODO: Eventually move this to models/entities

/**
 * Enum of locations useful for checking if the player is in a Location's boundary with
 * QuestHandler.isAtLocation() or Location.isAtLocation(), or walking to a location's standable tile
 * with QuestHandler.walkTowards() or Location.walkTowards().
 */
public enum Location {
  AL_KHARID_BORDER_GATE(
      new Boundary(88, 647, 91, 652), new Tile(80, 649), "Al-Kharid - Gate to Lumbridge"),
  ARDOUGNE_MONASTERY(new Boundary(575, 651, 603, 669), new Tile(589, 653), "Ardougne - Monastery"),
  ARDOUGNE_SOUTH_BANK(
      new Boundary(534, 605, 557, 619), new Tile(552, 613), "Ardougne - South Bank"),
  BARBARIAN_OUTPOST_ENTRANCE(
      new Boundary(493, 538, 506, 555), new Tile(495, 544), "Barbarian Outpost Gate"),
  BARBARIAN_OUTPOST_INNER(
      new Boundary(485, 541, 493, 555), new Tile(491, 546), "Barbarian Outpost Inner"),
  BARBARIAN_VILLAGE_COAL_MINE(
      new Boundary(224, 503, 229, 508), new Tile(226, 505), "Barbarian Village - Coal Mine"),
  BARBARIAN_VILLAGE_HELMET_SHOP(
      new Boundary(234, 507, 237, 510),
      new Tile(236, 509),
      "Barbarian Village - Peksa's Helmet Shop"),
  BARBARIAN_VILLAGE_MESS_HALL(
      new Boundary(230, 495, 235, 502), new Tile(233, 501), "Barbarian Village - Mess Hall"),
  BARBARIAN_VILLAGE_POTTERY_HOUSE(
      new Boundary(227, 521, 230, 524), new Tile(228, 522), "Barbarian Village - Pottery House"),
  BARBARIAN_VILLAGE_SPINNING_WHEEL(
      new Boundary(229, 508, 231, 510), new Tile(230, 509), "Barbarian Village - Spinning Wheel"),
  BARBARIAN_VILLAGE_TIN_MINE(
      new Boundary(227, 515, 230, 519), new Tile(228, 517), "Barbarian Village - Tin Mine"),
  BRIMHAVEN_SHRIMP_AND_PARROT(
      new Boundary(443, 679, 459, 692), new Tile(443, 679), "Brimhaven - Shrimp and Parrot Pub"),
  DRAYNOR_AGGIES_HOUSE(
      new Boundary(222, 624, 225, 627), new Tile(223, 625), "Draynor - Aggie's House"),
  DRAYNOR_BANK(new Boundary(216, 634, 223, 638), new Tile(220, 635), "Draynor - Bank"),
  DRAYNOR_JAIL(new Boundary(194, 637, 203, 642), new Tile(195, 639), "Draynor - Jail"),
  DRAYNOR_MANOR_ENTRANCE(
      new Boundary(209, 556, 212, 558), new Tile(211, 557), "Draynor - Draynor Manor Entrance"),
  DRAYNOR_MARKET(new Boundary(223, 628, 232, 634), new Tile(227, 631), "Draynor - Market"),
  DRAYNOR_MORGANS_HOUSE(
      new Boundary(214, 618, 217, 621), new Tile(216, 619), "Draynor - Morgan's House"),
  DRAYNOR_MORGANS_HOUSE_UPSTAIRS(
      new Boundary(214, 1562, 217, 1565), new Tile(216, 1563), "Draynor - Morgan's House Upstairs"),
  DRAYNOR_NEDS_HOUSE(new Boundary(214, 624, 217, 627), new Tile(215, 625), "Draynor - Ned's House"),
  DRAYNOR_POTATO_FIELD(
      new Boundary(181, 608, 189, 620), new Tile(185, 608), "Draynor - Potato Field"),
  DRAYNOR_TOMATO_FIELD(
      new Boundary(181, 597, 189, 603), new Tile(185, 603), "Draynor - Tomato Field"),
  DWARF_TUNNEL_EAST(
      new Boundary(383, 462, 388, 465), new Tile(385, 464), "Taverley - Dwarf Tunnel East"),
  DWARF_TUNNEL_WEST(
      new Boundary(420, 450, 432, 464),
      new Tile(427, 457),
      "Catherby - Dwarf Tunnel Western Entrance"),
  EDGEVILLE_BANK(new Boundary(212, 448, 220, 453), new Tile(216, 451), "Edgeville - Bank"),
  EDGEVILLE_BLACK_KNIGHTS_FORTRESS_ENTRANCE(
      new Boundary(268, 438, 270, 443), new Tile(270, 441), "Edgeville - Black Knight's Fortress"),
  EDGEVILLE_DUNGEON_ENTRANCE(
      new Boundary(214, 465, 218, 469),
      new Tile(216, 468),
      "Edgeville - Edgeville Dungeon Entrance"),
  EDGEVILLE_GENERAL_STORE(
      new Boundary(222, 439, 227, 443), new Tile(225, 442), "Edgeville - General Store"),
  EDGEVILLE_ICE_MOUNTAIN_ORACLE(
      new Boundary(285, 454, 289, 459), new Tile(287, 457), "Edgeville - Ice Mountain Oracle"),
  EDGEVILLE_JAIL(new Boundary(202, 433, 208, 437), new Tile(203, 435), "Edgeville - Jail"),
  EDGEVILLE_MAN_ROOM(new Boundary(210, 440, 217, 445), new Tile(213, 443), "Edgeville - Man Room"),
  EDGEVILLE_MONASTERY(
      new Boundary(249, 456, 265, 472), new Tile(255, 464), "Edgeville - Monastery"),
  EDGEVILLE_OZIACHS_HOUSE(
      new Boundary(242, 442, 246, 445), new Tile(243, 443), "Edgeville - Oziach's House"),
  FALADOR_APPLE_ORCHARD(
      new Boundary(305, 508, 325, 519), new Tile(320, 514), "Falador - Apple Orchard"),
  FALADOR_CABBAGE_FIELD(
      new Boundary(248, 597, 262, 606), new Tile(255, 602), "Falador - Cabbage Field"),
  FALADOR_CASTLE_COURTYARD(
      new Boundary(308, 561, 315, 566), new Tile(313, 564), "Falador - Castle Courtyard"),
  FALADOR_CASTLE_SIR_VYVINS_ROOM(
      new Boundary(316, 2454, 320, 2459), new Tile(319, 2455), "Falador - Sir Vyvin's Room"),
  FALADOR_CHAINBODY_SHOP(
      new Boundary(300, 577, 306, 580), new Tile(303, 579), "Falador - Wayne's Chains"),
  FALADOR_CHICKEN_PEN(
      new Boundary(269, 600, 273, 607), new Tile(272, 604), "Falador - Chicken Pen"),
  FALADOR_DORICS_ANVILS(
      new Boundary(323, 487, 327, 492), new Tile(326, 490), "Falador - Doric's Anvils"),
  FALADOR_DWARVEN_MINE_NORTH_ENTRANCE(
      new Boundary(278, 491, 282, 495), new Tile(279, 493), "Falador - Dwarven Mine North"),
  FALADOR_DWARVEN_MINE_SOUTH_ENTRANCE(
      new Boundary(249, 535, 253, 540), new Tile(250, 538), "Falador - Dwarven Mine South"),
  FALADOR_EAST_BANK(new Boundary(280, 564, 286, 573), new Tile(283, 570), "Falador - East Bank"),
  FALADOR_FOUNTAIN(new Boundary(312, 538, 315, 541), new Tile(315, 540), "Falador - Fountain"),
  FALADOR_FURNACES(new Boundary(306, 543, 311, 547), new Tile(311, 545), "Falador - Furnaces"),
  FALADOR_GEM_STORE(
      new Boundary(331, 565, 334, 569), new Tile(332, 565), "Falador - Herquin's Gem Store"),
  FALADOR_GENERAL_STORE(
      new Boundary(317, 530, 322, 536), new Tile(318, 533), "Falador - General Store"),
  FALADOR_MAKEOVER_MAGE(
      new Boundary(368, 578, 371, 580), new Tile(369, 579), "Falador - Makeover Mage"),
  FALADOR_MINE(new Boundary(355, 540, 369, 559), new Tile(361, 552), "Falador - West Mine"),
  FALADOR_MINING_GUILD_ENTRANCE(
      new Boundary(272, 563, 277, 567), new Tile(274, 565), "Falador - Mining Guild Entrance"),
  FALADOR_PARK(new Boundary(288, 543, 293, 548), new Tile(292, 545), "Falador - Park"),
  FALADOR_RISING_SUN_INN(
      new Boundary(316, 544, 323, 549), new Tile(320, 546), "Falador - Rising Sun Inn"),
  FALADOR_SHIELD_SHOP(
      new Boundary(317, 530, 322, 536), new Tile(318, 533), "Falador - Cassie's Shield Shop"),
  FALADOR_SPINNING_WHEEL(
      new Boundary(295, 577, 299, 580), new Tile(296, 579), "Falador - Spinning Wheel"),
  FALADOR_WEST_BANK(new Boundary(328, 549, 334, 557), new Tile(328, 552), "Falador - West Bank"),
  GOBLIN_VILLAGE(new Boundary(323, 487, 327, 492), new Tile(326, 490), "Goblin Village - Center"),
  GOBLIN_VILLAGE_ZAMORAK_ALTAR(
      new Boundary(328, 433, 334, 438), new Tile(331, 435), "Goblin Village - Altar of Zamorak"),
  LUMBRIDGE_BOBS_AXES(
      new Boundary(122, 666, 124, 670), new Tile(122, 668), "Lumbridge - Bob's Axe Shop"),
  LUMBRIDGE_BORDER_GATE(
      new Boundary(92, 647, 95, 652), new Tile(93, 649), "Lumbridge - Gate to Al-Kharid"),
  LUMBRIDGE_CABBAGE_FIELD(
      new Boundary(137, 597, 153, 612), new Tile(145, 606), "Lumbridge - Cabbage Field"),
  LUMBRIDGE_CASTLE_COURTYARD(
      new Boundary(120, 652, 128, 665), new Tile(126, 658), "Lumbridge - Castle Courtyard"),
  LUMBRIDGE_CASTLE_DUKES_ROOM(
      new Boundary(129, 1601, 133, 1606), new Tile(132, 1603), "Lumbridge - Duke's Room"),
  LUMBRIDGE_CASTLE_KITCHEN(
      new Boundary(131, 659, 137, 662), new Tile(135, 660), "Lumbridge - Castle Kitchen"),
  LUMBRIDGE_CASTLE_SPINNING_WHEEL(
      new Boundary(136, 2552, 141, 2557), new Tile(138, 2554), "Lumbridge - Spinning Wheel"),
  LUMBRIDGE_CHICKEN_PEN(
      new Boundary(115, 603, 122, 612), new Tile(117, 609), "Lumbridge - Chicken Pen"),
  LUMBRIDGE_CHURCH(new Boundary(109, 660, 113, 669), new Tile(113, 664), "Lumbridge - Church"),
  LUMBRIDGE_CORN_FIELD(
      new Boundary(157, 600, 163, 609), new Tile(160, 607), "Lumbridge - Corn Field"),
  LUMBRIDGE_COW_PEN(new Boundary(96, 605, 104, 628), new Tile(103, 619), "Lumbridge - Cow Pen"),
  LUMBRIDGE_FARMER_FREDS_HOUSE(
      new Boundary(157, 617, 161, 620), new Tile(159, 618), "Lumbridge - Farmer Fred's House"),
  LUMBRIDGE_FURNACE(new Boundary(130, 626, 133, 630), new Tile(130, 628), "Lumbridge - Furnace"),
  LUMBRIDGE_GENERAL_STORE(
      new Boundary(132, 642, 134, 643), new Tile(133, 642), "Lumbridge - General Store"),
  LUMBRIDGE_GOBLIN_HUT(
      new Boundary(115, 628, 119, 632), new Tile(117, 630), "Lumbridge - Goblin Hut"),
  LUMBRIDGE_GRAVEYARD(
      new Boundary(106, 666, 110, 676), new Tile(107, 675), "Lumbridge - Graveyard"),
  LUMBRIDGE_MILL_F1(
      new Boundary(163, 597, 169, 603), new Tile(166, 601), "Lumbridge - Mill Ground Floor"),
  LUMBRIDGE_MILL_F2(
      new Boundary(163, 1541, 169, 1547), new Tile(164, 1544), "Lumbridge - Mill Second Floor"),
  LUMBRIDGE_MILL_F3(
      new Boundary(163, 2485, 169, 2491), new Tile(167, 2489), "Lumbridge - Mill Third Floor"),
  LUMBRIDGE_MUMS_HOUSE(
      new Boundary(119, 664, 119, 667), new Tile(119, 666), "Lumbridge - Mum's House"),
  LUMBRIDGE_ONION_FIELD(
      new Boundary(157, 621, 160, 623), new Tile(160, 621), "Lumbridge - Onion Field"),
  LUMBRIDGE_POTATO_FIELD(
      new Boundary(95, 588, 103, 603), new Tile(102, 597), "Lumbridge - Potato Field"),
  LUMBRIDGE_SHEEP_PEN(
      new Boundary(136, 618, 156, 633), new Tile(144, 630), "Lumbridge - Sheep Pen"),
  LUMBRIDGE_SWAMP_KRESHS_HUT(
      new Boundary(156, 694, 159, 697), new Tile(157, 695), "Lumbridge - Kresh's Hut"),
  LUMBRIDGE_SWAMP_LEPRECHAUN_TREE(
      new Boundary(171, 660, 173, 663),
      new Tile(173, 662),
      "Lumbridge - Lost City Leprechaun Tree"),
  LUMBRIDGE_SWAMP_URHNEYS_HOUSE(
      new Boundary(115, 709, 118, 712), new Tile(116, 710), "Lumbridge - Father Urhney's House"),
  LUMBRIDGE_SWAMP_ZANARIS_SHED(
      new Boundary(126, 685, 127, 687), new Tile(126, 686), "Lumbridge - Zanaris Shed"),
  LUMBRIDGE_WHEAT_FIELD(
      new Boundary(170, 596, 178, 604), new Tile(172, 604), "Lumbridge - Wheat Field"),
  PORT_SARIM_AXE_SHOP(
      new Boundary(262, 626, 267, 632), new Tile(264, 630), "Port Sarim - Brian's Axe Shop"),
  PORT_SARIM_CHURCH(new Boundary(299, 688, 305, 691), new Tile(304, 689), "Port Sarim - Church"),
  PORT_SARIM_DOCKS(new Boundary(258, 636, 270, 660), new Tile(269, 650), "Port Sarim - Docks"),
  PORT_SARIM_FISHING_SHOP(
      new Boundary(276, 645, 280, 650), new Tile(278, 649), "Port Sarim - Gerrant's Fishing Shop"),
  PORT_SARIM_GENERAL_STORE(
      new Boundary(273, 655, 279, 659), new Tile(274, 657), "Port Sarim - General Store"),
  PORT_SARIM_ICE_DUNGEON_ENTRANCE(
      new Boundary(283, 709, 287, 713), new Tile(286, 711), "Port Sarim - Ice Dungeon Entrance"),
  PORT_SARIM_JAIL(new Boundary(282, 656, 286, 667), new Tile(285, 663), "Port Sarim - Jail"),
  PORT_SARIM_JEWELLERY_SHOP(
      new Boundary(276, 630, 280, 634), new Tile(276, 632), "Port Sarim - Grum's Jewellery Shop"),
  PORT_SARIM_JEWELLERY_SHOP_ENTRANCE(
      new Boundary(275, 631, 276, 633),
      new Tile(276, 632),
      "Port Sarim - Grum's Jewellery Shop Entrance"),
  PORT_SARIM_RUNE_SHOP(
      new Boundary(271, 630, 274, 634), new Tile(272, 632), "Port Sarim - Betty's Rune Shop"),
  PORT_SARIM_RUSTY_ANCHOR(
      new Boundary(249, 624, 257, 629), new Tile(253, 625), "Port Sarim - The Rusty Anchor"),
  PORT_SARIM_THURGOS_HUT(
      new Boundary(292, 711, 294, 714), new Tile(293, 712), "Port Sarim - Thurgo's Hut"),
  RIMMINGTON_CHEMIST(new Boundary(338, 656, 350, 668), new Tile(344, 663), "Rimmington - Chemist"),
  RIMMINGTON_CRAFTING_GUILD_ENTRANCE(
      new Boundary(345, 597, 349, 600), new Tile(347, 600), "Rimmington - Crafting Guild Entrance"),
  RIMMINGTON_CRAFTING_SHOP(
      new Boundary(328, 666, 332, 670), new Tile(330, 667), "Rimmington - Rommik's Crafting Shop"),
  RIMMINGTON_CROP_PATCH(
      new Boundary(326, 626, 333, 634), new Tile(329, 630), "Rimmington - Crop Patch"),
  RIMMINGTON_ESTERS_HOUSE(
      new Boundary(314, 659, 319, 664), new Tile(317, 662), "Rimmington - Ester's House"),
  RIMMINGTON_GENERAL_STORE(
      new Boundary(329, 658, 332, 664), new Tile(330, 661), "Rimmington - General Store"),
  RIMMINGTON_HETTYS_HOUSE(
      new Boundary(315, 665, 318, 669), new Tile(316, 667), "Rimmington - Hetty's House"),
  RIMMINGTON_HOBGOBLIN_PENINSULA(
      new Boundary(356, 601, 367, 620), new Tile(364, 605), "Rimmington - Hobgoblin Peninsula"),
  RIMMINGTON_MELZARS_MAZE_ENTRANCE(
      new Boundary(335, 631, 337, 633), new Tile(336, 632), "Rimmington - Melzar's Maze Entrance"),
  SORCERERS_TOWER(
      new Boundary(507, 505, 514, 511), new Tile(511, 508), "Sorcerers' Tower - Ground Floor"),
  SORCERERS_TOWER_ABOVE(
      new Boundary(507, 1448, 514, 1458), new Tile(511, 1452), "Sorcerers' Tower - 1st Floor "),
  TAVERLEY(new Boundary(346, 477, 387, 511), new Tile(364, 488), "Taverley - Center"),
  TAVERLEY_CRYSTAL_CHEST_HOUSE(
      new Boundary(366, 493, 370, 498), new Tile(367, 496), "Taverley - Crystal Chest"),
  TAVERLEY_DUNGEON_ENTRANCE(
      new Boundary(371, 514, 384, 525), new Tile(377, 520), "Taverley - Taverly Dungeon Entrance"),
  TAVERLEY_GAUIS_HOUSE(
      new Boundary(377, 500, 381, 503), new Tile(378, 501), "Taverley - Gauis' House"),
  TAVERLEY_GUTHIX_ALTAR(
      new Boundary(361, 461, 364, 464), new Tile(364, 463), "Taverley - Altar of Guthix"),
  TAVERLEY_HEROES_GUILD_ENTRANCE(
      new Boundary(370, 441, 374, 444), new Tile(372, 442), "Taverley - Heroes Guild Entrance"),
  TAVERLEY_JATIX_SHOP(
      new Boundary(366, 504, 370, 508), new Tile(368, 506), "Taverley - Jatix's Herblore Shop"),
  TAVERLEY_LADY_OF_THE_LAKE(
      new Boundary(350, 520, 356, 526), new Tile(353, 523), "Taverley - Lady of the Lake"),
  TAVERLEY_SANFEWS_HOUSE(
      new Boundary(377, 486, 381, 489), new Tile(379, 487), "Taverley - Sanfew's House"),
  VARROCK_ANVILS(new Boundary(145, 510, 148, 516), new Tile(148, 512), "Varrock - Anvils"),
  VARROCK_APOTHECARY(new Boundary(141, 518, 145, 521), new Tile(143, 520), "Varrock - Apothecary"),
  VARROCK_ARCHERY_SHOP(
      new Boundary(113, 511, 116, 516), new Tile(114, 513), "Varrock - Lowe's Archery Shop"),
  VARROCK_ARMOR_SHOP(
      new Boundary(113, 498, 119, 505), new Tile(117, 501), "Varrock - Horvik's Armor Shop"),
  VARROCK_BERRY_BUSHES_EAST(
      new Boundary(75, 530, 78, 536), new Tile(76, 533), "Varrock - Berry Bushes East"),
  VARROCK_BERRY_BUSHES_SOUTH(
      new Boundary(81, 536, 87, 538), new Tile(84, 537), "Varrock - Berry Bushes South"),
  VARROCK_BERRY_BUSHES_WEST(
      new Boundary(96, 536, 103, 538), new Tile(99, 537), "Varrock - Berry Bushes West"),
  VARROCK_BLACK_ARM_GANG_ENTRANCE(
      new Boundary(145, 533, 151, 535), new Tile(148, 534), "Varrock - Black Arm Gang Entrance"),
  VARROCK_BLUE_MOON_INN(
      new Boundary(120, 520, 126, 526), new Tile(122, 523), "Varrock - Blue Moon Inn"),
  VARROCK_BLUE_MOON_INN_KITCHEN(
      new Boundary(116, 520, 119, 526), new Tile(117, 525), "Varrock - Blue Moon Inn Kitchen"),
  VARROCK_BRASS_KEY_HUT(
      new Boundary(201, 481, 204, 484), new Tile(202, 482), "Varrock - Brass Key Hut"),
  VARROCK_CASTLE_ALTAR(
      new Boundary(130, 1399, 137, 1403), new Tile(136, 1401), "Varrock - Castle Altar"),
  VARROCK_CASTLE_COURTYARD(
      new Boundary(123, 478, 139, 486), new Tile(131, 483), "Varrock - Castle Courtyard"),
  VARROCK_CASTLE_GARDEN(
      new Boundary(123, 487, 139, 499), new Tile(130, 493), "Varrock - Castle Garden"),
  VARROCK_CASTLE_GUARD_BARRACKS(
      new Boundary(138, 1395, 142, 1408), new Tile(140, 1398), "Varrock - Castle Guard Barracks"),
  VARROCK_CASTLE_KITCHEN(
      new Boundary(118, 461, 123, 468), new Tile(119, 465), "Varrock - Castle Kitchen"),
  VARROCK_CASTLE_LIBRARY(
      new Boundary(126, 455, 134, 459), new Tile(128, 457), "Varrock - Castle Library"),
  VARROCK_CASTLE_SIR_PRYSINS_ROOM(
      new Boundary(135, 472, 140, 477), new Tile(137, 474), "Varrock - Sir Prysin's Room"),
  VARROCK_CASTLE_THRONE_ROOM(
      new Boundary(124, 472, 128, 477), new Tile(126, 474), "Varrock - Castle Throne Room"),
  VARROCK_CHAMPIONS_GUILD(
      new Boundary(148, 554, 152, 562), new Tile(150, 556), "Varrock - Champion's Guild "),
  VARROCK_CHURCH(
      new Boundary(98, 473, 106, 482), new Tile(100, 475), "Varrock - Church Ground Floor"),
  VARROCK_CHURCH_SECOND_FLOOR(
      new Boundary(103, 1417, 105, 1420), new Tile(104, 1418), "Varrock - Church Second Floor"),
  VARROCK_CLOTHES_SHOP(
      new Boundary(136, 515, 139, 516), new Tile(137, 516), "Varrock - Thessalia's Clothes Shop"),
  VARROCK_COOKS_GUILD(
      new Boundary(176, 480, 181, 487), new Tile(179, 483), "Varrock - Cook's Guild"),
  VARROCK_DANCING_DONKEY_INN(
      new Boundary(89, 528, 94, 539), new Tile(91, 529), "Varrock - Dancing Donkey Inn"),
  VARROCK_DIMINTHEIS_HOUSE(
      new Boundary(84, 521, 85, 523), new Tile(84, 522), "Varrock - Dimintheis' House"),
  VARROCK_EAST_BANK(new Boundary(98, 510, 106, 515), new Tile(102, 512), "Varrock - East Bank"),
  VARROCK_EAST_MINE(new Boundary(68, 542, 80, 550), new Tile(73, 545), "Varrock - East Mine"),
  VARROCK_GENERAL_STORE(
      new Boundary(124, 513, 129, 517), new Tile(126, 515), "Varrock - General Store"),
  VARROCK_GERTRUDES_HOUSE(
      new Boundary(163, 511, 168, 517), new Tile(165, 512), "Varrock - Gertrude's House"),
  VARROCK_GUIDORS_HOUSE(
      new Boundary(79, 530, 86, 535), new Tile(85, 533), "Varrock - Guidor's House"),
  VARROCK_JULIETS_HOUSE(
      new Boundary(165, 494, 174, 501), new Tile(168, 496), "Varrock - Juliet's House"),
  VARROCK_MARKET_SQUARE(
      new Boundary(126, 505, 137, 511), new Tile(134, 506), "Varrock - Market Square"),
  VARROCK_MUSEUM(new Boundary(97, 484, 106, 496), new Tile(100, 494), "Varrock - Museum"),
  VARROCK_PHOENIX_GANG_ENTRANCE(
      new Boundary(106, 534, 107, 536), new Tile(106, 535), "Varrock - Phoenix Gang Entrance"),
  VARROCK_RUNE_SHOP(
      new Boundary(101, 522, 103, 525), new Tile(101, 525), "Varrock - Aubury's Rune Shop"),
  VARROCK_SEWER_ENTRANCE(
      new Boundary(110, 473, 112, 475), new Tile(111, 475), "Varrock - Sewer Entrance"),
  VARROCK_SHEEP_PEN(new Boundary(96, 560, 109, 570), new Tile(104, 566), "Varrock - Sheep Pen"),
  VARROCK_SOUTH_ALTAR(new Boundary(96, 528, 101, 535), new Tile(98, 531), "Varrock - South Altar"),
  VARROCK_SPIRIT_TREE(
      new Boundary(159, 452, 161, 454), new Tile(161, 453), "Varrock - Spirit Tree"),
  VARROCK_STAFF_SHOP(
      new Boundary(138, 503, 142, 506), new Tile(140, 505), "Varrock - Zaff's Staff Shop"),
  VARROCK_STONE_CIRCLE(
      new Boundary(104, 545, 117, 558), new Tile(111, 553), "Varrock - Stone Circle"),
  VARROCK_SWORD_SHOP(new Boundary(133, 522, 138, 527), new Tile(134, 525), "Varrock - Sword Shop"),
  VARROCK_TAILORS_HOUSE(
      new Boundary(82, 524, 85, 526), new Tile(84, 525), "Varrock - Tailor's House"),
  VARROCK_TRAINING_ROOM(
      new Boundary(101, 499, 107, 505), new Tile(104, 502), "Varrock - Training Room"),
  VARROCK_WEST_BANK(new Boundary(147, 499, 153, 506), new Tile(150, 502), "Varrock - West Bank"),
  VARROCK_WEST_BANK_BASEMENT(
      new Boundary(147, 3330, 152, 3332), new Tile(149, 3332), "Varrock - West Bank Basement"),
  VARROCK_WEST_MINE(new Boundary(160, 531, 169, 548), new Tile(161, 541), "Varrock - West Mine"),
  VARROCK_WHEAT_FIELD(
      new Boundary(129, 558, 136, 569), new Tile(131, 559), "Varrock - Wheat Field"),
  WIZARDS_TOWER_BASEMENT(
      new Boundary(213, 3514, 225, 3526), new Tile(220, 3519), "Wizards' Tower - Basement"),
  WIZARDS_TOWER_ENTRANCE(
      new Boundary(214, 684, 220, 688), new Tile(217, 687), "Wizards' Tower - Entrance"),
  ZZ_RUNECRAFT_MYSTERIOUS_RUINS_AIR(
      new Boundary(305, 592, 309, 596), new Tile(307, 592), "Mysterious Ruins - Air Altar"),
  ZZ_RUNECRAFT_MYSTERIOUS_RUINS_BODY(
      new Boundary(258, 502, 262, 506), new Tile(261, 506), "Mysterious Ruins - Body Altar"),
  ZZ_RUNECRAFT_MYSTERIOUS_RUINS_CHAOS(
      new Boundary(231, 374, 235, 378), new Tile(233, 378), "Mysterious Ruins - Chaos Altar"),
  ZZ_RUNECRAFT_MYSTERIOUS_RUINS_COSMIC(
      new Boundary(105, 3564, 109, 3568), new Tile(105, 3566), "Mysterious Ruins - Cosmic Altar"),
  ZZ_RUNECRAFT_MYSTERIOUS_RUINS_EARTH(
      new Boundary(61, 463, 65, 467), new Tile(64, 467), "Mysterious Ruins - Earth Altar"),
  ZZ_RUNECRAFT_MYSTERIOUS_RUINS_FIRE(
      new Boundary(49, 632, 53, 636), new Tile(53, 634), "Mysterious Ruins - Fire Altar"),
  ZZ_RUNECRAFT_MYSTERIOUS_RUINS_MIND(
      new Boundary(296, 437, 300, 441), new Tile(298, 441), "Mysterious Ruins - Mind Altar"),
  ZZ_RUNECRAFT_MYSTERIOUS_RUINS_NATURE(
      new Boundary(391, 803, 395, 807), new Tile(394, 803), "Mysterious Ruins - Nature Altar"),
  ZZ_RUNECRAFT_MYSTERIOUS_RUINS_WATER(
      new Boundary(145, 682, 150, 687), new Tile(150, 684), "Mysterious Ruins - Water Altar");

  // ZZ_RUNECRAFT_MYSTERIOUS_RUINS_LAW(new Boundary(), new Tile(), "Mysterious Ruins - Law Altar"),
  //  NOT
  // ADDED
  // ZZ_RUNECRAFT_MYSTERIOUS_RUINS_DEATH(new Boundary(), new Tile(), "Mysterious Ruins - Death
  // Altar"), NOT
  // ADDED
  // ZZ_RUNECRAFT_MYSTERIOUS_RUINS_BLOOD(new Boundary(), new Tile(), "Mysterious Ruins - Blood
  // Altar"), NOT
  // ADDED

  // TREE_GNOME_STRONGHOLD_SPIRIT_TREE(new Boundary(), new Tile(), "Tree Gnome Stronghold - Spirit
  // Tree"),
  // TREE_GNOME_VILLAGE_SPIRIT_TREE(new Boundary(), new Tile(), "Tree Gnome Village - Spirit Tree"),
  // BATTLEFIELD_SPIRIT_TREE(new Boundary(), new Tile(), "Battlefield - Spirit Tree"),

  private static final Controller c = Main.getController();
  private static final WebWalker w = new WebWalker(new WebwalkGraph("assets/map/graph.txt"));

  // TODO: Update this array as new banks get added
  private static final Location[] bankArray = {
    ARDOUGNE_SOUTH_BANK,
    DRAYNOR_BANK,
    EDGEVILLE_BANK,
    FALADOR_EAST_BANK,
    FALADOR_WEST_BANK,
    VARROCK_EAST_BANK,
    VARROCK_WEST_BANK
  };

  private final Boundary boundary;
  private final Tile standableTile;
  private final String description;

  /**
   * @param boundary Boundary - Rectangular area that makes up the location
   * @param standableTile Tile - Standable tile within the boundary
   * @param description String - Description of the location
   */
  Location(Boundary boundary, Tile standableTile, String description) {
    this.boundary = sortBoundary(boundary);
    // If a location's standable tile is null, it will default to the first corner of its boundary
    this.standableTile =
        standableTile != null
            ? new Tile(standableTile.getX(), standableTile.getY())
            : new Tile(this.boundary.getX1(), this.boundary.getY1());
    this.description = description;
  }

  /**
   * Attempts to walk towards the specified location's standable tile. If this fails, WebWalker may
   * need to be updated to support the location you're attempting to navigate to.
   */
  public void walkTowards() {
    if (c.isRunning()) {
      int x = getX();
      int y = getY();
      if (notAtCoords(x, y)) {
        c.displayMessage(
            "@yel@Attempting to walk to: @cya@" + Location.getDescriptionFromStandableTile(x, y));
        System.out.println(
            "\nAttempting to walk to: " + Location.getDescriptionFromStandableTile(x, y));
        System.out.println(
            "If this fails, WebWalker might need to be updated to include the area.");
        int failedAttempts = 0;
        while (notAtCoords(x, y) && c.isRunning()) {
          failedAttempts = !c.walkTowards(x, y) ? ++failedAttempts : 0;
          if (failedAttempts >= 5) {
            c.log("Failed to walk to specified location. WebWalker may need to be updated.", "red");
            c.stop();
          }
          c.sleep(100);
        }
      }
    }
  }

  /**
   * Attempts to walk to the closest Location in an array of Locations.
   *
   * @param locations Location[] -- Array of locations
   */
  public static void walkTowardsClosest(Location[] locations) {
    if (locations == null || locations.length < 1) return;
    getClosest(locations).walkTowards();
  }

  /** Attempts to walk to the closest bank. */
  public static void walkTowardsNearestBank() {
    walkTowardsClosest(bankArray);
  }

  /**
   * Returns the closest Location from a given array of Locations.
   *
   * @param locations Location[] -- Array of locations to check
   * @return Location
   */
  private static Location getClosest(Location[] locations) {
    if (locations == null || locations.length < 1) return null;
    // Makes the array objects distinct.
    Location[] distinctLocations = Arrays.stream(locations).distinct().toArray(Location[]::new);
    Map<Location, Integer> locationMap =
        Arrays.stream(distinctLocations).collect(Collectors.toMap(l -> l, Location::distanceTo));
    return Collections.min(locationMap.entrySet(), Comparator.comparingInt(Map.Entry::getValue))
        .getKey();
  }

  /**
   * Returns a rough Geodesic distance from the player's current position to the standable tile of
   * the Location. Geodesic distance is the distance between two points while considering terrain
   * and navigability.
   *
   * @return int
   */
  public int distanceTo() {
    return w.distanceBetween(c.currentX(), c.currentY(), getX(), getY());
  }

  /**
   * Returns a rough Geodesic distance from this Location to a second Location. Geodesic distance is
   * the distance between two points while considering terrain and navigability.
   *
   * @param loc Location -- Location to check
   * @return int
   */
  public int distanceBetween(Location loc) {
    return w.distanceBetween(loc.getX(), loc.getY(), getX(), getY());
  }

  /**
   * Returns a rough Geodesic distance between two locations. Geodesic distance is the distance
   * between two points while considering terrain and navigability.
   *
   * @param loc1 Location -- Location to check
   * @param loc2 Location -- Location to check
   * @return int
   */
  public static int distanceBetween(Location loc1, Location loc2) {
    return w.distanceBetween(loc1.getX(), loc1.getY(), loc2.getX(), loc2.getY());
  }

  /**
   * Returns whether the player is not at the specified coordinates.
   *
   * @param x int -- X Coordinate
   * @param y int -- Y Coordinate
   * @return boolean
   */
  private static boolean notAtCoords(int x, int y) {
    return c.currentX() != x || c.currentY() != y;
  }

  /**
   * Returns the Location's Boundary
   *
   * @return Boundary
   */
  private Boundary getBoundary() {
    return boundary;
  }

  /**
   * Returns the X coordinate of the Location's standable tile.
   *
   * @return int
   */
  public int getX() {
    return standableTile.getX();
  }

  /**
   * Returns the Y coordinate of the Location's standable tile.
   *
   * @return int
   */
  public int getY() {
    return standableTile.getY();
  }

  /**
   * Returns the description of a start location
   *
   * @return String
   */
  public String getDescription() {
    return description;
  }

  /**
   * Sorts the coordinates of the boundary so that the first coordinate pair is always lower than
   * the second.
   *
   * @param boundary Boundary -- Boundary to sort
   * @return Boundary
   */
  private static Boundary sortBoundary(Boundary boundary) {
    int x1 = Math.min(boundary.getX1(), boundary.getX2());
    int y1 = Math.min(boundary.getY1(), boundary.getY2());
    int x2 = Math.max(boundary.getX1(), boundary.getX2());
    int y2 = Math.max(boundary.getY1(), boundary.getY2());
    return new Boundary(x1, y1, x2, y2);
  }

  /**
   * Returns whether the player is within the Location's boundary rectangle.
   *
   * @param loc Location -- Location to check
   * @return boolean
   */
  public static boolean isAtLocation(Location loc) {
    int cX = c.currentX();
    int cY = c.currentY();
    Boundary b = loc.getBoundary();

    return cX >= b.getX1() && cX <= b.getX2() && cY >= b.getY1() && cY <= b.getY2();
  }

  /**
   * Returns whether the player is within the Location's boundary rectangle.
   *
   * @return boolean
   */
  public boolean isAtLocation() {
    int cX = c.currentX();
    int cY = c.currentY();
    Boundary b = getBoundary();

    return cX >= b.getX1() && cX <= b.getX2() && cY >= b.getY1() && cY <= b.getY2();
  }

  /**
   * Returns either the description of a Location with a matching standable tile, or (X, Y)
   * coordinates as a string.
   *
   * @param x int - X coordinate to check for description
   * @param y int - Y coordinate to check for description
   * @return String - Location description
   */
  public static String getDescriptionFromStandableTile(int x, int y) {
    return Arrays.stream(Location.values())
        .filter(loc -> loc.getX() == x && loc.getY() == y)
        .findFirst()
        .map(Location::getDescription)
        .orElse(String.format("(%s, %s)", x, y));
  }
}

class Boundary {
  private final int x1;
  private final int y1;
  private final int x2;
  private final int y2;

  Boundary(int x1, int y1, int x2, int y2) {
    this.x1 = x1;
    this.y1 = y1;
    this.x2 = x2;
    this.y2 = y2;
  }

  public int getX1() {
    return x1;
  }

  public int getY1() {
    return y1;
  }

  public int getX2() {
    return x2;
  }

  public int getY2() {
    return y2;
  }

  public String toString() {
    return String.format("(%s, %s), (%s, %s)", x1, y1, x2, y2);
  }
}

class Tile {
  private final int x;
  private final int y;

  Tile(int x, int y) {
    this.x = x;
    this.y = y;
  }

  public int getX() {
    return x;
  }

  public int getY() {
    return y;
  }

  public String toString() {
    return String.format("(%s, %s)", x, y);
  }
}
