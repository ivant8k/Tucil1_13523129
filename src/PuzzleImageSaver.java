import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class PuzzleImageSaver {
    private static final int CELL_SIZE = 50; // Ukuran tiap sel
    private static final int PADDING = 10;   // Padding sekitar board

    public static void savePuzzleImage(char[][] board, String filename) {
        int rows = board.length;
        int cols = board[0].length;
        int width = cols * CELL_SIZE + 2 * PADDING;
        int height = rows * CELL_SIZE + 2 * PADDING;

        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g2d = image.createGraphics();

        // Set warna background putih
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, width, height);

        // Gambar grid dan blok
        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                char symbol = board[r][c];
                g2d.setColor(getColorForSymbol(symbol));
                g2d.fillRect(PADDING + c * CELL_SIZE, PADDING + r * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                // Gambar border
                g2d.setColor(Color.BLACK);
                g2d.drawRect(PADDING + c * CELL_SIZE, PADDING + r * CELL_SIZE, CELL_SIZE, CELL_SIZE);

                // Tulis huruf blok
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 20));
                g2d.drawString(String.valueOf(symbol), PADDING + c * CELL_SIZE + 20, PADDING + r * CELL_SIZE + 30);
            }
        }

        g2d.dispose();

        // Simpan sebagai file PNG
        try {
            ImageIO.write(image, "png", new File(filename));
            System.out.println("Gambar solusi disimpan sebagai " + filename);
        } catch (IOException e) {
            System.err.println("Gagal menyimpan gambar: " + e.getMessage());
        }
    }

    // Fungsi untuk memilih warna blok berdasarkan huruf
    private static Color getColorForSymbol(char symbol) {
        switch (symbol) {
            case 'A': return Color.RED;
            case 'B': return Color.BLUE;
            case 'C': return Color.GREEN;
            case 'D': return Color.ORANGE;
            case 'E': return Color.MAGENTA;
            case 'F': return Color.CYAN;
            case 'G': return Color.PINK;
            default: return Color.LIGHT_GRAY;
        }
    }
}
