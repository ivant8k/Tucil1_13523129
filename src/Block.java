import java.util.*;

public class Block {
    public char symbol;
    public List<int[]> coordinates;

    public Block(char symbol, List<String> shape) {
        this.symbol = symbol;
        this.coordinates = parseShape(shape);
    }

    private List<int[]> parseShape(List<String> shape) {
        List<int[]> coords = new ArrayList<>();
        for (int i = 0; i < shape.size(); i++) {
            for (int j = 0; j < shape.get(i).length(); j++) {
                if (shape.get(i).charAt(j) != '.') {
                    coords.add(new int[]{i, j});
                }
            }
        }
        return coords;
    }

    public List<Block> generateOrientations() {
        Set<String> seen = new HashSet<>();
        List<Block> orientations = new ArrayList<>();

        for (int r = 0; r < 4; r++) {
            List<int[]> rotated = new ArrayList<>();
            for (int[] cell : coordinates) {
                int x = cell[0], y = cell[1];
                for (int i = 0; i < r; i++) {
                    int temp = x;
                    x = y;
                    y = -temp;
                }
                rotated.add(new int[]{x, y});
            }
            normalize(rotated);
            if (seen.add(rotated.toString())) {
                orientations.add(new Block(this.symbol, coordsToShape(rotated)));
            }

            // Mirror horizontal
            List<int[]> mirrored = new ArrayList<>();
            for (int[] cell : rotated) {
                mirrored.add(new int[]{cell[0], -cell[1]});
            }
            normalize(mirrored);
            if (seen.add(mirrored.toString())) {
                orientations.add(new Block(this.symbol, coordsToShape(mirrored)));
            }
        }

        return orientations;
    }

    private void normalize(List<int[]> coords) {
        int minX = Integer.MAX_VALUE;
        int minY = Integer.MAX_VALUE;
        for (int[] c : coords) {
            minX = Math.min(minX, c[0]);
            minY = Math.min(minY, c[1]);
        }
        for (int[] c : coords) {
            c[0] -= minX;
            c[1] -= minY;
        }
    }

    private List<String> coordsToShape(List<int[]> coords) {
        int maxX = 0, maxY = 0;
        for (int[] c : coords) {
            maxX = Math.max(maxX, c[0]);
            maxY = Math.max(maxY, c[1]);
        }

        char[][] shape = new char[maxX + 1][maxY + 1];
        for (char[] row : shape) Arrays.fill(row, '.');

        for (int[] c : coords) {
            shape[c[0]][c[1]] = this.symbol;
        }

        List<String> result = new ArrayList<>();
        for (char[] row : shape) {
            result.add(new String(row));
        }
        return result;
    }
}
