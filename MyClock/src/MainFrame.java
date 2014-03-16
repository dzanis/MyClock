
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


import com.sun.awt.AWTUtilities;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;




import javax.swing.*;

import java.security.CodeSource;
import java.sql.Date;
import java.util.Calendar;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.net.URL;
import javax.sound.sampled.*;

/**
 *
 * @author zanis  28.05.2013
 * Часы предназначены для выключения компьютера после полуночи до 8-ми утра.
 * Через каждые два часа появится диалог,предупреждающий об выключении.
 * Если отменить выключение то выключение отсрочится на тех же два часа.
 * Часы можно перемешать по рабочему столу зажав ЛКМ.
 * Так же доступны другие функции :
 * Для этого нужно навести мышь на часы и нажать ПКМ
 * Seconds Show - показывать секундную стрелку (от этого зависит какая задержка sleep)
 * Exit - закрыть часы
 * Shutdown - запустить таймер выключения
 * 
 * Положение часов сохраняется в файл MyClock.properties в user.home (например C:\Users\zanis\MyClock.properties)
 * 

 */

public class MainFrame extends JFrame implements ActionListener { //Наследуя от JFrame мы получаем всю функциональность окна
    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @param args the command line arguments
     */
    public static void main(String[] args) { //эта функция может быть и в другом классе

    	try
        {
            // Устанавливаем нативный стиль компонентов
            UIManager.setLookAndFeel ( UIManager.getSystemLookAndFeelClassName () );
        }
        catch ( Throwable e )
        {
            //
        }
    	
        new MainFrame(); //Создаем экземпляр нашего приложения

    }
    
    
    public static int WindowsXPos;
    public static int WindowsYPos;  
    public static boolean SecondsShow;  
    public static JPopupMenu popupMenu ; 
    
    
    // картинки часов
	final ImageIcon trad = new ImageIcon(getClass().getResource("/trad.png"));
	final ImageIcon trad_h = new ImageIcon(getClass().getResource("/trad_h.png"));
	final ImageIcon trad_m = new ImageIcon(getClass().getResource("/trad_m.png"));
	final ImageIcon trad_s = new ImageIcon(getClass().getResource("/trad_s.png"));
	final ImageIcon trad_dot = new ImageIcon(getClass().getResource("/trad_dot.png"));
	
	// картинки анимаций
	// FIXME gif анимация глючит
	final ImageIcon crazy = new ImageIcon(getClass().getResource("/crazy.gif"));
	final ImageIcon bye = new ImageIcon(getClass().getResource("/bye.gif"));
	final ImageIcon shok = new ImageIcon(getClass().getResource("/shok.gif"));
	
 
    final int DIALOG_START_TIME = 1000 * 60 * 60 * 2 ; // два часа 
    final int BEFORE_SHUTDOWN_TIME = 1000 * 60  * 5 ; //  5 минут 
    
    
    Timer tDialog = new Timer(DIALOG_START_TIME, this);// таймер для диалога об отмене выключения
    Timer tShutdown = new Timer(BEFORE_SHUTDOWN_TIME, this) ; // таймер  выключения
   
    boolean  running = true;
    int hours ,minutes , seconds ;
 
    
    public MainFrame() {

    	
         super("My Clock"); //Заголовок окна
                 
         Settings.load();// загружаю предыдушее положение окна  
 
         //getContentPane().add(clock);
        
         initPopupMenu();
         
      // TODO: добавить в автозагрузку

 
        	//setType(Type.UTILITY); // уберём иконку из панели задач
        	
            setUndecorated(true);//отключим стандартные декорации ОС
            setSize(trad.getIconWidth(), trad.getIconHeight()); // установка размера окна по размеру часов
            
            
            setLocation(WindowsXPos, WindowsYPos);
            AWTUtilities.setWindowOpaque(this, false);//сделать окно полупрозрачным
            
            add(new DrawClock());// добавляю прорисовку часов
            
            setVisible(true); // делаем видимым
            
            toBack(); // всегда позади всех окон

            resource ();

    }
    
    
   
    
    
    
    
    
    private void initPopupMenu(){
    	
    	popupMenu = new JPopupMenu();
    	
    
        JCheckBoxMenuItem secondsShow = new JCheckBoxMenuItem("Seconds Show");
        secondsShow.setSelected(SecondsShow);
        popupMenu.add(secondsShow);
        secondsShow.addActionListener(this);
    
        popupMenu.addSeparator();
        
        JMenuItem exit; 	
    	popupMenu.add(exit = new JMenuItem("Exit",bye));
    	//exit.setHorizontalTextPosition(JMenuItem.RIGHT);
        exit.addActionListener(this);
        

        JMenuItem shutdown ;
        popupMenu.add(shutdown = new JMenuItem("Shutdown",shok));
       // shutdown.setHorizontalTextPosition(JMenuItem.RIGHT);
        shutdown.addActionListener(this);
  	
    }
    
    
    
    
	@Override
	public void actionPerformed(ActionEvent e) {

		
		
		if (e.getSource() == tDialog) {
			DialogCancelShutdown();
		}

		
		if (e.getSource() == tShutdown) {

			shutdown();

		}

		String action = e.getActionCommand();
		
		if (action == "Seconds Show") {			
			SecondsShow = ((JCheckBoxMenuItem) e.getSource()).isSelected();
			repaint();
			Settings.save();		

		}

		if (action == "Exit") {
			exit();
		}

		if (action == "Shutdown") {
			DialogCancelShutdown();
		}

		
		
		
	
		


	}

	private void exit() {

		System.out.println("exit");	
		running = false;
		dispose();
        System.exit(0);
		 
	}


	private void shutdown() {

		System.out.println("shutdown");
		// -s выключение компьютера
		// -f принудительно завершит работу любого приложения
		// -t 60 отсрочить выключение ПК на 60 секунд
		
		try {
			// чтобы небыло предупреждаюшего окна от ОС,ставлю -t 00
			Runtime.getRuntime().exec("shutdown  -s -f -t 00 ");
		} catch (IOException e) {}
		
		exit();
		 
	}
	
	
	void resource (){
		
		
		System.out.println("resource");
		
		String message = "";
		
		CodeSource src = MainFrame.class.getProtectionDomain().getCodeSource();
		
		URL jar = MainFrame.class.getProtectionDomain().getCodeSource().getLocation();
		
		message += src.toString();
		//if (src != null) {
		 // URL jar = src.getLocation();
		  ZipInputStream stream = null;
		try {
			stream = new ZipInputStream(jar.openStream());
			message += stream.available();
		
		//String outdir = args[1];
		/* Now examine the ZIP file entries to find those you care about. */
		// now iterate through each item in the stream. The get next
        // entry call will return a ZipEntry for each file in the
        // stream
        ZipEntry entry;
        while((entry = stream.getNextEntry())!=null)
        {
            String s = String.format("Entry: %s len %d added %TD",
                            entry.getName(), entry.getSize(),
                            new Date(entry.getTime()));
            System.out.println(s);
            message += s;
            // Once we get the entry from the stream, the stream is
            // positioned read to read the raw data, and we keep
            // reading until read returns 0 or less.
//            String outpath = outdir + "/" + entry.getName();
//            FileOutputStream output = null;
//            try
//            {
//                output = new FileOutputStream(outpath);
//                int len = 0;
//                while ((len = stream.read(buffer)) > 0)
//                {
//                    output.write(buffer, 0, len);
//                }
//            	
//            }
//            finally
//            {
//                // we must always close the output file
//                if(output!=null) output.close();
//            }
        }
        
		} catch (IOException e) {}
		
//		} 
//		else {
//		   /* Fail... */
//			System.out.println("Fail...");
//			message += "Fail...";
//		}
		
		
		JOptionPane.showMessageDialog (null, message, "resource", JOptionPane.INFORMATION_MESSAGE);
	}
	
	
	
	
	public class DrawClock extends JComponent implements Runnable{


		private static final long serialVersionUID = 1L;
		
		
		public DrawClock() {

			 setForeground ( Color.BLACK );
             MoveAdapter moveAdapter = new MoveAdapter();
             addMouseListener(moveAdapter);
             addMouseMotionListener(moveAdapter);
             // запуск потока,в нём будет вызываться прорисовка аналоговых часов
             // FIXME  поток: правильно ли???
                 (new Thread(this)).start();
		}
		
		

   public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHints(rh);

//        date = new Date();  // current time    
//        hours = date.getHours();
//        minutes = date.getMinutes();
//        seconds = date.getSeconds();
        
        Calendar calendar = Calendar.getInstance();
		hours = calendar.get(Calendar.HOUR);
		minutes = calendar.get(Calendar.MINUTE);
		seconds = calendar.get(Calendar.SECOND);
		
        int drawLocationX = (this.getWidth() - trad_h.getIconWidth()) / 2;
        int drawLocationY = 1;
        int rotateLocationX = trad_h.getIconWidth() / 2;
        int rotateLocationY = trad_h.getIconHeight() / 2;
        AffineTransform at = new AffineTransform();

        // часы
        g2d.drawImage(trad.getImage(), 0, 0, null);

        // часовая стрелка
        at.setToTranslation(drawLocationX, drawLocationY); // position x,y
        at.rotate(Math.toRadians(((hours > 12 ? hours - 12 : hours) * 30) + (minutes / 2)), rotateLocationX, rotateLocationY); // rotation, set center rotation              
        g2d.drawImage(trad_h.getImage(), at, null); // Drawing the rotated image at the required drawing locations

        // минутная стрелка
        at.setToTranslation(drawLocationX, drawLocationY); // position x,y
        at.rotate(Math.toRadians((minutes * 6) + (seconds / 10)), rotateLocationX, rotateLocationY); // rotation, set center rotation      
        g2d.drawImage(trad_m.getImage(), at, null); // Drawing the rotated image at the required drawing locations

        // точка по центру
        g2d.drawImage(trad_dot.getImage(), drawLocationX, drawLocationY, null);

    	
        // секундная стрелка
        if(SecondsShow){
        at.setToTranslation(drawLocationX, drawLocationY); // position x,y
        at.rotate(Math.toRadians(seconds * 6), rotateLocationX, rotateLocationY); // rotation, set center rotation              
        g2d.drawImage(trad_s.getImage(), at, null); // Drawing the rotated image at the required drawing locations
        }
        
      //TODO в часы добавить отсчёт времени 
        

        Toolkit.getDefaultToolkit().sync();
        g.dispose();
    }
   
   
	
	public void run() {

		while (running) {

			repaint();
			try {
				//если показывать секундную стрелку то задержка секунду,если непоказывать то минуту
				// FIXME  поток: останавливается если изменить SecondsShow
				//Thread.sleep(SecondsShow ? 1000 : 1000 * 60);
				Thread.sleep(1000 );
			} catch (InterruptedException e) {}

			// если время от 23:00 до 8-ми утра
			if (hours >= 23 && hours < 8) {
				// запуск таймера диалога выключения
				if (!tDialog.isRunning()) {
					System.out.println("запуск таймера диалога выключения");
					tDialog.start();
				}
			}

		}
		// FIXME  поток: правильно ли???
		Thread.currentThread().interrupt();
		

	}

	}


	private void DialogCancelShutdown() {

		tShutdown.start();
		
		EventQueue.invokeLater(new Runnable() {

			public  void run() {

				Toolkit.getDefaultToolkit().beep();// сигнал
				// включаю звук тиканья
				Sound tiktak = new Sound("clock-ticking-3.wav");

				

				String title = "Внимание!";
				String message = "Выключение компьютера через 5 минут!";
				Object[] options = { "Отменить" };
				
				//TODO в диалог добавить отсчёт времени
				JOptionPane pane = new JOptionPane(message,
						JOptionPane.WARNING_MESSAGE, JOptionPane.CANCEL_OPTION,
						crazy, options);
				JDialog dialog = pane.createDialog(title);
				
				
				dialog.setAlwaysOnTop(true);// диалог всегда поверх всех окон
				dialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);// запрешаю закрытие
				dialog.setVisible(true);// показать диалог
				

				if (pane.getValue() == "Отменить") {
					System.out.println("отмена отключения");
					tShutdown.stop();
					tiktak.stop();
					// -a отмена отключения
					// Runtime.getRuntime().exec("shutdown -a");

				}

			}
		});

	}

    

    private static class MoveAdapter extends MouseAdapter {

        private boolean dragging = false;
        private int prevX = -1;
        private int prevY = -1;

        @Override
        public void mousePressed(MouseEvent e) {
            if (SwingUtilities.isLeftMouseButton(e)) {
                dragging = true;
            }
            prevX = e.getXOnScreen();
            prevY = e.getYOnScreen();


            if (SwingUtilities.isRightMouseButton(e)) {
                popupMenu.show(e.getComponent(), e.getX(), e.getY());

            }

        }

        @Override
        public void mouseDragged(MouseEvent e) {
            if (prevX != -1 && prevY != -1 && dragging) {
                Window w = SwingUtilities.getWindowAncestor(e.getComponent());
                if (w != null && w.isShowing()) {
                    Rectangle rect = w.getBounds();
                    WindowsXPos = rect.x ;
                    WindowsYPos = rect.y ;
                    w.setBounds(rect.x + (e.getXOnScreen() - prevX),
                            rect.y + (e.getYOnScreen() - prevY), rect.width, rect.height);
                }
            }
            prevX = e.getXOnScreen();
            prevY = e.getYOnScreen();
        }

        @Override
        public void mouseReleased(MouseEvent e) {
        	if(dragging)
        	 Settings.save();
            dragging = false;
            
        }
    }
    
    
    
    
    
    public static class Settings {

		static final Properties props = new Properties();

		// получаем домашний каталог пользователя
		static final String homeDir = System.getProperty("user.home");
		// они объявлены как final, так что к ним можно получить доступ
		// во внутреннем анонимном классе ниже
		static final String settingsFilename = homeDir + File.separator
				+ "MyClock.properties";

		public static void load() {
			
			System.out.println(settingsFilename);

			// Загрузка сохраненных настроек
			try {
				FileInputStream input = new FileInputStream(settingsFilename);
				props.load(input);
				input.close();
			} catch (Exception ignore) {
				// исключение игнорируется, поскольку ожидалось, что
				// файл установочных параметров иногда может не существовать
				// при первом запуске приложения он точно не будет существовать
			}

			try {
				WindowsXPos = Integer.parseInt(props.getProperty("WindowsXPos", "100"));
				WindowsYPos = Integer.parseInt(props.getProperty("WindowsYPos", "100"));
				SecondsShow = (props.getProperty("SecondsShow", "true")).equals("true");
			} catch (NumberFormatException e) {
				WindowsXPos = 100;
				WindowsYPos = 100;
				SecondsShow = true;
			}

		}

		public static void save() {

			// Сохраняем настройки
			props.setProperty("WindowsXPos", String.valueOf(WindowsXPos));
			props.setProperty("WindowsYPos", String.valueOf(WindowsYPos));
			props.setProperty("SecondsShow", String.valueOf(SecondsShow));
			
			
			try {
				FileOutputStream output = new FileOutputStream(settingsFilename);
				props.store(output, "Saved settings");
				output.close();
			} catch (Exception ignore) {
				// если не получается сохранить настройки,
				// в следующий раз будут использоваться настройки по умолчанию
			}

		}
 	      
    		 
    	 
    	
    	
    	
    }
    
    
    
	public class Sound {

		Clip clip;

		public Sound(String res) {

			try {
				// Open an audio input stream.
				URL url = getClass().getClassLoader().getResource(res);
				AudioInputStream audioIn = AudioSystem.getAudioInputStream(url);
				// Get a sound clip resource.
				clip = AudioSystem.getClip();
				// Open audio clip and load samples from the audio input stream.
				clip.open(audioIn);
				clip.loop(Clip.LOOP_CONTINUOUSLY);
				//FIXME звук начинает играть без старта o_O
				// clip.start();
			} catch (UnsupportedAudioFileException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (LineUnavailableException e) {
				e.printStackTrace();
			}
		}

		public void start() {
			clip.start();
		}

		public void stop() {
			clip.stop();
		}

	}



	
	
	
	
	
	
    
    
    
}
