        /*
        * To change this license header, choose License Headers in Project Properties.
        * To change this template file, choose Tools | Templates
        * and open the template in the editor.
        */
        package impinjreader;
       
        import javax.swing.*;
        import java.awt.*;
        import java.awt.event.*; 
        import java.awt.image.*;
        import javax.imageio.*;
        import java.util.Properties;
        import java.io.*;
        import java.time.LocalDateTime;
        import java.time.format.DateTimeFormatter;
        import java.util.ArrayList;
        import java.net.URL;
        /**
        *
        * @author tssull1
        */
        public class ImpinjReader extends JFrame {
        
        JButton saveSettings, readTags, clearTags;
        JTextField ipField;
        JTextArea readingArea, logArea, summaryArea;
        JScrollPane readingScrollPane, logScrollPane, summaryScrollPane;
        JLabel readPowerLabel, readSensitivityLabel, readDelayLabel, ipLabel, readTimeLabel, readingLabel, statusLabelLabel, statusLabel, logLabel, summaryLabel;
        JSlider readPowerSlider, readSensitivitySlider;
        JComboBox<String> readDelayCombo, readTimeCombo;
        private ArrayList<String> readings, tagIDs, oldReadings;
        private ArrayList<Integer> readingCount;    
        
        Properties savedProperties = new Properties();
        FileInputStream propertiesInputStream;
        FileOutputStream propertiesOutputStream;
        int readPower;
        int readSensitivity;
        int readDelay;
        int readTime;
        String[] readDelayArray = new String[10];
        String[] readTimeArray = new String[10];
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        
        ImpinjReader(String title){
                super(title);
                // ImageIcon impinjReaderIcon = loadIcon("file:///C:/Users/tssull1/Documents/code/java/impinjReader20190909_wave.png");
                // System.out.println("Hey");
                // if(impinjReaderIcon != null){
                    // System.out.println("impinjReaderIcon");
                    // setIconImage(impinjReaderIcon.getImage());
                // }
                this.readings = new ArrayList<String>(0);
                this.readingCount = new ArrayList<Integer>(0);
                this.tagIDs = new ArrayList<String>(0);
                this.oldReadings = new ArrayList<String>(0);
                //IP address field
                ipLabel = new JLabel("IP Address:"); //Label for IP Address input
                ipField = new JTextField(13); //IP address input
                //Read power field
                readPowerLabel = new JLabel("Read Power:");//label for read power input
                readPowerSlider = new JSlider(JSlider.HORIZONTAL,10,32,32); //read power slider
                readPowerSlider.setMinorTickSpacing(1);
                readPowerSlider.setMajorTickSpacing(5);
                readPowerSlider.setPaintTicks(true);
                readPowerSlider.setPaintLabels(true);
                //Read sensitivity field
                readSensitivityLabel = new JLabel("Read Sensitivity:"); //label for read sensitivity input
                readSensitivitySlider = new JSlider(JSlider.HORIZONTAL,-70,-30,-30);//read sensitivity input
                readSensitivitySlider.setMinorTickSpacing(1);
                readSensitivitySlider.setMajorTickSpacing(5);
                readSensitivitySlider.setPaintTicks(true);
                readSensitivitySlider.setPaintLabels(true);
                //Read time input
                readTimeLabel = new JLabel("Read Time(s):");//label for read time input
                for(int i = 0; i < readTimeArray.length; ++i){//populate readTimeArray with values 1 through readValueArray.length
                    readTimeArray[i] = String.valueOf(i+1);
                }
                readTimeCombo = new JComboBox<>(readTimeArray); //read time input       
                //Read delay input
                readDelayLabel = new JLabel("Read Delay(s):");//label for read delay input
                for(int i = 0; i < readDelayArray.length; ++i){//populate readDelayArray with values 1 through readDelayArray.length 
                    readDelayArray[i] = String.valueOf(i+1);
                }
                readDelayCombo = new JComboBox<>(readDelayArray); //read delay input
                //Save settings button
                saveSettings=new JButton("Save Settings");    //creating instance of JButton 
                //Readings area
                readingLabel = new JLabel("Readings:");//label for the readings area
                readingArea = new JTextArea("Readings",2000,20); //Text area where readings are output
                readingArea.setLineWrap(true); //makes it so that lines don't run outside of the text area horizontally
                readingArea.setText("");
                readingScrollPane = new JScrollPane(readingArea); //Scroll pane that readings area is put into
                readingScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                readingScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
                //Sumary Area
                summaryLabel = new JLabel("Summary:");//label for the summary area
                summaryArea = new JTextArea("Summary",2000,20); //Text area where the summary is output
                summaryArea.setLineWrap(true); //makes it so that lines don't run outside of the text area horizontally
                summaryArea.setText("Reading --- Count\n");
                summaryScrollPane = new JScrollPane(summaryArea); //Scroll pane that summary area is put into
                summaryScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                summaryScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED); 
                //Button to get readings
                readTags=new JButton("Read");//creating instance of JButton  
                //Button to clear readings
                clearTags=new JButton("Clear");//creating instance of JButton  
                //Label telling current status of the program
                statusLabelLabel = new JLabel("Status: ");
                statusLabel = new JLabel("Not Connected");
                //Log area input
                logLabel = new JLabel("Log:");
                logArea = new JTextArea(2000,20);//Text area where log messages are output
                logArea.setLineWrap(true); //makes it so that lines don't run outside of the text area horizontally
                logScrollPane = new JScrollPane(logArea); //Scroll pane that log area is put into
                logScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                logScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);        
                //Set the location and size of each element
                ipLabel.setBounds(40,10,200,20);
                ipField.setBounds(140,10,200,20);
                readPowerLabel.setBounds(40,45,100,20);
                readPowerSlider.setBounds(140,45,300,40);
                readSensitivityLabel.setBounds(40,100,100,30);
                readSensitivitySlider.setBounds(140,100,300,40);
                readTimeLabel.setBounds(40,150,100,20);
                readTimeCombo.setBounds(140,150,40,20);
                readDelayLabel.setBounds(40,180,100,20);
                readDelayCombo.setBounds(140,180,40,20);
            saveSettings.setBounds(40,220,100,20);
            readingLabel.setBounds(40,250,100,20);
            readingScrollPane.setBounds(40,270,200,200);
            summaryLabel.setBounds(280,250,100,20);
            summaryScrollPane.setBounds(270,270,200,200);
            readTags.setBounds(40,480,100,20);
            clearTags.setBounds(160,480,100,20); 
            statusLabelLabel.setBounds(40,510,100,20);
            statusLabel.setBounds(140,510,300,20);
            logLabel.setBounds(40,540,100,20);
            logScrollPane.setBounds(40,560,430,100);
            //Add each element to the frame       
            add(ipLabel);
            add(ipField);
            add(readPowerLabel);
            add(readPowerSlider);
            add(readSensitivityLabel);
            add(readSensitivitySlider);
            add(readTimeLabel);
            add(readTimeCombo);
            add(readDelayLabel);
            add(readDelayCombo);
            add(saveSettings);
            add(readingLabel);
            add(readingScrollPane);
            add(summaryLabel);
            add(summaryScrollPane);
            add(readTags);
            add(clearTags);
            add(statusLabelLabel);
            add(statusLabel);
            add(logLabel);
            add(logScrollPane);
            //Load the settings from the properties file and set the values of the inputs with them
            try{
                propertiesInputStream = new FileInputStream("readerProperties.properties");
                savedProperties.load(propertiesInputStream);
                ipField.setText(savedProperties.getProperty("reader_ip"));
                readPowerSlider.setValue(Integer.parseInt(savedProperties.getProperty("reader_power")));
                readSensitivitySlider.setValue(Integer.parseInt(savedProperties.getProperty("reader_sensitivity")));
                readTimeCombo.setSelectedIndex(Integer.parseInt(savedProperties.getProperty("read_time"))-1);
                readDelayCombo.setSelectedIndex(Integer.parseInt(savedProperties.getProperty("read_delay"))-1);
                statusLabel.setText("Settings Loaded From File");
                logArea.append(formattedDate()+" --- "+"Settings Loaded From File\n");
                propertiesInputStream.close();
            }
            catch(FileNotFoundException e){
                statusLabel.setText("Could not find file");
                logArea.append(formattedDate()+" --- "+"Could not find file\n");
                e.printStackTrace(pw);
                String sStackTrace = sw.toString();
                System.out.println(sStackTrace);
            }
            catch(IOException e){
                statusLabel.setText("Could not load file");
                logArea.append(formattedDate()+" --- "+"Could not load file\n");
                e.printStackTrace(pw);
                String sStackTrace = sw.toString();
                System.out.println(sStackTrace);     
            }
            saveSettings.addActionListener(new ActionListener(){  
                @Override
                public void actionPerformed(ActionEvent e){  
                    try{
                        File propertiesFile = new File("readerProperties.properties");
                        propertiesOutputStream = new FileOutputStream(propertiesFile);
                        savedProperties.setProperty("reader_ip",ipField.getText());  
                        savedProperties.setProperty("reader_power",String.valueOf(readPowerSlider.getValue()));
                        savedProperties.setProperty("reader_sensitivity",String.valueOf(readSensitivitySlider.getValue()));
                        savedProperties.setProperty("read_time",String.valueOf(readTimeCombo.getSelectedIndex()+1));
                        savedProperties.setProperty("read_delay",String.valueOf(readDelayCombo.getSelectedIndex()+1));
                        savedProperties.store(propertiesOutputStream, "Properties Last Saved --- "+formattedDate());
                        statusLabel.setText("Settings Saved To File");
                        logArea.append(formattedDate()+" --- "+"Settings Saved To File\n");
                    } catch(FileNotFoundException ex){
                        statusLabel.setText("Could not find file");
                        logArea.append(formattedDate()+" --- "+"Could not find file\n");
                        ex.printStackTrace(pw);
                        String sStackTrace = sw.toString();
                        System.out.println(sStackTrace);
                    } catch(IOException ex){
                        statusLabel.setText("Could not save file");
                        logArea.append(formattedDate()+" --- "+"Could not save file\n");
                        ex.printStackTrace(pw);
                        String sStackTrace = sw.toString();
                        System.out.println(sStackTrace);     
                    }
    
                } 
            });  
            readTags.addActionListener(new ReadTagsListener(this.ipField,this.readPowerSlider,this.readSensitivitySlider,this.readTimeCombo,
                    this.readDelayCombo,this.readingArea, this.statusLabel, this.summaryArea, this.logArea,this.readings,
                    this.readingCount,this.tagIDs,this.oldReadings)); //Listener that gets readings and assigns the value to this.readingArea
            clearTags.addActionListener(new ActionListener(){  
                @Override
                public void actionPerformed(ActionEvent e){  
                    readingArea.setText("");
                    statusLabel.setText("");
                    summaryArea.setText("Reading --- Count\n");
                    logArea.append(formattedDate()+" --- Cleared\n");
                    readings.clear();
                    readingCount.clear();
                    tagIDs.clear();
                    oldReadings.clear();           
                } 
            });  
    
            // Image icon = Toolkit.getDefaultToolkit().getImage("impinjReader20190909_wave.png");
            // setIconImage(icon);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setSize(500, 700);
            setLayout(null);//using no layout managers  
            setVisible(true);  
            Font textFont = new Font(Font.SANS_SERIF,Font.PLAIN,10);
            changeFont(this,textFont);
            this.setLayout(null);
            //URL imgURL = getResource(strPath);
        }
        
        // private ImageIcon loadIcon(String strPath)
        // {
            // URL imgURL = getClass().getResource(strPath);
            // System.out.println("1");
            // if(imgURL != null){
                // System.out.println("2");
                // return new ImageIcon(imgURL);
            // }
            // else{
                // System.out.println("3");
                // return null;
            // }
        // }
        
        public static void changeFont ( Component component, Font font )
        {
            component.setFont ( font );
            if ( component instanceof Container )
            {
                for ( Component child : ( ( Container ) component ).getComponents () )
                {
                    changeFont ( child, font );
                }
            }
        }   
        
        private String formattedDate(){
            LocalDateTime tempDate = LocalDateTime.now();
            DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
            return tempDate.format(myFormatObj);
        }
        
        public static void main(String[] args) {
            ImpinjReader impinjReader = new ImpinjReader("Impinj Reader");
            BufferedImage img = null; 
            try {
                img = ImageIO.read(new File("20190909_wave.png"));
            } catch(IOException e){
            impinjReader.logArea.append(e.getMessage()+"\n");
            e.printStackTrace(System.out);        
        }
        
        impinjReader.setIconImage(img);
        
    }
    
}