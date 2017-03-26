
import java.util.*;
import java.sql.*;
import java.net.*;

class CStatus
{
	Util mUtil;
	CStatus()
	{
		mUtil=new Util();
	}

	public String DoStatus(Socket socket, String inputLine)
	{
		String Username=new String("");
		String Passwort=new String("");
		String ret;
		int i;
		i=3;
		System.out.println(inputLine);
		while(inputLine.charAt(i)!=';')
		{
			Username=Username+java.lang.Character.toString(inputLine.charAt(i));
			i++;
		}
		i++;
		
		while(inputLine.charAt(i)!=';')

		{

			Passwort=Passwort+java.lang.Character.toString(inputLine.charAt(i));

			i++;

		}

		i++;		
		System.out.println(Username);
		Users mUsers=new Users(Username);
		Sessions mSession=new Sessions(Username);
				

		if(mSession.CheckSession())
		{
			try{Thread.currentThread().sleep(1000);}catch(Exception e){}
			System.out.println("Warte auf Passwort");
			//byte[] PasswortCypheredTmp=new byte[255];
			int x=0;
			try{
				//x=socket.getInputStream().read(PasswortCypheredTmp);
			}
			catch(Exception e)
			{				
			}
			System.out.println("Passwort empfangen");
			
//			byte[] PasswortCyphered=new byte[x];
	//		for(i=0;i<x;i++)
		//		PasswortCyphered[i]=PasswortCypheredTmp[i];
			
			//String Passwort=mUtil.Decypher(PasswortCyphered, Username);
			
				
			if(mUsers.CheckUser(Passwort))
			{
				DBQuery query=new DBQuery();

				java.util.GregorianCalendar cal = new GregorianCalendar();
				java.lang.String erstanmeldung = java.lang.String.valueOf(cal.getTimeInMillis());
    				UserKarten mUserKarten=new UserKarten();
      				String Code = mUserKarten.GetCode(mUsers.GetUserID());
					
				query.DoQuery("UPDATE Sessions SET Endzeit='"+erstanmeldung+"' WHERE Username='"+Username+"'");
				query.DoQuery("UPDATE Sessions SET Active='A' WHERE Username='"+Username+"'");
				query.DoQuery("UPDATE UserKarten SET Restzeit=Restzeit-1 WHERE Code='"+Code+"'");
					
				String StatusString=GetUserDaten(Username);																									
				ret=StatusString;
			}
			else
			{
				ret="AF";
			}
		}
		else
		{
			ret="SO";
		}	
		return ret;
	}

	private String GetUserDaten(String Username)
	{
		DBQuery query=new DBQuery();

		java.util.GregorianCalendar cal = new GregorianCalendar();
		java.lang.String erstanmeldung = java.lang.String.valueOf(cal.getTimeInMillis());

		Users mUsers=new Users(Username);
		UserKarten mUserKarten=new UserKarten();
		KundenKarten mKundenKarten = new KundenKarten(Username);

		String ID = mUsers.GetUserID();
		String Code = mUserKarten.GetCode(ID);

		java.lang.String IDKarte = mUserKarten.GetIDKarte(ID);
		String ZeitKarte=mUserKarten.GetMenge(ID);
		Karten mKarten = new Karten(IDKarte);
		String restzeit=mUserKarten.GetRestzeit(ID);




		String AktuelleTimecard=mKarten.GetMenge();
                double Restzeit=Double.parseDouble(restzeit);                                                        
                if(Gesamtzeit>60)
                {
	                Gesamtzeit/=60;
	                AktuelleTimecard=Double.toString(Gesamtzeit);
              		AktuelleTimecard=AktuelleTimecard.substring(0,AktuelleTimecard.lastIndexOf(".")+2)+" Stunden";
                }
                else
                {
        	        AktuelleTimecard=Double.toString(Gesamtzeit)+" Minuten";
                }
	


                        if(Restzeit>60)
                        {
                            Restzeit/=60;
                            restzeit = java.lang.String.valueOf(Restzeit);
			    restzeit=restzeit.substring(0,restzeit.lastIndexOf(".")+2)+" Stunden";                            
                        }
			else
			{
				restzeit = java.lang.String.valueOf(Restzeit)+" Minuten";
			}





		ResultSet rs = query.DoQuery("SELECT * FROM UserKarten WHERE Code='"+Code+"'");

        	try
           	{
                	rs.first();
                 	if(rs.getString("Erstanmeldung").compareTo("0") == 0 || rs.getString("Erstanmeldung").compareTo("") == 0)
	                 	query.DoQuery("UPDATE UserKarten SET Erstanmeldung='"+erstanmeldung+"' WHERE Code='"+Code+"'");
               	}
          	catch(Exception exception2) { }				
			
		String StatusString=new String("ST;");
		StatusString=StatusString+restzeit+";"+Code+";"+AktuelleTimecard;
		StatusString=StatusString+GetRestKarten(ID);
		return StatusString;
	}

	private String GetRestKarten(String ID)
	{
		DBQuery query=new DBQuery();
		String RestKarten=new String("");
		boolean weiter=false;
		String Zeit;
		int count = 0;

		ResultSet zUserKarten = query.DoQuery("SELECT * FROM UserKarten WHERE IDUser='"+ID+"' AND Restzeit>0");
		try
		{
			zUserKarten.first();
			zUserKarten.next();
		}
		catch(Exception e){}
		do
		{
				
			try
			{					
				String IDKarte=zUserKarten.getString("IDKarte");
				Karten mKarten=new Karten(IDKarte);
				String Code=zUserKarten.getString("Code");
						
				if(mKarten.IsFlatrate())
				{
					Zeit=mKarten.GetMenge()+" Tages Flatrate";
				}
				else
				{
					Zeit=mKarten.GetMenge()+" Minuten";
				}
				count++;
				RestKarten=RestKarten+Code+";"+Zeit+";";
			}catch(Exception e){}

			try
			{
				weiter=zUserKarten.next();
			}
			catch(Exception e)
			{

				weiter=false;
			}
		}
		while(weiter);
		String StatusString=new String("");
		StatusString=StatusString+";"+String.valueOf(count)+";"+RestKarten;
		return StatusString;
	}
}