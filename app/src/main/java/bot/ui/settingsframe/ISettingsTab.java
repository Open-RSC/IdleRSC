package bot.ui.settingsframe;

/** Interface for creating tabs for the settings frame */
public interface ISettingsTab {

  /*
    Here's an example of how a tab's constructor and fields should be formatted:

      final SpringLayout sl; // The SpringLayout LayoutManager passed to the constructor
      final int compWidth = 180; // Generic component width to use in setConstraints()
      final int compHeight = 41; // Generic component height to use in setConstraints()

      // Define components with public access without initializing them.
      // They will be initialized later in initializeComponents()
      // They must be public so SettingsFrame can access them (Unless you want to write a getter for every field)
      public TextFieldPanel field1, field2;

      RandomTab(SpringLayout springLayout) {
        // Set the layout for the panel
        super(springLayout);

        // Set the sl field for use in setConstraints()
        sl = springLayout;

        // Initialize our components
        initializeComponents();

        // Set their constraints (place components in the panel bounds)
        setConstraints();
      }
  */

  /**
   * Initialize components, add them to the panel, and add any action listeners in here <br>
   * Must be called in the tab constructor before setConstraints()
   */
  void initializeComponents();

  /**
   * Set SpringLayout constraints in here <br>
   * Must be called in the tab constructor after initializeComponents()
   */
  void setConstraints();
}
