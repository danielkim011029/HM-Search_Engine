/*
Name: Myeongcheol Dan Kim
The program uses the HM.com website to search and obtain clothing information. Every information is stored in
a Hashmap called storage and every information that goes into this Hashmap storage gets saved into a back up
file called storage. This program requires users to log in and then based on the user, they can perform actions
such as modifying storage, obtaining data from the storage, and re-building the storage from a back up file.
Every information that changes the Hashmap storage will be logged to a file called transactionlog which then
can be used to re-build the data as previously mentioned. You can also download images from the outputGUI.
The parameters this program accetps are explained down below before the main function.
My innovation #1 is the ability for my GUI to use the advanced search function from the website
My innovation #2 is when you download the image, the image is shown onto a GUI.
*/
import javax.swing.*;
import java.awt.event.*;
import java.net.*;
import java.io.*;
import java.awt.image.*;
import javax.imageio.*;
import java.util.*;
import java.sql.Timestamp;
import java.util.Date; 
import java.text.SimpleDateFormat; 

public class Mini_Software{
    static HashMap<Timestamp,Data> storage=new HashMap<Timestamp,Data>(); 
    public static final String USER_AGENT = "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.2) Gecko/20100115 Firefox/3.6";
    public static BufferedImage image = null;
    public static String USER="";   //the username

    //default constructor that starts up the GUI
    public Mini_Software(){
        LOGINGUI();
    }
    //LOGIN GUI starts the whole process of logging in and performing any other task
    //USERNAME must be ADMIN, GUEST, or any form of USER such as USER0, USER1, USER2, etc.
    public void LOGINGUI(){
        JFrame frame=new JFrame("HM LOGIN");
        JPanel panel=new JPanel();
        JLabel label=new JLabel("USER");
        JTextField input=new JTextField(20);
        JButton button=new JButton("enter");
        frame.setSize(500,500);
        frame.setLocation(400,100);
        label.setLabelFor(input);
        panel.add(label);
        panel.add(input);
        panel.add(button);
        button.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(input.getText().indexOf("USER")!=-1||input.getText().equals("GUEST")||input.getText().equals("ADMIN")){
                    USER=input.getText();
                    System.out.println("LOGIN SUCCESSFUL: "+USER);
                    frame.setVisible(false);
                    frame.dispose();
                    searchGUI();
                }
                else{
                    System.out.println("UNKNOWN USER TRY AGAIN");
                    LOGINGUI();
                }
            }
        });

        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    //The Main GUI where users can search on the web, modify storage, change user, search for data in the
    //storage, and re build data from back up file.
    public void searchGUI() {
        JFrame frame=new JFrame("HM SEARCH MAIN");
        JPanel panel=new JPanel();
        JLabel label=new JLabel("SEARCH HERE");
        JTextField input=new JTextField(20);
        JButton enter=new JButton("enter");
        JButton modifyStorage=new JButton("MODIFY STORAGE");
        JButton changeUser=new JButton("CHANGE USER");
        JButton searchStorage=new JButton("SEARCH STORAGE");
        JButton reBuildData=new JButton("REBUILD DATA");

        frame.setSize(500,500);
        frame.setLocation(400,100);
        label.setLabelFor(input);
        panel.add(label);
        panel.add(input);
        panel.add(enter);
        panel.add(modifyStorage);
        panel.add(changeUser);
        panel.add(searchStorage);
        panel.add(reBuildData);
        //this button handles searching for items from the website
        enter.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String text=input.getText();
                redirectURL(text,true);
            }
        });
        //this button handles modifying storage
        modifyStorage.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                listenToCommands();
            }
        });
        //this button handles changing user aka re-login
        changeUser.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                LOGINGUI();
                frame.dispose();
                frame.setVisible(false);
            }
        });
        //this button handles searching for items in the storage
        searchStorage.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(USER.equals("GUEST")){
                    System.out.println("GUESTS CANNOT STORE DATA THEREFORE DOES NOT HAVE ACCESS TO THIS COMMAND");
                }
                else{
                    searchStorageGUI();
                }
            }
        });
        //this button handles re building the data from a back up file
        reBuildData.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                if(USER.equals("ADMIN")){
                    try{
                        BufferedReader reader=new BufferedReader(new FileReader("./transactionlog"));
                        String line=reader.readLine();
                        while(line!=null){
                            if(line.split(",").length>2){
                                listenToCommands(line);
                                System.out.println("MODIFYING STORAGE");
                            }
                            else if(line.split(",").length==2){
                                redirectURL(line,false);
                            }
                            line=reader.readLine();
                        }
                        reader.close();
                        updateStorage();
                    }
                    catch(Exception error){
                        System.out.println(error);
                    }
                }
                else{
                    System.out.println("ONLY ADMIN HAVE ACCESS TO THIS COMMAND");
                }
            }
        });
        frame.add(panel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
    //This function searches the query inputted from the GUI on the web's advanced search function then calls
    //getINFO
    public static void redirectURL(String text,boolean logIt){
        Timestamp savedTime=null;
        if(!logIt){
            try{
                String[] information=text.split(",");
                text=information[0];
                String date=formatDate(information[1]);
                Date savedDate=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss.SSS").parse(date);
                savedTime=new Timestamp(savedDate.getTime());
            }
            catch(Exception err){
                System.out.println(err);
            }

        }
        System.out.println("Searching query: "+text);
        String url="https://www2.hm.com/en_us/search-results.html?q=";
        String query="";
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==' ') query=query+'+';
            else query=query+text.charAt(i);
        }
        url=url+query;
        try{
            BufferedReader reader=read(url);
            FileWriter writer=new FileWriter("./html");
            String output="";
            String line=reader.readLine();
            while(line!=null){
                output=output+line;
                line=reader.readLine();
            }
            //if no results are found
            if(output.indexOf("NO MATCHING ITEMS")!=-1){
                System.out.println("No RESULTS FOUND");
            }
            //if results are found
            else{
                int start_position=output.indexOf("Product items");
                int end_position=output.indexOf("data-total");
                output=output.substring(start_position,end_position);
                writer.write(output);
                writer.close();
                int numberOfResults=getNumberOfResults(output);
                System.out.println("NUMBER OF RESULTS FOUND: "+numberOfResults);
                //create connection and output information
                String[] urls=getUrls(output);
                getInfo(urls,text,logIt,savedTime,query);
                
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    //This function creates a connection to the website and obtains information of the items that was searched.
    //This functino creates a connection by getting the html from the searched query then extracting all of 
    //the product urls then creating a connection to those product urls and extracts information.
    //then calls outputGUI
    public static void getInfo(String[] urls,String text,boolean logIt,Timestamp savedTime,String query){
        //I limited the results to 3 results because anything greater takes a bit too long to process
        int urls_size=urls.length; 
        if(urls_size>3) urls_size=3; 
        String domain="https://www2.hm.com";
        String info="";
        String imageURL="";
        try{
            Timestamp timestamp=null;
            if(savedTime!=null) timestamp=savedTime;
            else timestamp = new Timestamp(System.currentTimeMillis());
            if(!USER.equals("GUEST")&&logIt)   addToLogFile(text+","+timestamp);
            for(int i=0;i<urls_size;i++){
                System.out.println("LOADING RESULTS "+i);
                String inventoryName=text+" Item Number "+i;
                info=info+inventoryName+"\n";
                info=info+"username: "+USER+"\n";
                String url=domain+urls[i];
                BufferedReader reader=read(url);
                String line=reader.readLine();
                String file="";
                while(line!=null){
                    file=file+line+"\n";
                    line=reader.readLine();
                }
                int start_position=file.indexOf("@context");
                int end_position=file.indexOf("}]}");
                file=file.substring(start_position,end_position);
                String[] temp=file.split("\n");
                Data current=new Data();
                current.setInventoryName(inventoryName);
                current.setUsername(USER);
                String name="";
                String color="";
                String description="";
                String price="";
                for(int j=0;j<temp.length;j++){
                    int beginning=temp[j].indexOf('"');
                    int end=temp[j].indexOf('"',beginning+1);
                    String category=temp[j].substring(beginning+1,end);
                    if(category.equals("name")){
                        int categoryIndex=temp[j].indexOf(':');
                        int start_index=temp[j].indexOf('"',categoryIndex);
                        int end_index=temp[j].indexOf('"',start_index+1);
                        name=temp[j].substring(start_index+1,end_index);
                        info=info+"name: "+name+"\n";
                        current.setName(name);
                    }
                    else if(category.equals("color")){
                        int categoryIndex=temp[j].indexOf(':');
                        int start_index=temp[j].indexOf('"',categoryIndex);
                        int end_index=temp[j].indexOf('"',start_index+1);
                        color=temp[j].substring(start_index+1,end_index);
                        info=info+"color: "+color+"\n";
                        current.setColor(color);
                    }
                    else if(category.equals("description")){
                        int categoryIndex=temp[j].indexOf(':');
                        int start_index=temp[j].indexOf('"',categoryIndex);
                        int end_index=temp[j].indexOf('"',start_index+1);
                        description=temp[j].substring(start_index+1,end_index);
                        info=info+"description: "+description+"\n";
                        current.setDescription(description);
                    }
                    else if(category.equals("price")){
                        int categoryIndex=temp[j].indexOf(':');
                        int start_index=temp[j].indexOf('"',categoryIndex);
                        int end_index=temp[j].indexOf('"',start_index+1);
                        price=temp[j].substring(start_index+1,end_index);
                        info=info+"price: "+price+"\n\n";
                        current.setPrice(price);
                    }
                    else if(category.equals("image")){
                        int categoryIndex=temp[j].indexOf(':');
                        int start_index=temp[j].indexOf('"',categoryIndex);
                        int end_index=temp[j].indexOf('"',start_index+1);
                        imageURL=imageURL+temp[j].substring(start_index+1,end_index)+" ";
                        current.setImage(imageURL);
                        Timestamp newTime=new Timestamp(timestamp.getTime()+i);
                        if(!USER.equals("GUEST")) storage.put(newTime,current);
                    }
                }
            }
            System.out.println("DONE SEARCHING");
            outputGUI(info,imageURL,logIt,query);
            if(!USER.equals("GUEST")) saveInfo(info);
            
        }
        catch(Exception e){
            System.out.println(e);
        }
    }

    //This function creates a GUI and displays the information that was obtained from the searched query
    //and also download images
    public static void outputGUI(String text,String imageURL,boolean logIt,String query){
        if(logIt) System.out.println("SHOWING RESULTS");
        JFrame outputFrame=new JFrame("HM RESULTS");
        JPanel outputPanel=new JPanel();
        JTextArea output=new JTextArea(text);
        JScrollPane scroll=new JScrollPane(outputPanel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        outputFrame.setSize(500,500);
        outputFrame.setLocation(400,100);
        outputPanel.add(output);
        String[] temp=imageURL.split(" ");
        
        for(int i=0;i<temp.length;i++){
            int index=i;
            JButton button=new JButton("DOWNLOAD IMAGE ITEM: "+i);
            outputPanel.add(button);
            //This button handles downloading images
            button.addActionListener(new ActionListener(){
                public void actionPerformed(ActionEvent e){
                    try{
                        //File outputImageFile=new File(findName(text,index));
                        String imageFileName=query+" "+index;
                        File outputImageFile=new File(imageFileName);
                        URL url=new URL("http:"+temp[index]);
                        fetchImageFromURL(url);
                        ImageIO.write(image, "jpg", outputImageFile);
                        System.out.println("IMAGE DOWNLOADED");
                        showImage(imageFileName);
                    }
                    catch(Exception error){
                        System.out.println(error);
                    }
                }
            });
        }
        outputFrame.add(scroll);
        if(logIt) outputFrame.setVisible(true);
        else outputFrame.setVisible(false);
        outputFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    //this function shows the downloaded image onto a GUI
    public static void showImage(String imageFileName){
        JFrame frame=new JFrame("DISPLAYING IMAGE");
        JPanel panel=new JPanel();
        ImageIcon image=new ImageIcon(imageFileName);
        JLabel label=new JLabel(imageFileName);
        JScrollPane scroll=new JScrollPane(panel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

        frame.setSize(500,500);
        frame.setLocation(400,100);
        label.setIcon(image);

        panel.add(label);
        frame.add(scroll);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    //This function launches a GUI that lets you input a starting date and an ending date that you want to
    //obtain information from the storage then calls search storage
    public static void searchStorageGUI(){
        JFrame frame=new JFrame("HM SEARCH STORAGE");
        JPanel panel=new JPanel();
        JLabel startLabel=new JLabel("ENTER START DATE");
        JLabel endLabel=new JLabel("ENTER END DATE");
        JTextField startDate=new JTextField(30);
        JTextField endDate=new JTextField(30);
        JButton enter=new JButton("search");

        frame.setSize(500,500);
        frame.setLocation(400,100);
        startLabel.setLabelFor(startDate);
        endLabel.setLabelFor(endDate);
        panel.add(startLabel);
        panel.add(startDate);
        panel.add(endLabel);
        panel.add(endDate);
        panel.add(enter);
        enter.addActionListener(new ActionListener(){
            public void actionPerformed(ActionEvent e){
                String start=startDate.getText();
                String end=endDate.getText();
                searchStorage(start,end);
            }
        });

        frame.add(panel);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
    }

    //This function searches the storage that was searched betweem the two given dates then calls 
    //outputsearchstorageGUI
    public static void searchStorage(String start,String end){
        try{
            String info="";
            Date startDate=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(start);
            Date endDate=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(end);
            Iterator<Timestamp> it=storage.keySet().iterator();
            while(it.hasNext()){
                Timestamp tempKey=it.next();
                Data tempData=storage.get(tempKey);
                if(tempKey.compareTo(startDate)>0&&tempKey.compareTo(endDate)<0){
                    if(tempData.getUsername().equals(USER)||USER.equals("ADMIN"))
                        info=info+tempData.getInfo(); 
                }
            }
            outputSearchStorageGUI(info);
        }
        catch(Exception e){
            System.out.println("INVALID DATE");
            System.out.println("DATE MUST BE OF FORMAT: dd-MMM-yyyy HH:mm:ss");
        }
    }

    //This function outputs the information that was obtained from searching the storage onto a GUI
    public static void outputSearchStorageGUI(String info){
        JFrame frame=new JFrame("HM SEARCH STORAGE OUTPUT");
        JPanel panel=new JPanel();

        JTextArea output=new JTextArea(info);
        JScrollPane scroll=new JScrollPane(panel,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);


        frame.setSize(500,500);
        frame.setLocation(400,100);
        panel.add(output);
        frame.add(scroll);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

    }

    //This function is used when re building the data from a back up file. It handles logs that was 
    //logged by modify storage command
    public static void listenToCommands(String input){
        String[] information=input.split(",");
        Data temp=new Data();
        if(information[0].equals("ADD")){
            temp.setUsername(USER);
            temp.setInventoryName(information[1]);
            temp.setName(information[2]);
            temp.setColor(information[3]);
            temp.setDescription(information[4]);
            temp.setPrice(information[5]);
            temp.setImage(information[6]);
            Timestamp time=new Timestamp(System.currentTimeMillis());
            storage.put(time,temp);
        }
        else if(information[0].equals("DELETE")){
            try{
                Vector<Timestamp> removeItems=new Vector<Timestamp>();
                String startDate=information[1];
                String endDate=information[2];
                Date start=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(startDate);
                Date end=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(endDate);
                Iterator<Timestamp> storage_it=storage.keySet().iterator();
                while(storage_it.hasNext()){
                    Timestamp tempKey=storage_it.next();
                    Data tempValue=storage.get(tempKey);
                    if(tempKey.compareTo(start)>0&&tempKey.compareTo(end)<0){
                        if(tempValue.getUsername().equals(USER)||USER.equals("ADMIN")){
                            removeItems.add(tempKey);
                        }
                    }
                }
                Iterator<Timestamp> removeItems_it=removeItems.iterator();
                while(removeItems_it.hasNext()){
                    storage.remove(removeItems_it.next());
                }
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        else if(information[0].equals("MODIFY")){
            String inventoryName=information[1];
            String category=information[2];
            String newValue=information[3];
            Data replaceData=null;
            Timestamp replaceKey=null;
            Iterator<Timestamp> it=storage.keySet().iterator();
            while(it.hasNext()){
                Timestamp tempKey=it.next();
                Data tempValue=storage.get(tempKey);
                if(tempValue.getInventoryName().equals(inventoryName)){
                    replaceKey=tempKey;
                    if(category.equals("username")) tempValue.setUsername(newValue);
                    else if(category.equals("name")) tempValue.setName(newValue);
                    else if(category.equals("color")) tempValue.setColor(newValue);
                    else if(category.equals("description")) tempValue.setDescription(newValue);
                    else if(category.equals("price")) tempValue.setPrice(newValue);
                    else if(category.equals("image")) tempValue.setImage(newValue);
                    else System.out.println("INVALID CATEGORY");
                    replaceData=tempValue;
                }
            }
            storage.remove(replaceKey);
            storage.put(replaceKey,replaceData);
        }
    }

    //This function lets users input command and modify data from the storage. The commands are ADD,DELETE,
    //MODIFY, and PRINT. The formats are given in the function below
    //To stop modify search, type in "EXIT"
    public static void listenToCommands(){
        if(USER.equals("GUEST")) System.out.println("ACCESS DENIED");
        else{
            try{
                while(true){
                    System.out.print("MODIFY STORAGE: ");
                    BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
                    String input=reader.readLine();
                    if(input.equals("EXIT")) break;
                    //ADD COMMAND MUST BE OF FORMAT ADD,inventoryname,name,color,description,price,image
                    //EACH PARAMETER MUST BE SEPARATED BY A COMMA
                    else if(input.indexOf("ADD")!=-1){
                        String[] information=input.split(",");
                        if(information.length<7||information.length>7){
                            System.out.println("TOO FEW OR TOO MANY ARGUMENTS");
                            System.out.println("FORMAT MUST BE: ADD,inventoryname,name,color,description,price,image");
                        }
                        else{
                            Data temp=new Data();
                            temp.setUsername(USER);
                            temp.setInventoryName(information[1]);
                            temp.setName(information[2]);
                            temp.setColor(information[3]);
                            temp.setDescription(information[4]);
                            temp.setPrice(information[5]);
                            temp.setImage(information[6]);
                            Timestamp time=new Timestamp(System.currentTimeMillis());
                            storage.put(time,temp);
                            addToLogFile(input);
                        }
                    }
                    //DELETE COMMAND MUST BE OF FORMAT DELETE,Start Date,End Date
                    //DATES MUST BE IN FORMAT dd-MMM-yyyy HH:mm:ss
                    //EACH PARAMETER MUST BE SEPARATED BY A COMMA
                    else if(input.indexOf("DELETE")!=-1){
                        String[] information=input.split(",");
                        Vector<Timestamp> removeItems=new Vector<Timestamp>();
                        if(information.length>3||information.length<3){
                            System.out.println("INVALID FORMAT");
                            System.out.println("MUST BE OF FORMAT: DELETE,Start Date,End Date");
                        }
                        else{
                            String startDate=information[1];
                            String endDate=information[2];
                            try{
                                Date start=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(startDate);
                                Date end=new SimpleDateFormat("dd-MMM-yyyy HH:mm:ss").parse(endDate);
                                Iterator<Timestamp> storage_it=storage.keySet().iterator();
                                while(storage_it.hasNext()){
                                    Timestamp tempKey=storage_it.next();
                                    Data tempValue=storage.get(tempKey);
                                    if(tempKey.compareTo(start)>0&&tempKey.compareTo(end)<0){
                                        if(tempValue.getUsername().equals(USER)||USER.equals("ADMIN")){
                                            removeItems.add(tempKey);
                                        }
                                    }
                                }
                                Iterator<Timestamp> removeItems_it=removeItems.iterator();
                                while(removeItems_it.hasNext()){
                                    storage.remove(removeItems_it.next());
                                }
                                addToLogFile(input);
                            }
                            catch(Exception e){
                                System.out.println("INVALID DATE");
                                System.out.println("DATE MUST BE OF FORMAT: dd-MMM-yyyy HH:mm:ss");
                            }
                        }
                    }
                    //MODIFY COMMAND MUST BE OF FORMAT MODIFY,Inventoryname,category,new value
                    //EACH PARAMETER MUST BE SEPARATED BY A COMMA
                    else if(input.indexOf("MODIFY")!=-1){
                        boolean access=true;
                        String[] instructions=input.split(",");
                        if(instructions.length<4||instructions.length>4){
                            System.out.println("INVALID FORMAT");
                            System.out.println("MUST BE OF FORMAT: MODIFY,Inventoryname,category,value");
                        }
                        else{
                            String inventoryName=instructions[1];
                            String category=instructions[2];
                            String newValue=instructions[3];
                            Data replaceData=null;
                            Timestamp replaceKey=null;
                            Iterator<Timestamp> it=storage.keySet().iterator();
                            while(it.hasNext()){
                                Timestamp tempKey=it.next();
                                Data tempValue=storage.get(tempKey);
                                if(tempValue.getInventoryName().equals(inventoryName)){
                                    if(tempValue.getUsername().equals(USER)||USER.equals("ADMIN")){
                                        replaceKey=tempKey;
                                        if(category.equals("username")) tempValue.setUsername(newValue);
                                        else if(category.equals("name")) tempValue.setName(newValue);
                                        else if(category.equals("color")) tempValue.setColor(newValue);
                                        else if(category.equals("description")) tempValue.setDescription(newValue);
                                        else if(category.equals("price")) tempValue.setPrice(newValue);
                                        else if(category.equals("image")) tempValue.setImage(newValue);
                                        else System.out.println("INVALID CATEGORY");
                                        replaceData=tempValue;
                                    }
                                    else{
                                        System.out.println("YOU DO NOT HAVE ACCESS TO THIS ITEM");
                                        access=false;
                                    }
                                }
                            }
                            if(replaceData==null){
                                if(access) System.out.println("INVENTORY NAME DOES NOT EXIST");
                            }
                            else{
                                storage.remove(replaceKey);
                                storage.put(replaceKey,replaceData);
                                addToLogFile(input);
                            }
                        }
                    }
                    //PRINT COMMAND
                    else if(input.indexOf("PRINT")!=-1){
                        if(USER.equals("ADMIN")){
                            Iterator<Data> it=storage.values().iterator();
                            while(it.hasNext()){
                                it.next().print();
                            }
                        }
                        else{
                            System.out.println("YOU DO NOT HAVE ACCESS TO THIS COMMAND");
                        }
                    }
                    else System.out.println("UNKNWON COMMAND");
                }//end of while
            }//end of try
            catch(Exception e){
                System.out.println(e);
            }
            //must update back up storage after every change in the hash map storage
            updateStorage();
        }
    }

    //ALL FUNCTIONS FROM HERE ARE HELPER METHODS

    //This function saves the information of items onto a back up storage
    public static void saveInfo(String info){
        try{
            FileWriter writer=new FileWriter("./storage",true);
            writer.write(info);
            writer.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    
    //This function returns the urls of each product
    public static String[] getUrls(String text){
        String[] temp=text.split(" ");
        for(int i=0;i<temp.length;i++){
            if(temp[i].indexOf("productpage")!=-1){
                int start_position=temp[i].indexOf('"');
                int end_position=temp[i].indexOf('"',start_position+1);
                temp[i]=temp[i].substring(start_position+1,end_position);
            }
        }
        int numberOfResults=getNumberOfResults(text);
        String[] output=new String[numberOfResults];
        int count=0;
        for(int i=0;i<temp.length;i++){
            if(temp[i].indexOf("productpage")!=-1&&count<numberOfResults){
                if(count==0){
                    output[count]=temp[i];
                    count++;
                }
                else{
                    if(notDuplicate(output,temp[i],count)){
                        output[count]=temp[i];
                        count++;
                    }
                }
            }
        }
        return output;
    }

    //This function returns the name of each item
    public static String findName(String text,int c){
        System.out.println(text);
        System.out.println(c);
        String name="";
        int counter=0;
        String[] temp=text.split("\n");
        for(int i=0;i<temp.length;i++){
            if(temp[i].indexOf("username")==-1&&temp[i].indexOf("name")!=-1) counter++;
            if(counter==c){
                int start_index=temp[i].indexOf(" ");
                name=temp[i].substring(start_index+1);
                return name;
            }
        }
        return name;
    }
    
    //This function checks whether the product url is already in the array 
    public static boolean notDuplicate(String[] output,String current,int count){
        for(int i=0;i<count;i++){
            if(current.equals(output[i])) return false;
        }
        return true;
    }
    
    //This function returns the number of total results that was found from the search using the inputted query
    public static int getNumberOfResults(String output){
        int numberOfResults;
        int indexOfText=output.indexOf("data-items-shown");
        if(indexOfText==-1) return 0;
        String text=output.substring(indexOfText);
        int startIndex=text.indexOf('"');
        int endIndex=text.indexOf('"',startIndex+1);
        numberOfResults=Integer.parseInt(text.substring(startIndex+1,endIndex));
        return numberOfResults;
    }

    //This function records the action onto a transaction log
    public static void addToLogFile(String input){
        try{
            FileWriter writer=new FileWriter("./transactionlog",true);
            writer.write(input+"\n");
            writer.close();

        }
        catch(Exception e ){
            System.out.println(e);
        }
    }

    //This function formats dates in a way that can be converted to Timestamp
    public static String formatDate(String date){
        String[] info=date.split("-");
        String month="";
        if(info[1].equals("01")) month="Jan"; 
        else if(info[1].equals("02")) month="Feb";
        else if(info[1].equals("03")) month="Mar";
        else if(info[1].equals("04")) month="Apr";
        else if(info[1].equals("05")) month="Mar";
        else if(info[1].equals("06")) month="Jun";
        else if(info[1].equals("07")) month="Jul";
        else if(info[1].equals("08")) month="Aug";
        else if(info[1].equals("09")) month="Sep";
        else if(info[1].equals("10")) month="Oct";
        else if(info[1].equals("11")) month="Nov";
        else if(info[1].equals("12")) month="Dec";
        info[1]=month;
        String year=info[0];
        String day=info[2].substring(0,2);
        String time=info[2].substring(2);
        info[0]=day;
        info[2]=year+time;
        return String.join("-",info);
    }

    //This function updates the storage using the Hashmap as the source
    public static void updateStorage(){
        try{
            FileWriter writer=new FileWriter("./storage");
            Iterator<Data> it=storage.values().iterator();
            while(it.hasNext()){
                writer.write(it.next().getInfo()+"\n");
            }
            writer.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
    }
    //Flanagan Code From Class Notes
    // getURLInputStream
    public static InputStream getURLInputStream(String sURL) throws Exception {
        URLConnection oConnection = (new URL(sURL)).openConnection();
        oConnection.setRequestProperty("User-Agent", USER_AGENT);
        return oConnection.getInputStream();

    }

    //Flanagan Code From Class Notes
    // read
    public static BufferedReader read(String url) throws Exception {

            InputStream content = (InputStream)getURLInputStream(url);
            return new BufferedReader (new InputStreamReader(content));

    }

    //Flanagan Code From Class Notes
    // fetchImageFromURL
    public static void fetchImageFromURL (URL url) {
		try {
		// Read from a URL
		image = ImageIO.read(url);
		} catch (IOException e) {
		} // catch

    }

    //****************Similar methods for differnt return types************************
    //Functions from here on are used for the main method for the input parameter usage

    //This function returns all of the URLS from the searched query
    public static String[] getRedirectURL(String text){
        String url="https://www2.hm.com/en_us/search-results.html?q=";
        String query="";
        String[] urls=null;
        for(int i=0;i<text.length();i++){
            if(text.charAt(i)==' ') query=query+'+';
            else query=query+text.charAt(i);
        }
        url=url+query;
        try{
            BufferedReader reader=read(url);
            FileWriter writer=new FileWriter("./CommandLineHTML");
            String output="";
            String line=reader.readLine();
            while(line!=null){
                output=output+line;
                line=reader.readLine();
            }
            //if no results are found
            if(output.indexOf("NO MATCHING ITEMS")!=-1){
                System.out.println("No RESULTS FOUND");
            }
            //if results are found
            else{
                int start_position=output.indexOf("Product items");
                int end_position=output.indexOf("data-total");
                output=output.substring(start_position,end_position);
                writer.write(output);
                writer.close();
                int numberOfResults=getNumberOfResults(output);
                System.out.println("NUMBER OF RESULTS FOUND: "+numberOfResults);
                //create connection and output information
                urls=getUrls(output);
                //getInfo(urls);
            }
        }
        catch(Exception e){
            System.out.println(e);
        }
        return urls;
    }
    
    //This function returns the information from the URLS
    public static String getInformation(String[] urls,String text,boolean justPrintAndTerminate){
        //I limited the results to 3 results because anything greater takes a bit too long to process
        int urls_size=urls.length; 
        if(urls_size>3) urls_size=3; 
        String domain="https://www2.hm.com";
        String info="";
        String imageURL="";
        try{
            for(int i=0;i<urls_size;i++){
                String inventoryName=text+" Item Number "+i;
                info=info+inventoryName+"\n";
                info=info+"username: "+USER+"\n";
                String url=domain+urls[i];
                Data current=new Data();
                current.setInventoryName(inventoryName);
                BufferedReader reader=read(url);
                String line=reader.readLine();
                String file="";
                while(line!=null){
                    file=file+line+"\n";
                    line=reader.readLine();
                }
                int start_position=file.indexOf("@context");
                int end_position=file.indexOf("}]}");
                file=file.substring(start_position,end_position);
                String[] temp=file.split("\n");
                String name="";
                String color="";
                String description="";
                String price="";
                for(int j=0;j<temp.length;j++){
                    int beginning=temp[j].indexOf('"');
                    int end=temp[j].indexOf('"',beginning+1);
                    String category=temp[j].substring(beginning+1,end);
                    if(category.equals("name")){
                        int categoryIndex=temp[j].indexOf(':');
                        int start_index=temp[j].indexOf('"',categoryIndex);
                        int end_index=temp[j].indexOf('"',start_index+1);
                        name=temp[j].substring(start_index+1,end_index);
                        info=info+"name: "+name+"\n";
                        current.setName(name);
                    }
                    else if(category.equals("color")){
                        int categoryIndex=temp[j].indexOf(':');
                        int start_index=temp[j].indexOf('"',categoryIndex);
                        int end_index=temp[j].indexOf('"',start_index+1);
                        color=temp[j].substring(start_index+1,end_index);
                        info=info+"color: "+color+"\n";
                        current.setColor(color);
                    }
                    else if(category.equals("description")){
                        int categoryIndex=temp[j].indexOf(':');
                        int start_index=temp[j].indexOf('"',categoryIndex);
                        int end_index=temp[j].indexOf('"',start_index+1);
                        description=temp[j].substring(start_index+1,end_index);
                        info=info+"description: "+description+"\n";
                        current.setDescription(description);
                    }
                    else if(category.equals("price")){
                        int categoryIndex=temp[j].indexOf(':');
                        int start_index=temp[j].indexOf('"',categoryIndex);
                        int end_index=temp[j].indexOf('"',start_index+1);
                        price=temp[j].substring(start_index+1,end_index);
                        info=info+"price: "+price+"\n\n";
                        current.setPrice(price);
                    }
                    else if(category.equals("image")){
                        int categoryIndex=temp[j].indexOf(':');
                        int start_index=temp[j].indexOf('"',categoryIndex);
                        int end_index=temp[j].indexOf('"',start_index+1);
                        imageURL=imageURL+temp[j].substring(start_index+1,end_index)+" ";
                        current.setImage(imageURL);
                    }   
                }
                if(!justPrintAndTerminate){
                    Timestamp timeNow=new Timestamp(System.currentTimeMillis());
                    storage.put(timeNow,current);
                }
            }
            
        }
        catch(Exception e){
            System.out.println(e);
        }
        return info;
    }
    public static void main(String[]args){
        //This program accepts 4 or 5 parameters. Like so
        //-i input -o output or -i input -o output -p
        //The -p will only output the results from the input file onto the output file and terminate.
        //otherwise the program will launch the GUI's and all of its functions
        String inputName="";
        String outputName="";
        boolean justPrintAndTerminate=false;
        for(int i=0;i<args.length;i++){
            if(args[i].equals("-i")) inputName=args[i+1];
            else if(args[i].equals("-o")) outputName=args[i+1];
            else if(args[i].equals("-p")) justPrintAndTerminate=true;
        }

        try{
            BufferedReader reader=new BufferedReader(new FileReader("./"+inputName));
            FileWriter writer=new FileWriter("./"+outputName,true);
            String line=reader.readLine();
            while(line!=null){
                String urls[]=getRedirectURL(line);
                String result=getInformation(urls,line,justPrintAndTerminate);
                writer.write(result);
                if(!justPrintAndTerminate){
                    addToLogFile(line);
                    saveInfo(result);
                }
                line=reader.readLine();
            }
            reader.close();
            writer.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
        //only start the GUI if -p was not given
        if(!justPrintAndTerminate) new Mini_Software();
    }
}
