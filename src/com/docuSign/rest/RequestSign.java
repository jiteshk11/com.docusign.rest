package com.docuSign.rest;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

/**
 * Servlet implementation class RequestSign
 */
@WebServlet("/RequestSign")
public class RequestSign extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * @see HttpServlet#HttpServlet()
	 */

	String integratorKey = "ETOU-074dc2ff-26c1-4215-90c5-11d32a67e81f";
	String username = "docusigndemo111@gmail.com"; // account email (or your API //											// userId)
	String password = "demo111"; // account password
	String hostName = "Jitesh Kumar"; // recipient (signer) name
	String hostEmail = "docusigndemo111@gmail.com"; // recipient (signer) email
	String signerName1 = "SignerOne";
	String signerName2 = "SignerTwo";
	String documentName = "Title.pdf"; // copy file with same name
	String docContentType = "application/pdf"; // content type for above
	String baseURL = "https://demo.docusign.net/restapi/v2/accounts/1150458"; // we will retrieve this through the Login API call
	String accountId = ""; // we will retrieve this through the Login API					// call
	String envelopeId = ""; // generated from signature request API call
	String url = ""; // end-point for each api call
	String body = ""; // request body
	String response1 = ""; // response body
	int status; // response status
	HttpURLConnection conn = null; // connection object used for each
	String authenticationHeader = "<DocuSignCredentials>" + "<Username>" + username + "</Username>" + "<Password>"
			+ password + "</Password>" + "<IntegratorKey>" + integratorKey + "</IntegratorKey>"
			+ "</DocuSignCredentials>";

	public RequestSign() {
		super();
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		if(request.getParameter("signerName1") != null && request.getParameter("signerName2") != null){
				 signerName1=request.getParameter("signerName1");
				 signerName2=request.getParameter("signerName2");
		}
		
		url = baseURL + "/envelopes"; // append "/envelopes" to baseUrl for

		body = "<envelopeDefinition xmlns=\"http://www.docusign.com/restapi\">"
				+ "<emailSubject>API Call for In_Person signature</emailSubject>" + "<documents>" + "<document>"
				+ "<documentId>1</documentId>" + "<name>" + documentName + "</name>" + "</document>" + "</documents>"
				+ "<recipients>" +"<autoNavigation>true</autoNavigation>"+ "<inPersonSigners>" +
				"<autoNavigation>true</autoNavigation>"+
				"<inPersonSigner>"
				+ "<embeddedRecipientStartURL>SIGN_AT_DOCUSIGN</embeddedRecipientStartURL>"
				+"<autoNavigation>true</autoNavigation>"
				+ "<clientUserId>1001</clientUserId>" + "<hostEmail>" + hostEmail + "</hostEmail>" + "<hostName>"
				+ hostName + "</hostName>" + "<recipientId>1</recipientId>"			
				+ "<signerName>" + signerName1 + "</signerName>" + "<routingOrder>1</routingOrder>"
				+ "<autoNavigation>true</autoNavigation>" + "<tabs>" + "<signHereTabs>" + "<signHere>"
				+ "<xPosition>200</xPosition>" + "<yPosition>250</yPosition>" + "<documentId>1</documentId>"
				+ "<pageNumber>1</pageNumber>" + "</signHere>" + "</signHereTabs>" + "</tabs>" + "</inPersonSigner>"
				
				+ "<inPersonSigner>" + "<embeddedRecipientStartURL>SIGN_AT_DOCUSIGN</embeddedRecipientStartURL>"
				+"<autoNavigation>true</autoNavigation>"
				+ "<clientUserId>1002</clientUserId>" + "<hostEmail>" + hostEmail + "</hostEmail>" + "<hostName>"
				+ hostName + "</hostName>" + "<recipientId>2</recipientId>" 
				+ "<signerName>" + signerName2 + "</signerName>" + "<routingOrder>2</routingOrder>"
				+ "<autoNavigation>true</autoNavigation>" + "<tabs>" + "<signHereTabs>" + "<signHere>"
				+ "<xPosition>400</xPosition>" + "<yPosition>250</yPosition>" + "<documentId>1</documentId>"
				+ "<pageNumber>1</pageNumber>" + "</signHere>" + "</signHereTabs>" + "</tabs>" + "</inPersonSigner>"
				
				+ "<inPersonSigner>" + "<embeddedRecipientStartURL>SIGN_AT_DOCUSIGN</embeddedRecipientStartURL>"
				+ "<clientUserId>1003</clientUserId>"
				+ "<hostEmail>" + hostEmail + "</hostEmail>" + "<hostName>" + hostName + "</hostName>"
				+ "<recipientId>3</recipientId>"// + "<name>" + hostName + "</name>" + "<email>" + hostEmail + "</email>"
				+ "<signerName>" + hostName + "</signerName>" + "<routingOrder>3</routingOrder>"
				+ "<autoNavigation>true</autoNavigation>" 
				+ "<tabs>" + "<signHereTabs>" + "<signHere>" + "<xPosition>300</xPosition>" + "<yPosition>350</yPosition>"
				+ "<documentId>1</documentId>" + "<pageNumber>1</pageNumber>" + "</signHere>" + "</signHereTabs>"
				+ "</tabs>" + "</inPersonSigner>" + "</inPersonSigners>" 
				+ "</recipients>" + " <status>sent</status>" + "</envelopeDefinition>";
	
		conn = InitializeRequest(url, "POST", body, authenticationHeader);

		// read document content into byte array
		File file = new File("C:\\" + documentName);
		InputStream inputStream = new FileInputStream(file);
		byte[] bytes = new byte[(int) file.length()];
		inputStream.read(bytes);
		inputStream.close();

		String requestBody = "\r\n\r\n--BOUNDARY\r\n" + "Content-Type: application/xml\r\n"
				+ "Content-Disposition: form-data\r\n" + "Content-Length:" + Integer.toString(body.length()) + "\r\n"
				+ "\r\n" + body + "\r\n\r\n--BOUNDARY\r\n" + "Content-Type: " + docContentType + "\r\n" + "Content-Disposition: file; filename=\"" + documentName
				+ "\"; documentid=1\r\n" + "\r\n";
	
		String reqBody2 = "\r\n" + "--BOUNDARY--\r\n\r\n";

		// write the body of the request...
		DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
		dos.writeBytes(requestBody.toString());
		dos.write(bytes);
		dos.writeBytes(reqBody2.toString());
		dos.flush();
		dos.close();

		status = conn.getResponseCode(); // triggers the request
		if (status != 201) // 201 = Created
		{
			errorParse(conn, status);
			return;
		}

		// display the response body
		response1 = getResponseBody(conn);
		envelopeId = parseXMLBody(response1, "envelopeId");
		//System.out.println("-- Signature Request response --\n\n" + prettyFormat(response1, 2));
		System.out.println(envelopeId );
	
		doPost(request,response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		  
		   //doGet(request, response);	
			response.setContentType("text/html");
	        PrintWriter out = response.getWriter();
	        out.println("<html>");
	        out.println("<head>");
	        out.println("<title>In-Person Sign Demo*</title>");
	        out.println("</head>");
	        out.println("<body>");
	        out.println("<h3>In-Person Sign Demo</h3>");
	        if (envelopeId != null) {
	            out.println("Envelope ID:");
	            out.println(" = " + envelopeId + "<br>");
	                       
	        } else {
	            out.println("No Parameters, Please enter some");
	        }
	        out.println("<P>");
	        out.print("<form action=\"");
	        out.print("RequestSign\" ");
	        out.println("method=POST>");
	        out.println("First Signer:");
	        out.println("<input type=text size=20 name=firstname value="+signerName1+" style='margin-right:30px;' style='margin-left:30px;'>");
	        out.println("Second Signer:");
	        out.println("<input type=text size=20 name=lastname value="+signerName2+" style='margin-right:30px;' style='margin-left:5px;'>");
	        out.println("Host:");
	        out.println("<input type=text size=20 name=firstname value="+hostName+" style='margin-right:30px;' style='margin-left:30px;'>");
	        out.println("<br>");
	        out.println("<br>");
	        out.println("<input type=submit size=20 name=act value=First style='margin-left:120px;' >");
	        out.println("<input type=submit size=20 name=act value=Second   style='margin-left:220px;' >");
	        out.println("<input type=submit size=20 name=act value=Host   style='margin-left:160px;' >");
	        out.println("<input type=submit size=20 name=act value=Show   style='margin-left:400px;' >");
	        out.println("<input type=submit size=20 name=act value=Download   style='margin-left:60px;' >");
	        out.println("</form>");
	        out.println("</P>");
	       
	        int clientUserId = 1001;
	        String act = request.getParameter("act");
	       
	        if (act == null) {
	            //no button has been selected
	        } else if (act.equals("First")) {
	        	clientUserId =1001;
	        } else if (act.equals("Second")) {
	        	clientUserId =1002;
	        } else if (act.equals("Host")) {
	        	clientUserId =1003;
	        } else if (act.equals("Download")) {
	        	downloadFile(baseURL, envelopeId );
	        }	        
	        
	        url = baseURL + "/envelopes/"+ envelopeId + "/views/recipient";	// append envelope uri + "views/recipient" to url 
			
			body = "<recipientViewRequest xmlns=\"http://www.docusign.com/restapi\">"  +
					"<authenticationMethod>email</authenticationMethod>" + 
					"<email>" + hostEmail + "</email>" + 
					"<returnUrl>http://www.docusign.com/devcenter</returnUrl>" + 
					"<clientUserId>"+clientUserId+"</clientUserId>" +	//*** must match clientUserId set in Step 2! 
					"<userName>" + hostName + "</userName>" + 
					"</recipientViewRequest>";
		
			conn = InitializeRequest2(url, "POST", body, authenticationHeader);
			status = conn.getResponseCode(); // triggers the request
			if( status != 201 )	// 201 = Created
			{
				errorParse(conn, status);
				return;
			}	
		
			String response3 = getResponseBody(conn);
			String urlToken = parseXMLBody(response3, "url");
			out.println("<iframe src="+urlToken +" width='1000' height='600'/>");
	        out.println("</body>");
	        out.println("</html>");
	       
	    }
	

	// ***********************************************************************************************
	// ***********************************************************************************************
	// --- HELPER FUNCTIONS ---
	// ***********************************************************************************************
	// ***********************************************************************************************
	public static HttpURLConnection InitializeRequest(String url, String method, String body, String httpAuthHeader) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();

			conn.setRequestMethod(method);
			conn.setRequestProperty("X-DocuSign-Authentication", httpAuthHeader);
			conn.setRequestProperty("Accept", "application/xml");
			if (method.equalsIgnoreCase("POST")) {

				conn.setRequestProperty("Content-Type", "multipart/form-data; boundary=BOUNDARY");
				conn.setRequestProperty("Content-Length", Integer.toString(body.length()));
				conn.setDoOutput(true);
			} else {

				conn.setRequestProperty("Content-Type", "application/xml");
			}
			return conn;

		} catch (Exception e) {
			throw new RuntimeException(e); // simple exception handling, please
											// review it
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	public static HttpURLConnection InitializeRequest2(String url, String method, String body, String httpAuthHeader) {
		HttpURLConnection conn = null;
		try {
			conn = (HttpURLConnection) new URL(url).openConnection();

			conn.setRequestMethod(method);
			conn.setRequestProperty("X-DocuSign-Authentication", httpAuthHeader);
			conn.setRequestProperty("Content-Type", "application/xml");
			conn.setRequestProperty("Accept", "application/xml");
			if (method.equalsIgnoreCase("POST")) {
				conn.setRequestProperty("Content-Length", Integer.toString(body.length()));
				conn.setDoOutput(true);
				// write body of the POST request
				DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
				dos.writeBytes(body);
				dos.flush();
				dos.close();
			}
			return conn;

		} catch (Exception e) {
			throw new RuntimeException(e); // simple exception handling, please
											// review it
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	public static String parseXMLBody(String body, String searchToken) {
		String xPathExpression;
		try {
			// we use xPath to parse the XML formatted response body
			xPathExpression = String.format("//*[1]/*[local-name()='%s']", searchToken);
			XPath xPath = XPathFactory.newInstance().newXPath();
			return (xPath.evaluate(xPathExpression, new InputSource(new StringReader(body))));
		} catch (Exception e) {
			throw new RuntimeException(e); // simple exception handling, please
											// review it
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	public static String getResponseBody(HttpURLConnection conn) {
		BufferedReader br = null;
		StringBuilder body = null;
		String line = "";
		try {
			// we use xPath to get the baseUrl and accountId from the XML
			// response body
			br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			body = new StringBuilder();
			while ((line = br.readLine()) != null)
				body.append(line);
			return body.toString();
		} catch (Exception e) {
			throw new RuntimeException(e); // simple exception handling, please
											// review it
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	public static void errorParse(HttpURLConnection conn, int status) {
		BufferedReader br;
		String line;
		StringBuilder responseError;
		try {
			System.out.print("API call failed, status returned was: " + status);
			InputStreamReader isr = new InputStreamReader(conn.getErrorStream());
			br = new BufferedReader(isr);
			responseError = new StringBuilder();
			line = null;
			while ((line = br.readLine()) != null)
				responseError.append(line);
			System.out.println("\nError description:  \n" + prettyFormat(responseError.toString(), 2));
			return;
		} catch (Exception e) {
			throw new RuntimeException(e); // simple exception handling, please
											// review it
		}
	}

	///////////////////////////////////////////////////////////////////////////////////////////////
	public static String prettyFormat(String input, int indent) {
		try {
			Source xmlInput = new StreamSource(new StringReader(input));
			StringWriter stringWriter = new StringWriter();
			StreamResult xmlOutput = new StreamResult(stringWriter);
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformerFactory.setAttribute("indent-number", indent);
			Transformer transformer = transformerFactory.newTransformer();
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.transform(xmlInput, xmlOutput);
			return xmlOutput.getWriter().toString();
		} catch (Exception e) {
			throw new RuntimeException(e); // simple exception handling, please
											// review it
		}
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	public  void downloadFile(String baseURL, String envelopeId) {
	
	url = baseURL + "/envelopes/" + envelopeId + "/documents";
	String body = "";	// no request body for this call
	
	// re-use connection object for second request...
	conn = InitializeRequest(url, "GET", body, authenticationHeader);
	
	
	try {
		status = conn.getResponseCode();
	} catch (IOException e1) {
	
		e1.printStackTrace();
	} // triggers the request
	if( status != 200 )	// 200 = OK
	{
		errorParse(conn, status);
		return;
	}

	String response = getResponseBody(conn);
		
	XPathFactory factory = XPathFactory.newInstance();
	XPath xpath = factory.newXPath();
	XPathExpression expr = null;
	try {
		expr = xpath.compile("//*[1]/*[local-name()='envelopeDocument']");
	} catch (XPathExpressionException e) {
	
		e.printStackTrace();
	}
	Object result = null;
	try {
		result = expr.evaluate(new InputSource(new StringReader(response.toString())), XPathConstants.NODESET);
	} catch (XPathExpressionException e) {
		
		e.printStackTrace();
	}
	
	// read document list response, parse URIs, count number of documents 
	int cnt = -1;
	NodeList documents = (NodeList) result;
	String[] docNames = new String[documents.getLength()];
	String[] docURIs = new String[documents.getLength()];
	for (int i = 0; i < documents.getLength(); i++) 
	{
		Node node = documents.item( i );
		if (node != null && node.getNodeType() == Node.ELEMENT_NODE) {
			NodeList docNodes = node.getChildNodes();
	        for (int j = 0; j < docNodes.getLength(); j++)
	        {
	        	if( docNodes.item(j).getLocalName().equals("name"))
	        	{
	        		// store each document name and increment running counter
	        		docNames[++cnt] = docNodes.item(j).getTextContent();	
	        	}
	        	if( docNodes.item(j).getLocalName().equals("uri"))
	        	{
	        		// store each document uri as well
	        		docURIs[cnt] = docNodes.item(j).getTextContent();	
	        	}
	        }
		}
	}
	
	for( int i = 0; i < docURIs.length; i++)
	{
		url = baseURL + docURIs[i];	// each document has its own unique uri
		body = "";	// no request body for this call
		
		conn = InitializeRequest(url, "GET", body, authenticationHeader);
		
		// envelope documents are always converted to PDFs in the DocuSign platform
		conn.setRequestProperty("Accept", "application/pdf");	
		
		System.out.println("Retrieving envelope document \"" + docNames[i] + "\"...\n");
		try {
			status = conn.getResponseCode();
		} catch (IOException e) {
		
			e.printStackTrace();
		} // triggers the request
		if( status != 200 )	// 200 = OK
		{
			errorParse(conn, status);
			return;
		}
		
		// request body is a byte array of PDF data, write to a local file...
		writePDFBytesToFile(conn, docNames[i], envelopeId);
	}
	System.out.println("\nDone downloading document(s)!");
}
	
	///////////////////////////////////////////////////////////////////////////////////////////////
	public static void writePDFBytesToFile(HttpURLConnection conn, String docName, String envelopeId) {
		FileOutputStream fop = null;
		File file;
		DateFormat dateFormat = new SimpleDateFormat("yyMMddHHmm");
		Date date = new Date();
	
		try {
			file = new File("C:\\download\\"+ dateFormat.format(date)+"_" +docName );
			fop = new FileOutputStream(file);
			byte[] buffer = new byte[1024 /* we write the pdf in chunks of 1024 */];
			int numRead;
			
			while((numRead = conn.getInputStream().read(buffer)) > 0) {
				fop.write(buffer, 0, numRead);
			}
			fop.flush();
			fop.close();
		} catch (Exception e) {
			throw new RuntimeException(e); // simple exception handling, please review it
		}
	}
	
}
