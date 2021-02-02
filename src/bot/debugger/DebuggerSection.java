package bot.debugger;

public class DebuggerSection {
    public DebuggerSectionType sectionType = null;
    public String sectionName = null;

    public DebuggerSectionJTable table = null;

    public DebuggerSection(DebuggerSectionType sectionType, String sectionName, String[] sectionColumnNames) {
        this.sectionType = sectionType;
        this.sectionName = sectionName;

        this.table = new DebuggerSectionJTable(sectionColumnNames);
    }

    @Override
    public String toString() {
        return sectionName;
    }
}
