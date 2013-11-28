package game;

import gfx.Colors;
import gfx.Screen;
import gfx.SpriteSheet;

import java.awt.BorderLayout;
import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Game extends Canvas implements Runnable{

	private static final long serialVersionUID = 1L;

	public static final int WIDTH = 160;
	public static final int HEIGHT = WIDTH/ 12*9;
	public static final int SCALE = 5;
	public static final String NAME = "Game";
	public static final Dimension DIMENSIONS = new Dimension(WIDTH * SCALE, HEIGHT * SCALE);
	public static Game game;

	private JFrame frame;

	private Thread thread;

	public boolean running = false;
	public int tickCount = 0;

	private BufferedImage image = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_INT_RGB);
	private int[] pixels = ((DataBufferInt) image.getRaster().getDataBuffer()).getData();
	private int[] colors = new int[6*6*6];

	private Screen screen;
	public InputHandler input;
	//	public WindowHandler windowHandler;
	//	public Level leve;
	//	public Player player;
	//	
	//	public GameClient socketClient;
	//	public GameServer socketServer;

	public boolean debug = true;
	public boolean isApplet = false;

	public Game(){
		setMinimumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		setMaximumSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));
		setPreferredSize(new Dimension(WIDTH*SCALE, HEIGHT*SCALE));

		frame = new JFrame(NAME);

		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLayout(new BorderLayout());

		frame.add(this, BorderLayout.CENTER);
		frame.pack();

		frame.setResizable(false);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
	}

	public void init(){
		int index = 0;
		for(int r = 0; r < 6; r++){
			for(int g = 0; g < 6; g++){
				for(int b = 0; b < 6; b++){
					int rr = (r * 255 / 5);
					int gg = (r * 255 / 5);
					int bb = (r * 255 / 5);

					colors[index ++] = rr << 16 | gg << 8 | bb; //Lecture 05 - 5:00
				}
			}
		}

		screen = new Screen(WIDTH,HEIGHT, new SpriteSheet("/sprite_sheet.png"));
		input = new InputHandler(this);
		//		level = new Level("/levels/water_test_level.png");
		//		player = new PlayerMP(level, 100, 100, input, JOptionPane.showInputDialog(this, "Please enter a username"), null, -1);
		//		level.addEntity(player);
		//		if(!isApplet){
		//			Packet))Login loginPacket = new
		//		}
	}

	public synchronized void start(){
		running = true;
		thread = new Thread(this, NAME + "_main");
		thread.start();
		if(!isApplet){

		}
	}

	public synchronized void stop(){
		running = false;

		try{
			thread.join();
		}catch(InterruptedException e){
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		long lastTime = System.nanoTime();
		double nsPerTick = 1000000000D /60D;

		int frames = 0;
		int ticks = 0;
		long lastTimer = System.currentTimeMillis();
		double delta = 0;

		init();

		while(running){
			long now = System.nanoTime();
			delta += (now - lastTime)/ nsPerTick;
			lastTime = now;
			boolean shouldRender = true;

			while(delta >= 1){
				ticks++;
				tick();
				delta -= 1;
				shouldRender = true;
			}

			try {
				Thread.sleep(2);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			if(shouldRender){
				frames++;
				render();	//render	
			}

			if(System.currentTimeMillis() - lastTimer >= 1000){
				lastTimer += 1000;
				frames = 0;
				ticks = 0;
			}
		}
	}

	public void tick(){
		tickCount++;

		if(input.up.isPressed()) screen.yOffset--;
		if(input.down.isPressed()) screen.yOffset++;
		if(input.left.isPressed()) screen.xOffset--;
		if(input.right.isPressed()) screen.xOffset++;
	}

	public void render(){
		BufferStrategy bs = getBufferStrategy();
		if(bs == null){
			createBufferStrategy(3);//triple buffering
			return;
		}
//
//		for(int y = 0 ; y < 32 ; y++){
//			for(int x = 0 ; x< 32 ; x++){
//				screen.render(x << 3, y << 3, 0, Colors.get(555, 500, 050, 005));
//			}
//		}

		for(int y = 0 ; y < screen.height ; y++){
			for(int x = 0 ; x< screen.width ; x++){
				int colorCode = screen.pixels[x+y * screen.width];
				if( colorCode < 255) pixels[x+y * WIDTH] = colors[colorCode];
			}
		}

		Graphics g = bs.getDrawGraphics();
		g.drawImage(image, 0, 0, getWidth(), getHeight(), null);
		g.dispose();
		bs.show();
	}

	public static void main(String[] args){
		new Game().start()	;
	}
}
