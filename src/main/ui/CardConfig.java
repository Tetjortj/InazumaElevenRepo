package main.ui;

/**
 * Configuración de tamaños, padding y proporciones
 * para CardView y MiniCardView.
 */
public final class CardConfig {
    private CardConfig() {} // no instanciable

    // --- Tamaños de la carta completa (CardView) ---
    public static final double FULL_WIDTH       = 180;
    public static final double FULL_HEIGHT      = 260;
    public static final double ARC_RADIUS       = 20;

    // --- Tamaños de la mini‐carta (MiniCardView) ---
    public static final double MINI_WIDTH       = 180;
    public static final double MINI_HEIGHT      = 160;
    /** Desplazamiento vertical para centrar el clip de la mini‐carta */
    public static final double MINI_SHIFT_Y     = 0;

    // --- Espaciados generales ---
    /** Espacio vertical entre elementos en el VBox principal */
    public static final double CONTENT_SPACING        = 5;
    /** Padding interior de la carta */
    public static final double CONTENT_PADDING        = 8;
    /** Espacio horizontal en la fila superior (escudo+iconos+foto) */
    public static final double TOP_ROW_SPACING        = 5;

    // --- Iconos y escudos ---
    /** Tamaño (ancho y alto) genérico para iconos de posición/elemento/grado */
    public static final double ICON_SIZE              = 22;
    /** Espaciado vertical entre cada icono dentro de la caja de iconos */
    public static final double ICON_SPACING           = 5;

    /** Ancho de la caja donde va el escudo (FULL_WIDTH * ratio) */
    public static final double SHIELD_BOX_RATIO       = 0.22;
    /** Ajustes de tamaño de escudo por equipo */
    public static final double TEAM_LOGO_DEFAULT      = 50;
    public static final double TEAM_LOGO_ALT_SMALL    = 40; // p.ej. Raimon, Zeus
    public static final double TEAM_LOGO_ALT_SMALLEST = 35; // p.ej. Street_Sallys

    // --- Retrato de jugador ---
    /** Tamaño cuadrado del marco de la foto */
    public static final double PLAYER_FRAME_SIZE      = 96;
    /** Padding-top dentro del marco de la foto */
    public static final double PLAYER_FRAME_PADDING   = 8;
    /** Ratio ancho/alto del marco respecto a FULL_WIDTH */
    public static final double PLAYER_FRAME_RATIO     = 0.53;

    // --- Sección de stats ---
    /** Gaps dentro del GridPane de stats (horizontal y vertical) */
    public static final double STATS_GRID_HGAP        = 10;
    public static final double STATS_GRID_VGAP        = -5;
    /** Padding aplicado a cada etiqueta de stat */
    public static final double STAT_PADDING           = 5;
    /** Tamaño de fuente de cada stat */
    public static final double STAT_FONT_SIZE         = 14;

    /** Proporción de altura de la sección de stats respecto a FULL_HEIGHT */
    public static final double STATS_SECTION_RATIO    = 0.4;
    /** Proporción de padding‐bottom tras la sección de stats */
    public static final double STATS_BOTTOM_PAD_RATIO = 0.10;

    // --- Separador y nombre ---
    /** Ancho del separador (ratio sobre FULL_WIDTH) */
    public static final double SEPARATOR_WIDTH_RATIO  = 0.90;
    /** Proporción de padding‐top para el nombre (sobre FULL_HEIGHT) */
    public static final double NAME_PAD_TOP_RATIO     = 0.04;

    // --- Tipografía ----------
    /** Tamaño de la fuente del nombre (ratio sobre FULL_WIDTH) */
    public static final double NAME_FONT_RATIO        = 0.10;
    /** Tamaño de la fuente de GUTS (px fijo) */
    public static final double GUTS_FONT_SIZE         = 14;
    /** Tamaño de la fuente del score (ratio sobre FULL_WIDTH) */
    public static final double SCORE_FONT_RATIO       = 0.17;
    /** Márgenes para el score (ratio sobre FULL_HEIGHT y FULL_WIDTH) */
    public static final double SCORE_MARGIN_TOP_RATIO  = 0.02;
    public static final double SCORE_MARGIN_LEFT_RATIO = 0.08;
}
