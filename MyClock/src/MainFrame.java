
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

import java.util.Date;
import java.util.Properties;
import java.net.URL;
import javax.sound.sampled.*;

/**
 *
 * @author zanis  28.05.2013
 * ���� ������������� ��� ���������� ���������� ����� �������� �� 8-�� ����.
 * ����� ������ ��� ���� �������� ������,��������������� �� ����������.
 * ���� �������� ���������� �� ���������� ���������� �� ��� �� ��� ����.
 * ���� ����� ���������� �� �������� ����� ����� ���.
 * ��� �� �������� ������ ������� :
 * ��� ����� ����� ������� ���� �� ���� � ������ ���
 * Seconds Show - ���������� ��������� ������� (�� ����� ������� ����� �������� sleep)
 * Exit - ������� ����
 * Shutdown - ��������� ������ ����������
 * 
 * ��������� ����� ����������� � ���� MyClock.properties � user.home (�������� C:\Users\zanis\MyClock.properties)
 * 

 */

public class MainFrame extends JFrame implements ActionListener ,Runnable{ //�������� �� JFrame �� �������� ��� ���������������� ����
    
    
    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
     * @param args the command line arguments
     */
    public static void main(String[] args) { //��� ������� ����� ���� � � ������ ������

    	try
        {
            // ������������� �������� ����� �����������
            UIManager.setLookAndFeel ( UIManager.getSystemLookAndFeelClassName () );
        }
        catch ( Throwable e )
        {
            //
        }
    	
        new MainFrame(); //������� ��������� ������ ����������

    }
    
   
    
    public static int WindowsXPos;
    public static int WindowsYPos;  
    public static boolean SecondsShow;  
    public static JPopupMenu popupMenu ; 
    
    
    // �������� �����
	final ImageIcon trad = new ImageIcon(getClass().getResource("/trad.png"));
	final ImageIcon trad_h = new ImageIcon(getClass().getResource("/trad_h.png"));
	final ImageIcon trad_m = new ImageIcon(getClass().getResource("/trad_m.png"));
	final ImageIcon trad_s = new ImageIcon(getClass().getResource("/trad_s.png"));
	final ImageIcon trad_dot = new ImageIcon(getClass().getResource("/trad_dot.png"));
	
	// �������� ��������
	// FIXME gif �������� ������
	final ImageIcon crazy = new ImageIcon(getClass().getResource("/crazy.gif"));
	final ImageIcon bye = new ImageIcon(getClass().getResource("/bye.gif"));
	final ImageIcon shok = new ImageIcon(getClass().getResource("/shok.gif"));
	
 
    final int DIALOG_START_TIME = 1000 * 60 * 60 * 2 ; // ��� ���� 
    final int BEFORE_SHUTDOWN_TIME = 1000 * 60  * 5 ; //  5 ����� 
    
    
    Timer tDialog = new Timer(DIALOG_START_TIME, this);// ������ ��� ������� �� ������ ����������
    Timer tShutdown = new Timer(BEFORE_SHUTDOWN_TIME, this) ; // ������  ����������
   
    boolean  running = true;
    Date date;
    int hours ,minutes , seconds ;
 
    
    public MainFrame() {

    	
         super("My Clock"); //��������� ����
        
         
         Settings.load();// �������� ���������� ��������� ����       
         initPopupMenu();
         
      // TODO: �������� � ������������

        getContentPane().add(new JComponent() {
            /**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{

                 setForeground ( Color.BLACK );
                MoveAdapter moveAdapter = new MoveAdapter();
                addMouseListener(moveAdapter);
                addMouseMotionListener(moveAdapter);
            }
            
			// TODO ���� paintComponent � ��������� �����
            @SuppressWarnings("deprecation")
			@Override
            public void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            rh.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2d.setRenderingHints(rh);

            date = new Date();  // current time    

            hours = date.getHours();
            minutes = date.getMinutes();
            seconds = date.getSeconds();
            int drawLocationX = (this.getWidth() - trad_h.getIconWidth()) / 2;
            int drawLocationY = 1;
            int rotateLocationX = trad_h.getIconWidth() / 2;
            int rotateLocationY = trad_h.getIconHeight() / 2;
            AffineTransform at = new AffineTransform();

            // ����
            g2d.drawImage(trad.getImage(), 0, 0, null);

            // ������� �������
            at.setToTranslation(drawLocationX, drawLocationY); // position x,y
            at.rotate(Math.toRadians(((hours > 12 ? hours - 12 : hours) * 30) + (minutes / 2)), rotateLocationX, rotateLocationY); // rotation, set center rotation              
            g2d.drawImage(trad_h.getImage(), at, null); // Drawing the rotated image at the required drawing locations

            // �������� �������
            at.setToTranslation(drawLocationX, drawLocationY); // position x,y
            at.rotate(Math.toRadians((minutes * 6) + (seconds / 10)), rotateLocationX, rotateLocationY); // rotation, set center rotation      
            g2d.drawImage(trad_m.getImage(), at, null); // Drawing the rotated image at the required drawing locations

            // ����� �� ������
            g2d.drawImage(trad_dot.getImage(), drawLocationX, drawLocationY, null);

            // ��������� �������
            if(SecondsShow){
            at.setToTranslation(drawLocationX, drawLocationY); // position x,y
            at.rotate(Math.toRadians(seconds * 6), rotateLocationX, rotateLocationY); // rotation, set center rotation              
            g2d.drawImage(trad_s.getImage(), at, null); // Drawing the rotated image at the required drawing locations
            }
            
          //TODO � ���� �������� ������ ������� 
            

            Toolkit.getDefaultToolkit().sync();
            g.dispose();
        }
            
   
            
        });
        
           
 
        	setType(Type.UTILITY); // ����� ������ �� ������ �����
        	
            setUndecorated(true);//�������� ����������� ��������� ��
            setSize(trad.getIconWidth(), trad.getIconHeight()); // ��������� ������� ���� �� ������� �����
            
            
            setLocation(WindowsXPos, WindowsYPos);
            AWTUtilities.setWindowOpaque(this, false);//������� ���� ��������������
           
          
            setVisible(true); // ������ �������
            toBack(); // ������ ������ ���� ����

            
            // ������ ������,� �� ����� ���������� ���������� ���������� �����
         // FIXME  �����: ��������� ��???
             (new Thread(this)).start();

    }
    
    
    @SuppressWarnings("deprecation")
	@Override
	public void run() {

		while (running) {

			repaint();
			try {
				//���� ���������� ��������� ������� �� �������� �������,���� ������������ �� ������
				Thread.sleep(SecondsShow ? 1000 : 1000 * 60);
			} catch (InterruptedException e) {
			}

			// ���� ����� �� 23:00 �� 8-�� ����
			if (hours >= 23 || hours < 8) {
				// ������ ������� ������� ����������
				if (!tDialog.isRunning()) {
					System.out.println("������ ������� ������� ����������");
					tDialog.start();
				}
			}

		}
		// FIXME  �����: ��������� ��???
		Thread.currentThread().destroy();
		

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
			Settings.save();
			// FIXME  �����: ��������� ��???
			running = false;
			running = true;
			(new Thread(this)).start();
	

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
		// -s ���������� ����������
		// -f ������������� �������� ������ ������ ����������
		// -t 60 ��������� ���������� �� �� 60 ������
		
		try {
			// ����� ������ ���������������� ���� �� ��,������ -t 00
			Runtime.getRuntime().exec("shutdown  -s -f -t 00 ");
		} catch (IOException e) {}
		
		exit();
		 
	}


	private void DialogCancelShutdown() {

		tShutdown.start();
		
		EventQueue.invokeLater(new Runnable() {

			public  void run() {

				Toolkit.getDefaultToolkit().beep();// ������
				// ������� ���� �������
				Sound tiktak = new Sound("clock-ticking-3.wav");

				

				String title = "��������!";
				String message = "���������� ���������� ����� 5 �����!";
				Object[] options = { "��������" };
				
				//TODO � ������ �������� ������ �������
				JOptionPane pane = new JOptionPane(message,
						JOptionPane.WARNING_MESSAGE, JOptionPane.CANCEL_OPTION,
						crazy, options);
				JDialog dialog = pane.createDialog(title);
				
				
				dialog.setAlwaysOnTop(true);// ������ ������ ������ ���� ����
				dialog.setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);// �������� ��������
				dialog.setVisible(true);// �������� ������
				

				if (pane.getValue() == "��������") {
					System.out.println("������ ����������");
					tShutdown.stop();
					tiktak.stop();
					// -a ������ ����������
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

		// �������� �������� ������� ������������
		static final String homeDir = System.getProperty("user.home");
		// ��� ��������� ��� final, ��� ��� � ��� ����� �������� ������
		// �� ���������� ��������� ������ ����
		static final String settingsFilename = homeDir + File.separator
				+ "MyClock.properties";

		public static void load() {
			
			System.out.println(settingsFilename);

			// �������� ����������� ��������
			try {
				FileInputStream input = new FileInputStream(settingsFilename);
				props.load(input);
				input.close();
			} catch (Exception ignore) {
				// ���������� ������������, ��������� ���������, ���
				// ���� ������������ ���������� ������ ����� �� ������������
				// ��� ������ ������� ���������� �� ����� �� ����� ������������
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

			// ��������� ���������
			props.setProperty("WindowsXPos", String.valueOf(WindowsXPos));
			props.setProperty("WindowsYPos", String.valueOf(WindowsYPos));
			props.setProperty("SecondsShow", String.valueOf(SecondsShow));
			
			
			try {
				FileOutputStream output = new FileOutputStream(settingsFilename);
				props.store(output, "Saved settings");
				output.close();
			} catch (Exception ignore) {
				// ���� �� ���������� ��������� ���������,
				// � ��������� ��� ����� �������������� ��������� �� ���������
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
				//FIXME ���� �������� ������ ��� ������ o_O
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
