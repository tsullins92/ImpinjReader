/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impinjreader;
 
import com.impinj.octane.AntennaConfigGroup;
import java.awt.event.*;
import javax.swing.*;
import com.impinj.octane.ImpinjReader;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.Settings;
import com.impinj.octane.Tag;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;
import com.impinj.octane.AntennaConfig;
import com.impinj.octane.FeatureSet;
import com.impinj.octane.ReportConfig;
import com.impinj.octane.ReportMode;
import com.impinj.octane.RxSensitivityTableEntry;
import com.impinj.octane.TxPowerTableEntry;
import java.awt.AWTException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.Scanner;
/**
 *
 * @author tssull1
 */
public class ReadTagsListener implements ActionListener  {
    
    private JTextArea readingTextArea, logTextArea, summaryTextArea;
    private JLabel statusLabel;
    private JTextField ipField;
    private JSlider readPowerSlider, readSensitivitySlider;
    private JComboBox readTimeCombo, readDelayCombo;
    private ArrayList<String> readings, tagIDs, oldReadings;
    private ArrayList<Integer> readingCount;
    private SmartRobot keyboardWedge;
    private ReaderWorker impinjWorker;
    
    
    public ReadTagsListener(JTextField ipField, JSlider readPowerSlider, JSlider readSensitivitySlider, JComboBox readTimeCombo, 
            JComboBox readDelayCombo, JTextArea readingTextArea, JLabel statusLabel, JTextArea summaryTextArea, JTextArea logTextArea,
            ArrayList<String> readings, ArrayList<Integer> readingCount, ArrayList<String> tagIDs, ArrayList<String> oldReadings){
        this.readingTextArea = readingTextArea;
        this.summaryTextArea = summaryTextArea;
        this.logTextArea = logTextArea;
        this.statusLabel = statusLabel;
        this.ipField = ipField;
        this.readPowerSlider = readPowerSlider;
        this.readSensitivitySlider = readSensitivitySlider;
        this.readTimeCombo = readTimeCombo;
        this.readDelayCombo = readDelayCombo;
        this.readings = readings;
        this.tagIDs = tagIDs;
        this.readingCount = readingCount;
        this.oldReadings = oldReadings;
    }
          
    public void actionPerformed(ActionEvent e) {
        ReaderWorker impinjWorker = new ReaderWorker(this.ipField,this.readPowerSlider,this.readSensitivitySlider,this.readTimeCombo,
                this.readDelayCombo,this.readingTextArea, this.statusLabel, this.summaryTextArea, this.logTextArea, 
                this.readings, this.readingCount, this.tagIDs,this.oldReadings);
        impinjWorker.execute();
    }
        
    private String formattedDate(){
        LocalDateTime tempDate = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
        return tempDate.format(myFormatObj);
    }
}
