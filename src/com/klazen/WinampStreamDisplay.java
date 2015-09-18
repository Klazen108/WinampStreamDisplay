package com.klazen;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

import com.qotsa.exception.InvalidHandle;
import com.qotsa.jni.controller.WinampController;

public class WinampStreamDisplay extends JFrame {
	private static final long serialVersionUID = 6291676800069861590L;

	public static void main(String[] args) {
		new WinampStreamDisplay();
	}
	
	JMarqueeLabel lbl;
	Properties props;
	
	public static final String PROPS_FILE_NAME = "wsd.properties";
	public static final Pattern WINAMP_NAME_PATTERN = Pattern.compile("\\d+\\.\\s(.*)");
	
	private void loadProperties() {
		props = new Properties();
		File propsFile = new File(PROPS_FILE_NAME);
		if (propsFile.exists()) {
			try {
				props.load(new FileInputStream(propsFile));
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public WinampStreamDisplay() {
		super("Winamp Stream Display - by klazen108");
		
		loadProperties();
		
		lbl = new JMarqueeLabel();
		lbl.setTextFont(new Font(tryGetProperty("font", "Arial"),Font.PLAIN,tryParseInt(tryGetProperty("fontSize", "18"),18)));
		lbl.setBackground(Color.decode(tryGetProperty("bgcolor", "000000")));
		lbl.setForeground(Color.decode(tryGetProperty("fgcolor", "16777215")));
		lbl.setSpeed(tryParseInt(tryGetProperty("scrollSpeed", "10"),10));
		setLabelText();
		add(lbl);
		
		setSize(tryParseInt(tryGetProperty("width", "400"),400),tryParseInt(tryGetProperty("height","64"),64));
		setVisible(true);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		Timer t = new Timer("Text Update Timer");
		t.schedule(new TimerTask() {
			@Override
			public void run() {
				setLabelText();
			}
		}, 0, 500);
		
		Runtime.getRuntime().addShutdownHook(new Thread()
		{
		    @Override
		    public void run()
		    {
		    	try {
					props.store(new FileOutputStream(PROPS_FILE_NAME), "Winamp Stream Display props by Klazen108");
				} catch (IOException e) {
					System.err.println("Error saving properties - "+e.getMessage());
				}
		    }
		});
	}
	
	private String tryGetProperty(String key, String defaultValue) {
		if (props.containsKey(key)) return props.getProperty(key);
		else {
			props.setProperty(key, defaultValue);
			return defaultValue;
		}
	}
	
	private int tryParseInt(String string, int defaultValue) {
		try {
			return Integer.parseInt(string);
		} catch (Exception e) {
			return defaultValue;
		}
	}
	
	public synchronized void setLabelText() {
		try {
			String title = WinampController.getTitle();
			Matcher m = WINAMP_NAME_PATTERN.matcher(title);
			if (m.matches()) {
				title = m.group(1);
				int winamppos = title.lastIndexOf('-');
				title = title.substring(0, winamppos-1);
			}
			lbl.setText(title);
		} catch (InvalidHandle e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			lbl.setText("Winamp not running...");
		} catch (Exception e) {
			
		}
	}
}

/**
 * http://stackoverflow.com/questions/2291760/how-to-add-marquee-behaviour-to-jlabel
 * @author Martijn Courteaux
 *
 */
class JMarqueeLabel extends JPanel implements Runnable
{
    private static final long serialVersionUID = -2973353417536204185L;
    private int x;
    private FontMetrics fontMetrics;
    public static final int MAX_SPEED = 30;
    public static final int MIN_SPEED = 1;
    private int speed;
    public static final int SCROLL_TO_LEFT = 0;
    public static final int SCROLL_TO_RIGHT = 1;
    private int scrollDirection = 0;
    private boolean started = false;
    private JLabel label;

    
    public JMarqueeLabel() {
    	this("");
    }
    
    public JMarqueeLabel(String text)
    {
        super();
        label = new JLabel(text)
        {
            private static final long serialVersionUID = -870580607070467359L;

            @Override
            protected void paintComponent(Graphics g)
            {
                g.translate(x, 0);
                super.paintComponent(g);
            }
        };
        setLayout(null);
        add(label);
        setSpeed(10);
        setScrollDirection(SCROLL_TO_LEFT);
    }

    @Override public void setForeground(Color c) {
		if (label != null) label.setForeground(c);
    }
    
    @Override
    protected void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        label.paintComponents(g);
    }

    public void setScrollDirection(int scrollDirection)
    {
        this.scrollDirection = scrollDirection;
    }

    public int getScrollDirection()
    {
        return scrollDirection;
    }

    public void setSpeed(int speed)
    {
        if (speed < MIN_SPEED || speed > MAX_SPEED)
        {
            throw new IllegalArgumentException("speed out of range");
        }
        this.speed = speed;
    }

    public int getSpeed()
    {
        return speed;
    }

    @Override
    public void validateTree()
    {
        //System.out.println("Validate...");
        super.validateTree();
        label.setBounds(0, 0, 2000, getHeight());
        if (!started)
        {
            x = getWidth() + 10;
            Thread t = new Thread(this);
            t.setDaemon(true);
            t.start();
            started = true;
        }
    }

    public String getText()
    {
        return label.getText();
    }

    public void setText(String text)
    {
        label.setText(text);
    }

    public void setTextFont(Font font)
    {
        label.setFont(font);
        fontMetrics = label.getFontMetrics(label.getFont());
    }

    @Override
    public void run()
    {
        fontMetrics = label.getFontMetrics(label.getFont());
        try
        {
            Thread.sleep(100);
        } catch (Exception e)
        {
        }
        while (true)
        {
            if (scrollDirection == SCROLL_TO_LEFT)
            {
                x--;
                if (x < -fontMetrics.stringWidth(label.getText()) - 10)
                {
                    x = getWidth() + 10;
                }
            }
            if (scrollDirection == SCROLL_TO_RIGHT)
            {
                x++;
                if (x > getWidth() + 10)
                {
                    x = -fontMetrics.stringWidth(label.getText()) - 10;
                }
            }
            repaint();
            try
            {
                Thread.sleep(35 - speed);
            } catch (Exception e)
            {
            }
        }
    }

}
