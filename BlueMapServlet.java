/** this servlet will attempt to serve the gzip version of a file if it exsist before serving the regular version of the file
NOTE: this probubly will not work with packed WARs
*/
import org.apache.catalina.servlets.DefaultServlet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;


public class BlueMapServlet extends DefaultServlet{
	
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		//System.out.println("my servlet was poked "+System.nanoTime());
		String path = getRelativePath(request, true);//get the path that was enterd into the browser
		//System.out.println("with path: \""+path);
		String onSystemPath= getServletContext().getRealPath(path);//get the path of where the file would be on the system if it exsists
		//System.out.println(onSystemPath);
       
		//check to see if the requested resoure is a file
		int indexOfFileType=-1;
		boolean valid=false;
		for(int i=onSystemPath.length()-1;i>1;i--){
			char ca = onSystemPath.charAt(i);
			//chekc for the . in the file
			if(ca=='.'){
				valid=true;
				indexOfFileType=i;
				break;
			}
			//if you find a path seperator before you find a . then don't bother
			if(ca=='\\' || ca=='/'){
				break;
			}
		}
		//System.out.println("is File request?: "+valid);
		
		//check to see if the .gz version of the file exsists
		if(valid){
			valid = new File(onSystemPath+".gz").exists();
			//System.out.println("found gzip:" +valid);
		}
		
		if(!valid){
			//check to see if they requested a json file
			if(onSystemPath.length()-indexOfFileType == 5){
				if(onSystemPath.substring(indexOfFileType).equals(".json")){
					if(!new File(onSystemPath).exists()){//if the file does not exsist
						response.getOutputStream().println("{}");//send an empty json in return
						//System.out.println("Sending empty json");
						return;
					}
				}
			}
		}
		
		//if the gz version of the file exists then serv that outherwise fall back to the default servlet
		if(valid){
			//System.out.println("attempting to serve gzip version");
			
			//set the http headers to let the client know the file they are reciving is compressed and will need to be decompressed
			response.setHeader("Content-Encoding","gzip");
			response.setContentType(getServletContext().getMimeType(path));
			
			//if the content is properly GZIP encodde then this will work
			try{
				FileInputStream in = new FileInputStream(onSystemPath+".gz");
				OutputStream out = response.getOutputStream();
				
				//load the file in and write it to the output
				byte[] buffer = new byte[4096];
				int length;
				while ((length = in.read(buffer)) > 0) {
					//System.out.println(length);
					out.write(buffer, 0, length);
				}
				//System.out.println(length+" f");
				
				in.close();
				out.flush();
			}catch(Exception e){
				e.printStackTrace();
				response.setStatus(500);
			}
			
		}else{
			super.doGet(request,response);
		}
		
    }
}