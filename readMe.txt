Instructions on how to use my program

Parameters:
My program accepts 4 or 5 parameters (the last parameter is optional).
Example:
-i input -o output -p
 0   1    2    3    4

The -i indicates that the parameter preceding is the name of the input file and -o indicates that the parameter preceding is the name of the 
output file. Both files must be in the same directory as the program because I do not ask for the path. The last parameter is optional, if the 
parameter -p is given, it will just process the input file and print results into the output file and terminate. If -p is not given, it will 
process the input file and print results to the output file and start the GUI's.
The input file must contain key words to be searched in the website line by line that the user wants to process in a batch.
The results will be shown on the output file.

Compiling:
The main program Phase3.java can be compiled and ran through command prompt:
Example:
javac Phase3.java
java Phase3 -i input -o output

GUI's:

LOGINGUI:
This program has multiple GUI's. The first one users will see is the LOGINGUI. This GUI asks users for their user ID. There are 3 types of User
ID's this program recognizes. First is the GUEST; guests can only search for items in the website and nothing more. Second is the USER; users can
be distinguished by adding any character after the word USER such as USER0, USER1, USER2, and etc. Users can do much more than the GUEST such
as all search results will be stored in a storage, download images, modify storage, and search storage. Lastly, the third is the ADMIN; admins have
all the privilages that the users do plus they can rebuild the data using a transaction log and can search and modify data in the storage that was not 
stored by them. 

SEARCHGUI:
After the user logs in, they will be directed to a SEARCHGUI. This GUI is the main GUI of this program and once this GUI is exited, it will shut down
the program. This GUI has a text field that users can type in key words and obtain information. Once the
user clicks on the button "SEARCH", another GUI will pop up containing the information based on their searched key words (HM.com displays about 40 results
per page so I limited it to 3 because anything greater takes too much time). In this GUI, there are 3 more buttons. THe first is called "MODIFY STORAGE".
When this button is clicked, users can modify information via command prompt. Second button is called "CHANGE USER". This button lets users re-login. Lastly,
the third button is called "SEARCH STORAGE". When this button is clicked, another GUI will pop up that allows users to search for information in the
storage.

OUTPUTSEARCHRESULTSGUI:
This GUI contains information that was obtained using the key words inputted through SEARCHGUI. It will have information of the first 3 results that are
obtained from the website. This GUI also has buttons with each search results that downloads image.

SEARCHSTORAGEGUI:
This GUI pops up when users click the button "SEARCH STORAGE" from the SEARCHGUI. This GUI has two text fields that lets users type in information.
The first is the starting time and the second is the ending time that the user wants to obtain information that was obtained during those time period.
The time must be of form dd-MMM-YYYY HH:MM:ss where dd is the day, MMM is the month in letter form such as Jan, YYYY is the year, HH is the hour, MM is 
the minutes, and ss is the seconds. When the button "SEARCH" is clicked, it will show another GUI that contains the information that was searched. 

MODIFY STORAGE:
This command can be used by clicking the "MODIFY STORAGE" button from the SEARCHGUI. Users can type in commands through the command line to modify the
storage. There are 4 commands and specific parameters each command accepts and each parameter must be separated by a comma. The first command is the 
ADD command. This command accepts 7 parameters, the command, inventory name, name of the item, color, description, price, and url of the product image.
Example: ADD,shorts Item Number 0, shorts, black, shorts with ripped spots, 34.99, imageURL
This command will add the item to the storage. Second command is the DELETE command.This command accepts 3 parameters, the command, starting time, ending time
Example: DELETE,04-Dec-2020 13:00:00,04-Dec-2020 15:00:00
This command will delete items that was searched during those time period that was searched by the user.
The third command is MODIFY command. This command accepts 4 parameters, the command, inventory number, the category they want to change, the new value.
Example: MODIFY,shorts Item Number 2,price,150
This command will modify the item's price to 150 that has the inventory number "shorts Item Number 2"
To stop modify search, the command "EXIT" must be inputted