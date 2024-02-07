package models.entities;

import com.openrsc.client.entityhandling.instances.Item;

/**
 * This is essentially a copy of the AuctionItem class from com.openrsc.interfaces.misc.AuctionHouse
 */
public class AuctionItem {

  private final int auctionID, priceTotal;
  private final String seller;
  private final Item item;

  /**
   * @param auctionId int -- Id of the auction
   * @param itemId int -- Id of item for sale
   * @param amount int -- Amount of items
   * @param priceTotal int -- Total price set for the auction
   * @param seller String -- Name of the seller
   */
  public AuctionItem(int auctionId, int itemId, int amount, int priceTotal, String seller) {
    this.item = new Item();
    this.item.setItemDef(itemId);
    this.item.setAmount(amount);
    this.auctionID = auctionId;
    this.priceTotal = priceTotal;
    this.seller = seller;
  }

  /**
   * Returns the auction's id
   *
   * @return int
   */
  public int getAuctionId() {
    return auctionID;
  }

  /**
   * Returns the auction's item id
   *
   * @return int
   */
  public int getItemId() {
    return this.item.getItemDef().id;
  }

  /**
   * Returns the auction's item amount
   *
   * @return int
   */
  public int getAmount() {
    return this.item.getAmount();
  }

  /**
   * Returns the auction's total price
   *
   * @return int
   */
  public int getPriceTotal() {
    return priceTotal;
  }

  /**
   * Returns the auction's price per item
   *
   * @return int
   */
  public int getPricePerItem() {
    return this.priceTotal / this.getAmount();
  }

  /**
   * Returns the auction's seller name
   *
   * @return String
   */
  public String getSeller() {
    return seller;
  }
}
