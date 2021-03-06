package dungeontrivia;

import java.awt.Button;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferStrategy;
import java.util.ArrayList;

/**
 * Dungeon Trivia MAIN GAME CLASS- ESTRUCTURA COMPLETA DEL VIDEOJUEGO
 *
 * @author Luis, Adrian, Antonio and Rodrigo
 */
public class Game implements Runnable {

    private BufferStrategy bs; // BufferStrategy var
    private Graphics g; // for the graphics
    private Display display; // for the display of the game
    String title; // the title of the game
    public static int width; // the width of the game
    public static int height; //the height of the game
    private Thread thread; //the thread of the game
    private boolean running; //boolean saying if it is running

    private KeyManager keyManager; //key manager
    private MouseInput mouseManager;

    public static ArrayList<Pregunta> preguntas = new ArrayList<Pregunta>();
    public static int numeroPreguntas = 0;
    private int firstRandomIndex;
    private int secondRandomIndex;
    private int thirdRandomIndex;

    private String timer = "0:00";
    public static int timerStart = 10;
    private int counter = 0;
    private int counter2 = 0;
    private boolean timerOff = false;
    //Players
    public static ArrayList<Player> players = new ArrayList<Player>();

    public static int numPlayers = 4;
    //EndPlayers
    private String answer;
    private String posZero;
    private String posOne;
    private String posTwo;
    private String resultado = "";
    private boolean finalDePregunta;
    private int counter3 = 0;

    private int direction;
    private Rectangle rectanguloUno;
    private Rectangle rectanguloDos;
    private Rectangle rectanguloTres;
    private Rectangle rectangulo;

    private Player player;
    private boolean check;
    private boolean faseMovimiento;
    private boolean fasePregunta = true;
    private int speed = 7;

    //menu helper
    boolean gameStarted = false;
    private MainMenuPanel menu;
    private InstructionsPanel controls;
    private LevelSelect levelSelect;
    public static EndGame endGamelvl;
    private HighscoresPanel highscoresPanel;
    PlayerSelectPanel playerSelect;
    private PausePanel pausePanel;
    private boolean puertaZero;
    private boolean puertaOne;
    private boolean puertaTwo;
    private boolean endgame;
    private int counter4 = 0;

    public static boolean paused; // paused boolean
    public static HighScoreDialog highScoreDialog = new HighScoreDialog(false);
    public static boolean isAvailableForHighscore;

    //Maneja los estados del juego
    public static enum STATE {
        MENU,
        GAME,
        ENDGAME,
        HIGHSCORES,
        CONTROLS,
        LEVELS,
        PLAYERSELECT,
        PAUSED,
        EXIT
    };
    public static STATE state = STATE.MENU;

    /**
     * Game Constructor
     *
     * @param title
     * @param width
     * @param height
     */
    public Game(String title, int width, int height) {
        this.title = title;
        this.width = width;
        this.height = height;
        running = false;
        keyManager = new KeyManager();
        mouseManager = new MouseInput();
    }

    /**
     * getHeight method
     *
     * @return height
     */
    public int getHeight() {
        return height;
    }

    /**
     * getPlayers method
     *
     * @return players
     */
    public ArrayList<Player> getPlayers() {
        return players;
    }

    /**
     * setNumPlayers method
     *
     * @param numPlayers
     */
    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    /**
     * getWidth method
     *
     * @return width
     */
    public int getWidth() {
        return width;
    }

    /**
     * isFasePregunta method
     *
     * @return fasePregunta
     */
    public boolean isFasePregunta() {
        return fasePregunta;
    }

    /**
     * inits the game with the display and player
     */
    public void init() {
        highScoreDialog = new HighScoreDialog(false);
        menu = new MainMenuPanel();
        controls = new InstructionsPanel();
        playerSelect = new PlayerSelectPanel();
        levelSelect = new LevelSelect();
        pausePanel = new PausePanel();
        highscoresPanel = new HighscoresPanel();
        display = new Display(title, getWidth(), getHeight());
        display.getCanvas().addMouseListener(mouseManager);
        Assets.init();

        Assets.sound.setLooping(true);
        Assets.sound.play();
        finalDePregunta = false;
        faseMovimiento = false;
        firstRandomIndex = (int) (Math.random() * 3);
        secondRandomIndex = (int) (Math.random() * 2);
        if (firstRandomIndex == 0 && secondRandomIndex == 0) {
            secondRandomIndex = 1;
        } else if (firstRandomIndex == 1 && secondRandomIndex == 1) {
            secondRandomIndex = 2;
        }
        thirdRandomIndex = 3 - (firstRandomIndex + secondRandomIndex);

        rectanguloUno = new Rectangle(200, 620, 10, 10);
        rectanguloDos = new Rectangle(500, 620, 10, 10);
        rectanguloTres = new Rectangle(900, 620, 10, 10);

        display.getJframe().addKeyListener(keyManager);

    }

    /**
     * setIsAvailableForHighscore method
     *
     * @param isAvailableForHighscore
     */
    public static void setIsAvailableForHighscore(boolean isAvailableForHighscore) {
        Game.isAvailableForHighscore = isAvailableForHighscore;
    }

    /**
     * isIsAvailableForHighscore method
     *
     * @return isIsAvailableForHighscore
     */
    public static boolean isIsAvailableForHighscore() {
        return isAvailableForHighscore;
    }

    /**
     * run method
     */
    @Override
    public void run() {
        init();
        int fps = 50;
        double timeTick = 1000000000 / fps;
        double delta = 0;
        long now;
        long lastTime = System.nanoTime();
        while (running) {
            now = System.nanoTime();
            delta += (now - lastTime) / timeTick;
            lastTime = now;

            if (delta >= 1) {
                tick();
                render();
                delta--;
            }
        }
        stop();
    }

    /**
     * getKeyManager method
     *
     * @return keyManager
     */
    public KeyManager getKeyManager() {
        return keyManager;
    }

    /**
     * Metodo que actualiza el tiempo a desplegar en la pantalla
     *
     * @param time
     */
    private void updateTimer(int time) {

        if (time < 10) {
            timer = "0:0" + time;
        } else {
            timer = "0:" + time;
        }
    }

    /**
     * tick method
     */
    private void tick() {
        //tick
        //Si esta corriendo el juego
        if (state == STATE.GAME) {
            keyManager.tick();
            if (getKeyManager().pause) {
                getKeyManager().setKeyDown();
                paused = !paused;
            }
            //Si no esta en pausa
            if (!paused) {

                //se obtiene la respuesta a la pregunta
                answer = preguntas.get(counter3).getRespuestas().get(0);
                //Se obtienen las respuestas de las puertas en variables 
                //posZero, posOne, y posTwo
                if (firstRandomIndex == 0 && secondRandomIndex == 1) {
                    posZero = preguntas.get(counter3).getRespuestas().get(0);
                    posOne = preguntas.get(counter3).getRespuestas().get(1);
                    posTwo = preguntas.get(counter3).getRespuestas().get(2);
                } else if (firstRandomIndex == 0 && secondRandomIndex == 2) {
                    posZero = preguntas.get(counter3).getRespuestas().get(0);
                    posOne = preguntas.get(counter3).getRespuestas().get(2);
                    posTwo = preguntas.get(counter3).getRespuestas().get(1);
                } else if (firstRandomIndex == 1 && secondRandomIndex == 0) {
                    posZero = preguntas.get(counter3).getRespuestas().get(1);
                    posOne = preguntas.get(counter3).getRespuestas().get(0);
                    posTwo = preguntas.get(counter3).getRespuestas().get(2);
                } else if (firstRandomIndex == 1 && secondRandomIndex == 2) {
                    posZero = preguntas.get(counter3).getRespuestas().get(1);
                    posOne = preguntas.get(counter3).getRespuestas().get(2);
                    posTwo = preguntas.get(counter3).getRespuestas().get(0);
                } else if (firstRandomIndex == 2 && secondRandomIndex == 0) {
                    posZero = preguntas.get(counter3).getRespuestas().get(2);
                    posOne = preguntas.get(counter3).getRespuestas().get(0);
                    posTwo = preguntas.get(counter3).getRespuestas().get(1);
                } else {
                    posZero = preguntas.get(counter3).getRespuestas().get(2);
                    posOne = preguntas.get(counter3).getRespuestas().get(1);
                    posTwo = preguntas.get(counter3).getRespuestas().get(0);
                }

                //se tickea los players, y si uno muere, se establece como muerto
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getLives() > 0 || players.get(i).isSec()) {
                        players.get(i).tick();
                    }
                    if (players.get(i).getLives() == 0) {
                        players.get(i).setDead(true);
                        players.get(i).setMoving(false);
                        players.get(i).setIdle(false);
                    }
                }

                //Contador para la animacion de muerte de un player
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).isSec()) {

                        if (players.get(i).getCounter4() < 40) {
                            players.get(i).setCounter4(players.get(i).getCounter4() + 1);
                        } else {
                            players.get(i).setSec(false);

                        }
                    }
                }
                //logica para el reloj de la esquina superior derecha del juego
                if (counter < 50) {
                    counter++;
                } else {
                    if (timerStart != 0) {
                        timerStart--;
                        updateTimer(timerStart);
                    } else {

                        //Primera fase del juego, donde se hace la pregunta y corre el tiempo.
                        if (fasePregunta) {

                            for (int i = 0; i < numPlayers; i++) {

                                //por cada jugador, se obtiene su movimiento
                                switch (players.get(i).getMove()) {
                                    case 'l':

                                        rectangulo = getRectangulo('l');
                                        if (rectangulo.getX() - players.get(i).getX() > 0) {
                                            players.get(i).setDirection(1);
                                        } else {
                                            players.get(i).setDirection(-1);
                                        }
                                        if (!posZero.equals(answer)) {
                                            players.get(i).decreasePlayerLive();
                                            if (players.get(i).getLives() == 0) {
                                                players.get(i).setSec(true);
                                                players.get(i).setDead(true);
                                                players.get(i).setMoving(false);
                                                players.get(i).setIdle(false);
                                                Assets.deathSound.play();
                                            }
                                        } else {
                                            //actualizar el score
                                            if (players.get(i).getLives() > 0) {
                                                int score = players.get(i).getScore() + 10;
                                                players.get(i).setScore(score);
                                            }
                                        }
                                        break;
                                    case 'u':
                                        rectangulo = getRectangulo('u');
                                        if (rectangulo.getX() - players.get(i).getX() > 0) {
                                            players.get(i).setDirection(1);
                                        } else {
                                            players.get(i).setDirection(-1);
                                        }
                                        if (!posOne.equals(answer)) {
                                            players.get(i).decreasePlayerLive();
                                            if (players.get(i).getLives() == 0) {
                                                players.get(i).setSec(true);
                                                players.get(i).setDead(true);
                                                players.get(i).setMoving(false);
                                                players.get(i).setIdle(false);
                                                Assets.deathSound.play();
                                            }
                                        } else {
                                            //actualizar el score
                                            if (players.get(i).getLives() > 0) {
                                                int score = players.get(i).getScore() + 10;
                                                players.get(i).setScore(score);
                                            }
                                        }
                                        break;
                                    case 'r':
                                        rectangulo = getRectangulo('r');
                                        if (rectangulo.getX() - players.get(i).getX() > 0) {
                                            players.get(i).setDirection(1);
                                        } else {
                                            players.get(i).setDirection(-1);
                                        }
                                        if (!posTwo.equals(answer)) {
                                            players.get(i).decreasePlayerLive();
                                            if (players.get(i).getLives() == 0) {
                                                players.get(i).setSec(true);
                                                players.get(i).setDead(true);
                                                players.get(i).setMoving(false);
                                                players.get(i).setIdle(false);
                                                Assets.deathSound.play();
                                            }
                                        } else {
                                            //actualizar el score
                                            if (players.get(i).getLives() > 0) {
                                                int score = players.get(i).getScore() + 10;
                                                players.get(i).setScore(score);
                                            }
                                        }
                                        break;
                                    default:
                                        players.get(i).decreasePlayerLive();
                                        if (players.get(i).getLives() == 0) {
                                            players.get(i).setSec(true);
                                            players.get(i).setDead(true);
                                            players.get(i).setMoving(false);
                                            players.get(i).setIdle(false);
                                            Assets.deathSound.play();
                                        }
                                        players.get(i).setAnswer(true);
                                        break;
                                }

                            }

                            fasePregunta = false;
                            faseMovimiento = true;
                        }

                    }
                    counter = 0;

                }
                //fase 2: cuando se mueven los personajes
                if (faseMovimiento) {

                    if (posZero.equals(answer)) {
                        puertaZero = true;
                    } else {
                        puertaZero = false;
                    }

                    if (posOne.equals(answer)) {
                        puertaOne = true;
                    } else {
                        puertaOne = false;
                    }

                    if (posTwo.equals(answer)) {
                        puertaTwo = true;
                    } else {
                        puertaTwo = false;
                    }

                    check = true;
                    for (int i = 0; i < players.size(); i++) {

                        //deshabilitar teclado
                        players.get(i).setEnabled(false);

                        if (!getRectangulo(players.get(i).getMove()).intersects(players.get(i).getRect()) && !players.get(i).isAnswer()) {
                            players.get(i).setMoving(true);
                            players.get(i).setIdle(false);
                            players.get(i).setX(players.get(i).getX() + players.get(i).getDirection() * speed);
                        } else {
                            players.get(i).setMoving(false);
                            players.get(i).setIdle(true);
                        }

                        if (!players.get(i).isAnswer()) {
                            check &= getRectangulo(players.get(i).getMove()).intersects(players.get(i).getRect());
                        }

                    }
                    //si todos los jugadores ya se movieron, cambia a fase 3.
                    if (check) {
                        Assets.openDoor.setLooping(false);
                        Assets.openDoor.play();
                        faseMovimiento = false;
                        finalDePregunta = true;
                        for (int i = 0; i < players.size(); i++) {

                            if (!getRectangulo(players.get(i).getMove()).intersects(players.get(i).getRect()) && !players.get(i).isAnswer()) {
                                players.get(i).setMoving(true);
                                players.get(i).setIdle(false);
                                players.get(i).setX(players.get(i).getX() + players.get(i).getDirection() * speed);
                            } else {
                                players.get(i).setMoving(false);
                                players.get(i).setIdle(true);
                            }

                        }
                    }

                }
                //fase 3: final de pregunta donde se abren las puertas y 
                //se obtiene la siguiente pregunta
                if (finalDePregunta) {

                    if (counter2 < 250) {
                        counter2++;
                    } else {

                        timerStart = 10;
                        updateTimer(timerStart);
                        firstRandomIndex = (int) (Math.random() * 3);
                        secondRandomIndex = (int) (Math.random() * 2);
                        if (firstRandomIndex == 0 && secondRandomIndex == 0) {
                            secondRandomIndex = 1;
                        } else if (firstRandomIndex == 1 && secondRandomIndex == 1) {
                            secondRandomIndex = 2;
                        }
                        thirdRandomIndex = 3 - (firstRandomIndex + secondRandomIndex);
                        //obtener siguiente pregunta
                        if (counter3 < numeroPreguntas - 1) {
                            counter3++;
                        } else {
                            //fin de preguntas
                            counter3 = 0;
                        }
                        counter2 = 0;
                        for (int i = 0; i < players.size(); i++) {
                            players.get(i).setEnabled(true);
                            players.get(i).setAnswer(false);
                            players.get(i).setMove('n');
                            players.get(i).setX(200 + 200 * i);
                        }

                        finalDePregunta = false;
                        fasePregunta = true;
                        Assets.closeDoor.play();

                        endgame = false;
                        for (int i = 0; i < players.size(); i++) {
                            endgame |= (players.get(i).getLives() > 0);
                        }

                        if (!endgame) {
                            endGamelvl.setGameDone(true);
                            state = Game.STATE.ENDGAME;
                        }
                    }
                }
            }
        } else if (state == STATE.EXIT) {
            System.exit(0);
        }
    }

    /**
     * Metodo auxiliar que regresa un rectangulo que corresponde a cada puerta
     *
     * @param c
     * @return rectanguloUno o rectanguloDos o rectanguloTres
     */
    public Rectangle getRectangulo(char c) {
        if (c == 'l') {
            return rectanguloUno;
        } else if (c == 'u') {
            return rectanguloDos;
        } else {
            return rectanguloTres;
        }
    }

    /**
     * render method 
     */
    private void render() {
        bs = display.getCanvas().getBufferStrategy();
        if (bs == null) {
            display.getCanvas().createBufferStrategy(3);
        } else {
            g = bs.getDrawGraphics();
            //Cargar todo lo del juego 
            if (state == STATE.GAME) {

                g.drawImage(Assets.bg, 0, 0, width, height, null);
                Font myFont = new Font("Courier New", 1, 22);
                g.setFont(myFont);
                g.setColor(Color.WHITE);
                g.drawString(timer, 950, 100);
                g.drawImage(Assets.reloj, 1010, 75, 20, 30, null);

                if (finalDePregunta) {

                    if (puertaZero) {
                        g.drawImage(Assets.puertaBien, 40, 445, 200, 280, null);
                    } else {
                        g.drawImage(Assets.puertaMal, 40, 445, 200, 280, null);
                    }
                    if (puertaOne) {
                        g.drawImage(Assets.puertaBien, 440, 445, 200, 280, null);
                    } else {
                        g.drawImage(Assets.puertaMal, 440, 445, 200, 280, null);
                    }
                    if (puertaTwo) {
                        g.drawImage(Assets.puertaBien, 840, 445, 200, 280, null);
                    } else {
                        g.drawImage(Assets.puertaMal, 840, 445, 200, 280, null);
                    }

                } else {
                    g.drawImage(Assets.puertaCerrada, 40, 445, 200, 280, null);
                    g.drawImage(Assets.puertaCerrada, 440, 445, 200, 280, null);
                    g.drawImage(Assets.puertaCerrada, 840, 445, 200, 280, null);
                }

                //renderear los players
                for (int i = 0; i < players.size(); i++) {
                    if (players.get(i).getLives() > 0 || players.get(i).isSec()) {
                        players.get(i).render(g);
                    }

                    for (int j = 0; j < players.get(i).getLives(); j++) {
                        Heart heart = players.get(i).getHearts().get(j);
                        heart.render(g);
                    }
                }

                myFont = new Font("Courier New", 1, 14);
                g.setFont(myFont);
                g.setColor(Color.WHITE);
                //Dibujar el score
                g.drawString("Scores", 10, 10);
                for (int i = 0; i < players.size(); i++) {
                    g.drawString("Player " + (i + 1) + ":" + Integer.toString(players.get(i).getScore()), 10, (i * 5) * 3 + 25);
                }

                myFont = new Font("Courier New", 1, 14);
                g.setFont(myFont);
                g.setColor(Color.BLACK);

                g.drawString(preguntas.get(counter3).getPregunta(), getWidth() / 2 - 250, 100);
                g.drawString(posZero, getWidth() / 2 - 455, 350);
                g.drawString(posOne, getWidth() / 2 - 60, 350);
                g.drawString(posTwo, getWidth() / 2 + 320, 350);

                if (finalDePregunta) {
                    if (resultado == "Correcto") {
                        g.setColor(Color.green);
                    } else {
                        g.setColor(Color.red);
                    }
                    g.drawString(resultado, 200, 200);
                }
                if (paused) {
                    pausePanel.render(g, getWidth(), getHeight());

                }
            } else if (state == state.CONTROLS) {
                g.drawImage(Assets.controls, 0, 0, width, height, null);
                controls.render(g, getWidth(), getHeight());
            } else if (state == state.HIGHSCORES) {
                g.drawImage(Assets.bg_hs, 0, 0, width, height, null);
                highscoresPanel.render(g, getWidth(), getHeight());
                //Player select
            } else if (state == state.LEVELS) {
                g.drawImage(Assets.level_select, 0, 0, width, height, null);
                levelSelect.render(g, getWidth(), getHeight());
            } else if (state == state.ENDGAME) {
                // highScoreDialog.setAvailable(true);
                g.drawImage(Assets.bg1, 0, 0, width, height, null);
                endGamelvl.render(g, getWidth(), getHeight());
                //Player select
            } else if (state == state.PLAYERSELECT) {
                g.drawImage(Assets.bg1, 0, 0, width, height, null);
                playerSelect.render(g, getWidth(), getHeight());
            } else {
                g.drawImage(Assets.menu, 0, 0, width, height, null);
                menu.render(g, getWidth(), getHeight());
            }

            bs.show();
            g.dispose();
        }
    }

    /**
     * start method
     */
    public synchronized void start() {
        if (!running) {
            running = true;
            thread = new Thread(this);
            thread.start();
        }
    }

    /**
     * stop method
     */
    public synchronized void stop() {
        if (running) {
            running = false;
            try {
                thread.join();
            } catch (InterruptedException ie) {
                ie.printStackTrace();
            }
        }
    }
}
