/* Application that allows to execute shell scripts 
 * in remote machines through telnet.
 * by cipher.org.uk
 * 
 */

//Import Network and IO libs
import java.io.*;
import java.net.*;

public class remotesh{

static String username_;
static String password_;
static String script_;
public static void main(String [] args){
	
//======================CHECK Command Line Arguments===================
//USAGE: java remotesh Host Port Username Password Script
try{username_=args[2];password_=args[3];script_=args[4]; } 
catch(ArrayIndexOutOfBoundsException e) 
	{ 
	System.out.println("USAGE: java remotesh Host Port Username Password Script");
	System.exit(0);
	}
//=====================================================================

try{
     //==============================
     //xxxxxxxxopen connectionxxxxxxx
     //==============================
     Socket sock = new Socket(args[0],args.length<2 ? 23 : Integer.parseInt(args[1]));
     InputStream in = sock.getInputStream();
     BufferedOutputStream os = new BufferedOutputStream(sock.getOutputStream());
        //Checking variable
        //========================
     		int AICcount = 0;   
     		int    count = 0;
		int[] sendB = new int[3];
		String[]  sa = new String[3];
		String ss = "";
		int passCheck = 0;
		int logCheck=0;
		int watch=0;
		int count1=0;
	//<Big Loop>/if (connection)
	//==============================
   	while(true){		  	
   	  AICcount = 0; count = 0; //Clear Variables
		//Login              
              	if	((ss.lastIndexOf("login:")!=1)&logCheck==0) 
              	        {send(username_+"\r",os);logCheck=1;}              
              	else if ((ss.lastIndexOf("Password:")!=-1)&passCheck==0) 
              		{send(password_+"\r",os);passCheck=1; }
              	//Send Script to remote host
              	else if(logCheck==1 & passCheck==1) { 
              		String thisLine;
              		try {
          		FileInputStream fin =  new FileInputStream(script_);
          		BufferedReader myInput = new BufferedReader(new InputStreamReader(fin));
          			while ((thisLine = myInput.readLine()) != null) 
          				{  send(thisLine+"\r",os);}
          			}
       			catch (Exception e) {System.out.println("Your Script File is having problem");}
              		send("exit\r",os);
              		}
   		//Send
   		//====
   		 if(sa[2]!=null & sa[1]!=null & sa[0]!=null) {
   			//Watch Negotiation / IF YOU WANT TO WATCH TELNET NEGOTIATION SET watch==0
   			if(watch==1){System.out.println("Server:"+sa[0]+","+sa[1]+","+sa[2]);
   				     System.out.println("Client:"+sendB[0]+","+sendB[1]+","+sendB[2]);}
   			           //Send Reply   
   			           //===========
   		 	   	   send(sendB[0],os);
   		 	   	   send(sendB[1],os);
   		 	   	   send(sendB[2],os);}
              	//Clear All 
              	sendB[0]=-1;sendB[1]=-1;sendB[2]=-1;
              	sa[0]=null;sa[1]=null;sa[2]=null;
              
		//======================================
     		//xxxxxxxxxxxNegotiation bitchxxxxxxxxxx
     		//======================================
     		//If you want to Understand the codes of Telnet negotiation read the RFC
   		while(count<1) {
   			int b = in.read();if (b==-1)System.exit(0); 
			/*================================================
			  Neg COMMAND Response  WILL -> DONT / 251 - > 254
			              		DO   -> WONT / 253 - > 252
			        		WONT -> DONT / 252 - > 254
			        		DONT -> WONT / 254 - > 252 
		          ================================================*/    
		    if ( (b >=240 & b<=255) || b==140 || b==139 
		    || b==138 || b==1 || b==24|| b==35 || b==39 ||  b==36 ||b==3|| b==31|| b==5|| b==33 || b==32) {
			    	if(b==255) { AICcount++; sa[0] = "255";sendB[0]=255;}
			    	
			    	 else {    
				     	     if(b==251) { sa[1]= "251"; sendB[1]=254;}
					else if(b==252) { sa[1]= "252"; sendB[1]=254;}
					else if(b==253) { sa[1]= "253"; sendB[1]=252;}
					else if(b==254) { sa[1]= "254"; sendB[1]=252;}
					else            { sa[2]= b+""  ; sendB[2]=b;count++;if(b==32){ss +=(char)b;System.out.print((char)b);}  } 
					
						 }
			   }
			else {	ss +=(char)b;	System.out.print((char)b);}	
			   }
		}//</Big Loop>

	}
//Catch Any problem and through Warning
catch (IOException e){
	System.out.println("----\nThere is problem to connect with "+args[0]+"\n----");}
   
   
   }

//The following methods created so we can send Data to socket using different Data types 
private static void send(String str,BufferedOutputStream bos)
  {
    byte[] buf = new byte[str.length()];
    str.getBytes(0, str.length(), buf, 0);
    try { bos.write(buf);bos.flush(); 
    	} catch(IOException e) {}
  }
public static void send(int buff,BufferedOutputStream bos) throws IOException {	
	try { bos.write(buff);bos.flush();}
	catch (IOException e) {}
	}     
}
