/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package impinjreader;

import com.impinj.octane.AntennaConfigGroup;
import com.impinj.octane.OctaneSdkException;
import com.impinj.octane.ReportConfig;
import com.impinj.octane.ReportMode;
import com.impinj.octane.Settings;
import com.impinj.octane.Tag;
import com.impinj.octane.TagReport;
import com.impinj.octane.TagReportListener;
//import com.sun.tools.javac.util.ArrayUtils;
import java.awt.AWTException;
import java.awt.Toolkit;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

/**
 *
 * @author tssull1
 */
public class ReaderWorker extends SwingWorker<Integer, String> implements TagReportListener {
    
    private JTextArea readingTextArea, logTextArea, summaryTextArea;
    private JLabel statusLabel;
    private JTextField ipField;
    private JSlider readPowerSlider, readSensitivitySlider;
    private JComboBox readTimeCombo, readDelayCombo;
    private ArrayList<String> readings, tagIDs, oldReadings;
    private ArrayList<Integer> readingCount;
    private SmartRobot keyboardWedge;
    private ReaderWorker impinjWorker;    

    
    public ReaderWorker(JTextField ipField, JSlider readPowerSlider, JSlider readSensitivitySlider, JComboBox readTimeCombo, JComboBox readDelayCombo,
            JTextArea readingTextArea, JLabel statusLabel, JTextArea summaryTextArea, JTextArea logTextArea,ArrayList<String> readings, 
            ArrayList<Integer> readingCount, ArrayList<String> tagIDs, ArrayList<String> oldReadings){
        this.readingTextArea = readingTextArea;
        this.summaryTextArea = summaryTextArea;
        this.logTextArea = logTextArea;
        this.statusLabel = statusLabel;
        this.ipField = ipField;
        this.readPowerSlider = readPowerSlider;
        this.readSensitivitySlider = readSensitivitySlider;
        this.readTimeCombo = readTimeCombo;
        this.readDelayCombo = readDelayCombo;
        this.statusLabel.setText("Reading");
        this.readings = readings;
        this.oldReadings = oldReadings;
        this.readingCount = readingCount;
        this.tagIDs = tagIDs;
    }
    
    @Override
    protected Integer doInBackground() throws Exception {
        System.out.println(this.readings);
        com.impinj.octane.ImpinjReader reader = new com.impinj.octane.ImpinjReader();
        try{
            reader.connect(this.ipField.getText());
            // connect a listener
            reader.setTagReportListener(this);
            // Get the default settings
            Settings settings = reader.queryDefaultSettings();
            // set all antennas
            AntennaConfigGroup ag = settings.getAntennas();
            ag.setIsMaxRxSensitivity(false);
            ag.setRxSensitivityinDbm((double)this.readSensitivitySlider.getValue());
            ag.setIsMaxTxPower(false);
            ag.setTxPowerinDbm((double)this.readPowerSlider.getValue());
            ReportConfig report = settings.getReport();
            report.setIncludeAntennaPortNumber(true);
            report.setIncludeFastId(true);
//            report.setMode(ReportMode.BatchAfterStop);
            report.setMode(ReportMode.Individual);
            reader.applySettings(settings);
            this.statusLabel.setText((this.readDelayCombo.getSelectedIndex()+1) + " Second Delay Period");
            this.logTextArea.append(formattedDate()+" --- "+(this.readDelayCombo.getSelectedIndex()+1) + " Second Delay Period"+"\n");
            try {
                Thread.sleep((this.readDelayCombo.getSelectedIndex()+1)*1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            // Start the reader
            this.statusLabel.setText((this.readTimeCombo.getSelectedIndex()+1) + " Second Read Period");
            this.logTextArea.append(formattedDate()+" --- "+(this.readTimeCombo.getSelectedIndex()+1) + " Second Read Period"+"\n");
            reader.start();
            try {
                Thread.sleep((this.readTimeCombo.getSelectedIndex()+1)*1000);
            } catch (InterruptedException ex) {
                Thread.currentThread().interrupt();
            }
            reader.stop();
            reader.disconnect();
            reader.removeTagReportListener();
            this.statusLabel.setText("Reading Finished --- "+this.readings.size()+" Unique Labels");
            this.keyboardWedge = new SmartRobot();
            this.summaryTextArea.setText("Reading --- Count\n");
            for(String reading : this.readings){
                this.summaryTextArea.append(reading+" --- "+this.readingCount.get(this.readings.indexOf(reading))+"\n");
                Thread.sleep(150);
                // this.keyboardWedge.type(reading);
                // if(this.oldReadings.contains(reading)){}
                // else{
                    // this.oldReadings.add(reading);

                // }
            }
            if(this.readingCount.size()==1){
                this.keyboardWedge.type(this.readingCount.get(0).toString());
            }
            else if(this.readingCount.size()==0){
                this.keyboardWedge.type("No EPC Values Detected");
            }
            else if(this.readingCount.size()>1){
                this.keyboardWedge.type("Multiple EPC Values Detected");
            }            
            this.logTextArea.append(formattedDate()+" --- "+"Read Finished"+"\n");
        } catch (OctaneSdkException ex) {
            reader.disconnect();
            this.logTextArea.append(formattedDate()+" --- "+ex.getMessage()+"\n");
        } catch(AWTException ex){
            reader.disconnect();
            this.logTextArea.append(formattedDate()+" --- "+ex.getMessage()+"\n");
            ex.printStackTrace(System.out);        
        } catch (Exception ex) {
            reader.disconnect();
            this.logTextArea.append(formattedDate()+" --- "+ex.getMessage()+"\n");
            ex.printStackTrace(System.out);
        } 
      this.done();
      return 0;
    }

    @Override
    public void onTagReported(com.impinj.octane.ImpinjReader reader, TagReport report) {
        List<Tag> tags = report.getTags();
        for (Tag t : tags) {
            String textReading = t.getEpc().toString();
            textReading = "532D "+textReading;
//            textReading = textReading.substring(4, textReading.length()); -------- RFID Values are 12 bytes now, so don't need this
            String tagID = t.getTid().toString();
            textReading = textReading.replace(" ","");
            textReading = convertHexToString(textReading);
            if(!this.tagIDs.contains(tagID)){
                if(this.readings.isEmpty()){
                    this.readings.add(textReading);
                    this.tagIDs.add(tagID);
                    this.readingCount.add(1);
                } else if(this.readings.contains(textReading)){
                    int readingIndex = this.readings.indexOf(textReading);
                    int readingCountValue = this.readingCount.get(readingIndex);
                    this.tagIDs.add(tagID);
                    this.readingCount.set(readingIndex,++readingCountValue);
                } else{
                    this.readings.add(textReading);
                    this.tagIDs.add(tagID);
                    this.readingCount.add(1);
                }
                textReading = textReading+="\n";
                this.readingTextArea.append(textReading);
            }
        }
    }    
    
    public String convertHexToString(String hex){
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        
        //49204c6f7665204a617661 split into two characters 49, 20, 4c...
        for( int i=0; i<hex.length()-1; i+=2 ){
            //grab the hex in pairs
            String output = hex.substring(i, (i + 2));
            //convert hex to decimal
            int decimal = Integer.parseInt(output, 16);
            //convert the decimal to character
            sb.append((char)decimal);
            temp.append(decimal);
        }
        return sb.toString();
    }    
    
    private String formattedDate(){
        LocalDateTime tempDate = LocalDateTime.now();
        DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("MM-dd-yyyy HH:mm:ss"); 
        return tempDate.format(myFormatObj);
    }
}
