import java.io.*;
import java.util.*;

public class PuzzleSolver {
    private char[][] board;
    private List<Block> blocks;
    private int N, M;
    private int iterationCount = 0;

    public PuzzleSolver(String fileName) throws IOException {
        readInput(fileName);
    }

    private void readInput(String fileName) throws IOException {
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String[] dims = br.readLine().split(" ");
        N = Integer.parseInt(dims[0]);
        M = Integer.parseInt(dims[1]);
        int P = Integer.parseInt(dims[2]);

        String type = br.readLine();
        board = new char[N][M];
        for (char[] row : board) Arrays.fill(row, '.');

        blocks = new ArrayList<>();
        String currentSymbol = null;
        List<String> currentShape = new ArrayList<>();

        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) continue;
            char firstChar = line.charAt(0);
            if (currentSymbol == null || firstChar != currentSymbol.charAt(0)) {
                if (!currentShape.isEmpty()) {
                    blocks.add(new Block(currentSymbol.charAt(0), new ArrayList<>(currentShape)));
                    currentShape.clear();
                }
                currentSymbol = String.valueOf(firstChar);
            }
            currentShape.add(line);
        }
        if (!currentShape.isEmpty()) {
            blocks.add(new Block(currentSymbol.charAt(0), new ArrayList<>(currentShape)));
        }
        br.close();
    }

    public boolean solve() {
        return solveRecursive(0);
    }

    private boolean solveRecursive(int index) {
        if (index == blocks.size()) return isValidBoard();

        Block block = blocks.get(index);
        List<Block> orientations = block.generateOrientations();
        System.out.println("Menghasilkan orientasi unik untuk blok " + block.symbol + ": " + orientations.size());
/*         for (Block orient : orientations) {
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < M; c++) {
                    //iterationCount++;
                    if (canPlace(orient, r, c)) {
                        placeBlock(orient, r, c, block.symbol);
                        if (solveRecursive(index + 1)) return true;
                        removeBlock(orient, r, c);
                    }
                }
            }
        } */
        for (int r = 0; r < N; r++) {
            for (int c = 0; c < M; c++) {
                for (Block orient : orientations) {  
                    if (canPlace(orient, r, c)) { 
                        iterationCount++; 
                        placeBlock(orient, r, c, block.symbol);
                        printBoard(); 
                        System.out.println("---------------------------------");
                        if (solveRecursive(index + 1)) return true;  
    
                        removeBlock(orient, r, c);  
                        System.out.println("Backtracking: Menghapus blok " + block.symbol + " dari (" + r + ", " + c + ")");
                        printBoard();
                    }
                }
            }
        }
        return false; // Jika sudah coba di satu posisi tapi gagal, langsung keluar
    }

/*     private boolean solveRecursive(int index) {
        if (iterationCount > 1000000) { // Batas iterasi, bisa disesuaikan
            System.out.println("❌ Terlalu banyak iterasi, kemungkinan loop tak berujung.");
            return false;
        }
    
        System.out.println("\n🔄 Iterasi ke-" + iterationCount + " | Index: " + index);
        iterationCount++;
    
        if (index == blocks.size()) {
            System.out.println("✅ Semua blok telah ditempatkan. Memeriksa solusi...");
            return isValidBoard();
        }
    
        Block block = blocks.get(index);
        List<Block> orientations = block.generateOrientations();
        System.out.println("📦 Mencoba menempatkan blok: " + block.symbol + " (" + orientations.size() + " orientasi)");
    
        for (Block orient : orientations) {
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < M; c++) {
                    System.out.println("🔎 Coba di posisi: (" + r + ", " + c + ")");
    
                    if (canPlace(orient, r, c)) {
                        System.out.println("✅ Berhasil menempatkan blok " + block.symbol + " di (" + r + ", " + c + ")");
                        placeBlock(orient, r, c, block.symbol);
                        printBoard();
    
                        if (solveRecursive(index + 1)) {
                            return true;
                        }
    
                        System.out.println("❌ Backtracking! Melepas blok " + block.symbol + " dari (" + r + ", " + c + ")");
                        removeBlock(orient, r, c);
                        printBoard();
                    }
                }
            }
        }
    
        System.out.println("❌ Tidak bisa menempatkan blok " + block.symbol + ". Kembali ke langkah sebelumnya.");
        return false;
    }
    
 */
    private boolean canPlace(Block block, int startX, int startY) {
        for (int[] cell : block.coordinates) {
            int x = startX + cell[0];
            int y = startY + cell[1];
            if (x < 0 || x >= N || y < 0 || y >= M || board[x][y] != '.') {
                return false;
            }
        }
        return true;
    }

    private void placeBlock(Block block, int startX, int startY, char symbol) {
        for (int[] cell : block.coordinates) {
            int x = startX + cell[0];
            int y = startY + cell[1];
            board[x][y] = symbol;
        }
    }

    private void removeBlock(Block block, int startX, int startY) {
        for (int[] cell : block.coordinates) {
            int x = startX + cell[0];
            int y = startY + cell[1];
            board[x][y] = '.';
        }
    }

    private boolean isValidBoard() {
        // Validasi jumlah blok yang harus terisi (sesuai spesifikasi)
        int filledCells = 0;
        for (char[] row : board) {
            for (char cell : row) {
                if (cell != '.') filledCells++;
            }
        }
        return filledCells == (N*M); // Papan 5x5 harus penuh
    }

    public void printBoard() {
        for (char[] row : board) {
            for (char cell : row) {
                System.out.print(Utils.getColoredChar(cell) + " ");
            }
            System.out.println();
        }
    }

    public void saveSolution(String fileName) throws IOException {
        BufferedWriter bw = new BufferedWriter(new FileWriter(fileName));
        for (char[] row : board) {
            for (char cell : row) {
                bw.write(cell + " ");
            }
            bw.newLine();
        }
        bw.close();
    }

    public int getIterationCount() {
        return iterationCount;
    }
}
