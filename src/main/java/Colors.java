import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class Colors {
    private static final List<Color> colors = List.of(
            Color.RED,
            Color.GREEN,
            Color.MAGENTA,
            Color.YELLOW,
            Color.BLUE,
            Color.LIGHT_GRAY,
            Color.ORANGE,
            Color.CYAN,
            Color.GRAY,
            Color.PINK,
            new Color(0, 255, 51),
            new Color(0, 102, 0),
            new Color(162, 193, 242),
            new Color(255, 255, 204),
            new Color(255, 255, 153),
            new Color(255, 204, 51),
            new Color(80, 40, 200),
            new Color(255, 40, 10),
            new Color(200, 150, 150),
            new Color(255, 117, 20),
            new Color(137, 18, 121),
            new Color(0, 143, 57),
            new Color(174, 160, 75),
            new Color(197, 29, 52),
            new Color(93, 155, 155),
            new Color(199, 180, 70),
            new Color(76, 145, 65),
            new Color(255, 117, 20),
            new Color(102, 255, 102),
            new Color(34, 113, 179),
            new Color(222, 76, 138),
            new Color(152, 195, 190),
            new Color(208, 208, 208),
            new Color(0, 188, 45),
            new Color(164, 125, 144),
            new Color(189, 236, 182),
            new Color(215, 45, 109),
            new Color(87, 151, 255),
            new Color(19, 235, 26),
            new Color(250, 122, 239),
            new Color(240, 42, 63),
            new Color(252, 247, 144)
    );

    static Color getColor(int id) {
        return colors.get(id % colors.size());
    }
}
