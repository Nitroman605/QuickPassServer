
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;

import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.fileupload.FileItemIterator;
import org.apache.commons.fileupload.FileItemStream;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.fileupload.util.Streams;


/**
 * A crappy Test servlet to test and validate file storage strategies in a
 * static/dynamic isolated web application env.
 *
 * @author Alexandre Gauthier <alex@lab.underwares.org>
 *
 */

public class NewFileUpload extends HttpServlet {

    /* FIXME: THIS SHOULD COME FROM CONFIGURATION VALUES
     * Debug constants, for tweaking. Feel free to get this from your
     * configuration system, or better yet from JNDI so the app server can
     * tell you where to place the files.
     */
    private static final String STATIC_SERVER_URL
            = "D:\\home\\";
    private static final int MAX_FILE_SIZE = 1024*512; //512 megabytes

    // Set up log4j logger
    private static final Logger logger = Logger.getLogger(
            ServletFileUpload.class.getName());

    /**
     * Process requests. Practically, handles HTTP <code>POST</code> alone,
     * because GET has no buisness being for now. It was not written in
     * <code>doPost()</code> because maybe I'll wish to call it also for
     * <code>doGet()</code> or whatever.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
       
        String code = request.getHeader("code");
        String size = request.getHeader("size");
        String email = request.getHeader("email");

        // Abort if form is not multipart
        if (!ServletFileUpload.isMultipartContent(request)) {
            return;
        }

        //  upload.setFileSizeMax(MAX_FILE_SIZE);
        response.setContentType("text/plain");

        PrintWriter out = response.getWriter();
        //String name = null;
        //Check if the code is already taken.
        if (!Functions.verify(code)) {
            out.println("1");
            return;

        } //check the file size if it exceeds the max 
        else if (Integer.parseInt(size) > MAX_FILE_SIZE) {
            out.write("2");
            return;
        } else {
            //establishing the special directory for the file
            String dirID = Functions.getLastID();
            String finalDir = STATIC_SERVER_URL + dirID;

            //Creating the folder for the file .
            File uploadDirectory = new File(finalDir);
            if (!uploadDirectory.exists()) {
                uploadDirectory.mkdirs();
            }
            ServletFileUpload upload;   // upload object
            FileItemIterator fit;       // Iterator for uploaded file(s)
            upload = new ServletFileUpload();
            upload.setFileSizeMax(1073741824);
            String fileName = "";
            try {
                fit = upload.getItemIterator(request);
                // Process any file(s) resulting from request.
                while (fit.hasNext()) {
                    FileItemStream item = fit.next();
                    String name = item.getFieldName();
                    InputStream stream = item.openStream();
                    fileName = item.getName();
                    if (item.isFormField()) {
                        logger.log(Level.INFO,
                                "Form had field {0}, set to value: {1}.",
                                new Object[]{name, Streams.asString(stream)}
                        );
                    } else {
                        logger.log(Level.INFO,
                                "Filefield! {0}, {1}({3})",
                                new Object[]{
                                    name,
                                    item.getName(),
                                    item.getContentType(),}
                        );

                        /* Process the uploaded file
                     * TODO: Some sort of "Oh hey this actually exists, right"
                     * pre dump checks would be nice.
                         */
                        File dstfile = new File(finalDir, item.getName());
                        long slen; // Total stream lenght, for display

                        logger.log(Level.INFO, "Starting to receive {0}...",
                                dstfile.getAbsolutePath());

                        // Dump stream into file, and close handle
                        slen = Streams.copy(stream,
                                new FileOutputStream(dstfile), true);

                        logger.log(Level.INFO, "Complete! Copied {0} bytes!", slen);

                        boolean suc = Functions.register(code, "/" + dirID + File.separator + item.getName());
                        if (suc) {
                            out.write("0");
                        } else {
                            out.write("3");
                        }
                    }
                }
            } catch (FileUploadException ex) {
                logger.log(Level.SEVERE,
                        "Oh dear god, file upload crapped out!", ex);
            } catch (Exception ex) {
                logger.log(Level.SEVERE, "Unexpected error", ex);
            }
            if(email != null && Functions.validateEmail(email)){
                ServletContext cntx= request.getServletContext();
                String as = cntx.getRealPath("/images/QuickPassLogo.jpg");
                System.out.println("The Path to the image is : "+as);
                Functions.sendEmail(email, fileName, size, code,as);
            }
        }
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);

    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Test Servlet for upload of stuffs.";
    }

}
