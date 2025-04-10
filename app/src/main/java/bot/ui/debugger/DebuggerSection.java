package bot.ui.debugger;

import bot.ui.components.table.Table;

public class DebuggerSection {
  public DebuggerSectionType sectionType = null;
  public String sectionName = null;

  public Table table = null;

  public DebuggerSection(
      DebuggerSectionType sectionType,
      String sectionName,
      String[] sectionColumnNames,
      int initialSortedColumn) {
    this.sectionType = sectionType;
    this.sectionName = sectionName;

    this.table = new Table(sectionColumnNames);

    this.table.getRowSorter().toggleSortOrder(initialSortedColumn);
  }

  public void setColumnTypes(Class[] columnTypes) {
    this.table.setColumnTypes(columnTypes);
  }

  @Override
  public String toString() {
    return sectionName;
  }
}
