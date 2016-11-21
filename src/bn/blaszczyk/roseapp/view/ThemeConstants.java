package bn.blaszczyk.roseapp.view;

import java.awt.Color;
import java.awt.Font;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public interface ThemeConstants {
	
	
	public static final Color LLGRAY = new Color(247,247,247);
	public static final Color LGRAY = new Color(238,238,238);
	
	// Basic Panel
	
	public static final int LBL_HEIGHT = 25;
	public static final int H_SPACING = 10;
	public static final int V_SPACING = 5;
	
	public static final int PROPERTY_WIDTH = 200;
	public static final Font PROPERTY_FONT = new Font("Arial", Font.BOLD, 18);
	public static final Color PROPERTY_BG = Color.LIGHT_GRAY;
	public static final Color PROPERTY_FG = Color.BLACK;
	
	public static final int VALUE_WIDTH = 500;
	public static final Font VALUE_FONT = new Font("Arial", Font.BOLD, 18);
	public static final Color VALUE_BG = Color.LIGHT_GRAY;
	public static final Color VALUE_FG = Color.BLACK;
	
	public static final Color BASIC_PNL_BACKGROUND = LLGRAY;
	
	public static final int BASIC_WIDTH = VALUE_WIDTH + H_SPACING + PROPERTY_WIDTH;
	
	
	// Full Panel
	
	public static final int TITLE_WIDTH = 400;
	public static final int TITLE_HEIGHT = 35;
	public static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 30);
	public static final Color TITLE_BG = LGRAY;
	public static final Color TITLE_FG = Color.BLACK;
	
	public static final int SUBTITLE_WIDTH = 300;
	public static final int SUBTITLE_HEIGHT = 30;
	public static final Font SUBTITLE_FONT = new Font("Arial", Font.BOLD, 25);
	public static final Color SUBTITLE_BG = LGRAY;
	public static final Color SUBTITLE_FG = Color.BLACK;
	
	public static final int SUBTLTBTN_WIDTH = 100;
	public static final int SUBTABLE_HEIGTH = 250;
	
	public static final Color FULL_PNL_BACKGROUND = LLGRAY;
	
	public static final int V_OFFSET = 20;	
	
	// Table
	
	public static final int CELL_HEIGTH = 25;
	public static final int CELL_WIDTH = 30;
	
	public static final int CELL_SPACING = 0;
	
	public static final int BUTTON_WIDTH = 24;
	public static final int BUTTON_HEIGHT = 35;

	public static final DateFormat  DATE_FORMAT = new SimpleDateFormat("dd.MM.yyyy");
	public static final NumberFormat INT_FORMAT = NumberFormat.getIntegerInstance();
	public static final DecimalFormat BIG_DEC_FORMAT = ((DecimalFormat) NumberFormat.getNumberInstance(Locale.GERMAN));
	public static final NumberFormat DOUBLE_FORMAT = new DecimalFormat("0.000",DecimalFormatSymbols.getInstance(Locale.GERMAN));
	
	public static final Color ODD_BG = Color.WHITE;
	public static final Color ODD_FG = Color.BLACK;
	public static final Font ODD_FONT = new Font("Arial",Font.PLAIN,16);
	
	public static final Color EVEN_BG = Color.LIGHT_GRAY;
	public static final Color EVEN_FG = Color.BLACK;
	public static final Font EVEN_FONT = new Font("Arial",Font.PLAIN,16);

	public static final Font HEADER_FONT = new Font("Arial",Font.BOLD,16);
	public static final Color HEADER_BG = LGRAY;
	
	// Main Frame

	public static final int MF_WIDTH = 1920;
	public static final int MF_HEIGTH = 1000;
	
	public static final int PANEL_HEIGHT = 800;
	public static final int TABLE_HEIGHT = 150;
	public static final int FULL_TABLE_WIDTH = MF_WIDTH - 3 * H_SPACING;
	
	
	// StartPanel
	
	public static final int START_BTN_HEIGHT = 100;
	public static final int START_BTN_WIDTH = 700;
	public static final Font START_BTN_FONT = new Font("Arial",Font.BOLD,48);
	public static final int START_V_SPACING = 50;
	public static final int START_H_SPACING = 600;

	
	// SelectDialog
	
	public static final int SEL_DIAL_WIDTH = 700;
	public static final int SEL_DIAL_HEIGTH = 250;
	
	public static final int SEL_DIAL_BOX_WIDTH = 650;
	public static final int SEL_DIAL_BOX_HEIGHT = LBL_HEIGHT;
	
}
