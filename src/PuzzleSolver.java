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
    
        // Membaca ukuran papan (N x M) dan jumlah blok (P)
        String[] dims = br.readLine().split(" ");
        N = Integer.parseInt(dims[0]);  // Jumlah baris papan
        M = Integer.parseInt(dims[1]);  // Jumlah kolom papan
        int P = Integer.parseInt(dims[2]); // Jumlah blok
    
        // Membaca mode puzzle (DEFAULT)
        String type = br.readLine();
    
        // Inisialisasi papan kosong ('.')
        board = new char[N][M];
        for (char[] row : board) Arrays.fill(row, '.');
    
        // Menyimpan daftar blok yang akan digunakan
        blocks = new ArrayList<>();
        Set<Character> usedSymbols = new HashSet<>(); // Untuk mengecek duplikasi huruf
    
        String currentSymbol = null;
        List<String> currentShape = new ArrayList<>();
    
        String line;
        while ((line = br.readLine()) != null) {
            if (line.isEmpty()) continue; // Lewati baris kosong
    
            char firstChar = line.charAt(0);
    
            // **Validasi: Pastikan hanya huruf kapital (A-Z)**
            if (!Character.isUpperCase(firstChar) || firstChar < 'A' || firstChar > 'Z') {
                throw new IllegalArgumentException("Error: Karakter blok harus berupa huruf kapital A-Z! Ditemukan: " + firstChar);
            }
    
            // Jika menemukan blok baru (karakter berubah)
            if (currentSymbol == null || firstChar != currentSymbol.charAt(0)) {
                if (!currentShape.isEmpty()) {
                    // **Validasi: Pastikan blok tidak kosong**
                    if (!hasValidCharacter(currentShape)) {
                        throw new IllegalArgumentException("Error: Blok " + currentSymbol + " tidak memiliki bentuk valid!");
                    }
    
                    blocks.add(new Block(currentSymbol.charAt(0), new ArrayList<>(currentShape)));
                    currentShape.clear();
                }
                currentSymbol = String.valueOf(firstChar);
    
                // **Validasi: Pastikan huruf tidak duplikat**
                if (usedSymbols.contains(currentSymbol.charAt(0))) {
                    throw new IllegalArgumentException("Error: Blok " + currentSymbol + " sudah ada! Tidak boleh ada duplikasi.");
                }
                usedSymbols.add(currentSymbol.charAt(0));
            }
            currentShape.add(line); // Tambahkan baris ke bentuk blok
        }
    
        // Tambahkan blok terakhir yang belum tersimpan
        if (!currentShape.isEmpty()) {
            if (!hasValidCharacter(currentShape)) {
                throw new IllegalArgumentException("Error: Blok " + currentSymbol + " tidak memiliki bentuk valid!");
            }
            blocks.add(new Block(currentSymbol.charAt(0), new ArrayList<>(currentShape)));
        }
    
        br.close();
    
        // **Validasi: Pastikan jumlah blok sesuai dengan P**
        if (blocks.size() != P) {
            throw new IllegalArgumentException("Error: Jumlah blok tidak sesuai! Diharapkan: " + P + ", tetapi ditemukan: " + blocks.size());
        }
    }
    // Fungsi untuk memastikan blok memiliki minimal satu huruf yang valid
    private boolean hasValidCharacter(List<String> shape) {
        for (String row : shape) {
            for (char c : row.toCharArray()) {
                if (Character.isUpperCase(c) && c >= 'A' && c <= 'Z') {
                    return true; // Ditemukan karakter huruf kapital yang valid
                }
            }
        }
        return false; // Tidak ada huruf kapital dalam blok ini
    }
        
    public boolean solve() {
        System.out.println("Memulai pencarian solusi...");
        return solveRecursive(0);
    }
    

    private boolean solveRecursive(int index) {
        if (index == blocks.size()) return isValidBoard(); // cek valid

        Block block = blocks.get(index);
        List<Block> orientations = block.generateOrientations();

        for (Block orient : orientations) {  
            for (int r = 0; r < N; r++) {
                for (int c = 0; c < M; c++) {
                    if (canPlace(orient, r, c)) {
                        placeBlock(orient, r, c, block.symbol);
                        if (solveRecursive(index + 1)) return true; // Jika berhasil, langsung return
                        removeBlock(orient, r, c);
                        iterationCount++;
                    }
                }
            }
        }
        return false; // backtrack
    }
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
