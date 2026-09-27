import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.sound.sampled.Clip;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import Modules.*;

import java.util.Random;
import java.util.concurrent.Executors;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class GameMain {
    public static void main(String[] args) {
        GameMain session = new GameMain();
        System.out.println("Ended game at: " + session.countTotalSession + " seconds.");
    }

    long countGameSession = 0;
    public long countTotalSession = 0;
    boolean isInGaming = false, isSessionRunning = false;

    MAudioPlayer myAudioPlr = new MAudioPlayer();
    UIConstants textSetts = new UIConstants();

    String playerName;

    void setname(String name) {
        playerName = name;
    }

    String getPlayerName() {
        return playerName;
    }

    Random rng = new Random();
    final int foodAvailable = 5;
    final int maxFOODQnty = 44;
    final int minFOOD = 8, maxFOOD = 24;
    int gameScore = 0;
    int incrementChance = 3;

    String foodList[] = {
            "CROQUETTE", "BAGEL", "STEAK", "SPAGHETTI", "LASAGNA", "PIZZA", "SHAWARMA", "SAMOSA", "CORNDOG", "HOTDOG",
            "APPLE PIE", "CHOCO BAR", "FALAFEL",
            "BEEF CURRY", "PARATHA", "HUMMUS", "BOILED EGG", "MACCARONI & CHEESE", "TACO", "GRILLED SALMON",
            "CHIMICHANGA", "ALMOND", "WAFFLE", "QUESADILLA", "DEEP DISH PIE",
            "COCONUT MUFFIN", "BROWNIE", "CHEESECAKE", "BEIGHNET", "COOKIE", "BEEF WELLINGTON", "ONION RING",
            "FISH FINGER", "BREADSTICK", "WEDGE", "BURGER", "FRIES", "CRISPY CHICKEN"
    };

    String selectedFoods[] = new String[foodAvailable];
    int foodQuantitiesp[] = new int[foodAvailable];
    JButton foodButtons[] = new JButton[foodAvailable];
    boolean isStockOverflowing[] = new boolean[foodAvailable];
    boolean isfinishStock[] = new boolean[foodAvailable];

    boolean isFoodAmtHitALimit(int foodIndex) {
        return (isStockOverflowing[foodIndex] || isfinishStock[foodIndex]);
    }

    int hasWonGame() {
        // kriteria ingame = semua makanan harus habis, jika terdapat satu makanan yang
        // kelebihan produk, anda kalah game
        // kalah = -1 (salah satu stock makanan sudah melewati batasnya), netral = 0 ,
        // menang game = 1 (semua stock makanan sudah habis)
        int isWin = 0;
        int countFinishedFoods = 0;
        for (int i = 0; i < foodAvailable; i++) {
            if (isfinishStock[i]) {
                countFinishedFoods += 1;
            } else if (isStockOverflowing[i]) {
                return -1;
            }
        }
        if (countFinishedFoods == foodAvailable)
            isWin = 1;
        return isWin;
    }

    void refreshWindow(JFrame window) {
        window.setVisible(false);
        new PauseThread(400);
        window.setVisible(true);
    }

    void updateQuantity(int index, int increment) {
        if (!isInGaming)
            return;
        JButton foodButton = foodButtons[index];
        if (!isFoodAmtHitALimit(index)) {
            foodQuantitiesp[index] += increment;
            gameScore += 1;
            if (Math.signum(increment) == -1) { // check angka pengubah adalah negatif
                Executors.newSingleThreadExecutor().submit(() -> {
                    // buat animasi memakan dengan cara membuat thread yang baru biar gak menggangu
                    // proses program utama, sumber dari forum
                    // https://stackoverflow.com/questions/17758411/java-creating-a-new-thread
                    updateImage(imageHandler, "Images\\emojiOpenMouth.png");
                    new PauseThread(300);
                    myAudioPlr.playAudio("SoundsFX\\crunchEat.wav", 0);
                    updateImage(imageHandler, "Images\\emojiMunching.png");
                    new PauseThread(300);
                    updateImage(imageHandler, imageMainStatus);
                });
            }
        }

        isfinishStock[index] = foodQuantitiesp[index]==0;
        isStockOverflowing[index] = foodQuantitiesp[index]>maxFOODQnty;

        // if (foodQuantitiesp[index] < 1) {
        //     isfinishStock[index] = true;
        // } else if (foodQuantitiesp[index] > maxFOODQnty) {
        //     isStockOverflowing[index] = true;
        // }

        foodButton.setText(selectedFoods[index] + " [" + foodQuantitiesp[index] + "]");
        if (isfinishStock[index]) {
            foodButton.setForeground(new Color(10, 180, 20));
            foodButton.setBackground(new Color(16, 16, 16));
        } else if (isStockOverflowing[index]) {
            foodButton.setForeground(new Color(178, 20, 10));
            foodButton.setBackground(new Color(16, 16, 16));
        } else {
            foodButton.setForeground(new Color(16, 16, 16));
            foodButton.setBackground(new Color(220, 220, 220));
        }
    }

    void addKeyBinding(JComponent c, String key, final Action action) {
        c.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(key), key);
        c.getActionMap().put(key, action);
        c.setFocusable(true);
    }

    JFrame newFrame;
    JPanel imageScene;
    JLabel imageHandler;
    String imageMainStatus;
    JPanel hotbar;
    JTextField hotbarTIP;
    JPanel banner;

    Clip introSong = myAudioPlr.getBulkAudio("OSTBGM\\intro.wav");
    Clip gameSessionSong = myAudioPlr.getBulkAudio("OSTBGM\\whenGame.wav");

    void forceShutAudios() {
        introSong.stop();
        gameSessionSong.stop();
    }

    void updateImage(JLabel f, String location) { // ubah gambar dan menyesuaikan posisi ke tengah sesuai resolusi gambar
        Toolkit it = Toolkit.getDefaultToolkit();
        Dimension d = it.getScreenSize();
        ImageIcon emotionD = new ImageIcon(location);
        final int IMGWidth = emotionD.getIconWidth(), IMGHeight = emotionD.getIconHeight();
        int posX = (d.width / 2 - IMGWidth / 2), posY = (d.height / 2 - IMGHeight);
        f.setBounds(posX, posY, IMGWidth, IMGHeight);
        f.setBackground(Color.yellow);
        f.setIcon(emotionD);
    }
    JButton resetGameBTN;
    void initializeGame() {
        hotbar = new JPanel();
        hotbar.setLayout(new FlowLayout());
        hotbar.setBackground(new Color(205, 127, 50));

        // inisialisasi beberapa array
        for (int i = 0; i < foodAvailable; i++) {
            isStockOverflowing[i] = false;
            isfinishStock[i] = false;
            foodQuantitiesp[i] = 0;
            selectedFoods[i] = foodList[i];
            foodButtons[i] = new JButton();// inisialsasi tombol untuk interaksi

            JButton foodButton = foodButtons[i];
            foodButton.setFont(textSetts.defaultButtonsFont());
            final int innerI = i;
            foodButton.addActionListener(new ActionListener() {
                public void actionPerformed(ActionEvent e) {
                    updateQuantity(innerI, -1);
                    myAudioPlr.playAudio("SoundsFX\\tickFast.wav", 0);
                }
            });
            foodButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent evt) {
                    if (isFoodAmtHitALimit(innerI) || !isInGaming)
                        return;
                    updateImage(imageHandler, "Images\\emojiOpenMouth.png");
                }

                public void mouseExited(MouseEvent evt) {
                    updateImage(imageHandler, imageMainStatus);
                }
            });
            hotbar.add(foodButton);
        }
        // tampilin status muka Emoji
        imageScene = new JPanel();
        imageScene.setLayout(null);
        imageScene.setBackground(Color.black);
        newFrame.add(imageScene, BorderLayout.CENTER);
        newFrame.setMinimumSize(new Dimension(1280, 720));
        imageHandler = new JLabel();
        imageScene.add(imageHandler);

        banner = new JPanel();
        banner.setPreferredSize(new Dimension(1, 80));
        banner.setBackground(new Color(8, 96, 170));
        banner.setLayout(new FlowLayout());
        
        resetGameBTN = new JButton();
        resetGameBTN.setText("RETRY GAME");
        resetGameBTN.setFont(textSetts.defaultButtonsFont());
        resetGameBTN.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt){
                if(isInGaming) return;
                runGamingSession();
            }
        });

        banner.add(resetGameBTN);
        newFrame.add(banner, BorderLayout.NORTH);
        refreshWindow(newFrame);
        // buat fullscreen
        newFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        newFrame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent windowEvent) {
                isSessionRunning = false;
                endGamingSession();
            }
        });
        runGamingSession();
    }

    void runGamingSession() {
        isInGaming = true;
        introSong.stop();
        resetGameBTN.setEnabled(false);
        List<String> strList = Arrays.asList(foodList.clone()); // buat copyan table list makanan, acak mereka biar yang terseleksi pertama berupa urutan beracak, sumber https://www.geeksforgeeks.org/how-to-shuffle-the-elements-of-array-in-java/
        Collections.shuffle(strList);
        String copyFoods[] = strList.toArray(new String[strList.size()]);

        for (int i = 0; i < foodAvailable; i++) {
            foodQuantitiesp[i] = rng.nextInt(maxFOOD - minFOOD) + minFOOD;// initialisasi counter makanan
            selectedFoods[i] = copyFoods[i];

            updateQuantity(i, 0); // inisiasi text tombol makanan
        }
        imageMainStatus = "Images\\emojiNeutral.png";
        updateImage(imageHandler, imageMainStatus);
        System.out.println("Now memain game");
        newFrame.remove(hotbarTIP);
        newFrame.add(hotbar, BorderLayout.SOUTH);
        newFrame.setTitle("Game " + getPlayerName().toUpperCase());
        newFrame.setResizable(true);

        gameSessionSong.start();
        gameSessionSong.loop(foodAvailable);
    }

    void endGamingSession() {
        forceShutAudios();
        isInGaming = false;
        countGameSession = 0;
        resetGameBTN.setEnabled(true);
    }

    void incrementFoodStats() {
        for (int i = 0; i < selectedFoods.length; i++) {
            int x = rng.nextInt(incrementChance);
            if (x != 0)
                continue;
            updateQuantity(i, +1);
        }
    }

    public void setGUI(String windowName) {
        newFrame = new JFrame(windowName);
        newFrame.setMinimumSize(new Dimension(800, 200));
        newFrame.setResizable(false);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Container gameWindow = newFrame.getContentPane();
        gameWindow.setLayout(new BorderLayout());

        hotbarTIP = new JTextField();
        hotbarTIP.setFont(textSetts.defaultFont());
        hotbarTIP.setToolTipText("Masukkan nama untuk melanjutkan game.");
        hotbarTIP.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                setname(hotbarTIP.getText());
                if (getPlayerName().length() < 1)
                    return;
                initializeGame();
            }
        });

        newFrame.add(hotbarTIP, BorderLayout.SOUTH);

        newFrame.setLocationRelativeTo(null); // memaksakan window berposisi tepat di tengah layar saat inisiasi program
        newFrame.setVisible(true);
        introSong.start();
        addKeyBinding(newFrame.getRootPane(), "F11", new FullscreenHandler(newFrame));
        isSessionRunning = true;
        while (isSessionRunning) {
            new PauseThread(1000);
            if (isInGaming) {
                int getStatus = hasWonGame();
                if (getStatus == -1) {
                    System.out.println("LOSE");
                    imageMainStatus = "Images\\emojiPanic.png";
                    updateImage(imageHandler, imageMainStatus);
                    endGamingSession();
                } else if (getStatus == 1) {
                    System.out.println("WIN");
                    imageMainStatus = "Images\\emojiWinner.png";
                    updateImage(imageHandler, imageMainStatus);
                    endGamingSession();
                }
                
                incrementFoodStats();
                countGameSession += 1;
            }
            countTotalSession += 1;
        }
    }

    public GameMain() {
        setGUI("Gaming Moment");
    }
}
