/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dao.FacebookDAO;
import dao.FacebookDAOException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Album;
import model.Comment;
import model.Photo;
import model.Post;
import model.PostComment;
import model.User;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.IOUtils;

/**
 *
 * @author BBEIRIGO
 */
@WebServlet(name = "FacebookServlet", urlPatterns = {"/facebook/*"})
public class FacebookServlet extends HttpServlet {

    public boolean doFilePost64(HttpServletRequest request) {
        System.out.println("Do file post");
        DiskFileItemFactory fileUpload = new DiskFileItemFactory();
        ServletFileUpload sfu = new ServletFileUpload(fileUpload);
        if (request.getContentType() == null) {
            System.out.println("Content type null");
            return false;
        }
        StringWriter writer = new StringWriter();
        try {
            IOUtils.copy(request.getInputStream(), writer);
        } catch (IOException ex) {
            Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println(writer.toString());
        
        //System.out.println("profile_encoded"+profileEnconded);
        byte[] decoded = Base64.decodeBase64(writer.toString());
        System.out.println(decoded.length);
        String data = "";
        
        BufferedReader br=null;
        try {
            InputStream iStream = request.getInputStream();
            br = new BufferedReader(new InputStreamReader(iStream, "utf8"));
            StringBuffer sb = new StringBuffer();
        String line = "";

        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        data = sb.toString();
        System.out.println(data);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        

        String diretorio = "C:\\Users\\BBEIRIGO\\Documents\\NetBeansProjects\\WebServiceFacebook\\src\\main\\webapp\\photos";
        try {
            FileOutputStream fos = new FileOutputStream(diretorio + "\\teste2.jpg");
            fos.write(decoded);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

        String filename = "file";
        String path = diretorio;

        return true;
    }

    public boolean doFilePost(HttpServletRequest request) {
        System.out.println("Do file post");
        DiskFileItemFactory fileUpload = new DiskFileItemFactory();
        ServletFileUpload sfu = new ServletFileUpload(fileUpload);
        if (request.getContentType() == null) {
            System.out.println("Content type null");
            return false;
        }
        if (!request.getContentType().startsWith("multipart/form-data")) {
            System.out.println("Não começa com multipart");
            return false;
        }
        //String diretorio = getServletContext().getRealPath("/") + "files";
        String diretorio = "C:\\Users\\BBEIRIGO\\Documents\\NetBeansProjects\\WebServiceFacebook\\src\\main\\webapp\\photos";

        System.out.println("DIRETORIO:" + diretorio);
        String filename = "file";
        String path = diretorio;
        List list;
        try {
            list = sfu.parseRequest(request);

            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                FileItem item = (FileItem) iterator.next();
                if (!item.isFormField()) {
                    System.out.println("FIELD NAME:" + item.getFieldName());
                    filename = item.getName();
                    if ((filename != null) && (!filename.equals(""))) {
                        filename = (new File(filename)).getName();
                        item.write(new File(path + "/" + filename));
                    }
                }
            }
        } catch (FileUploadException ex) {
            Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
        }
        return true;
    }

    private static class ServletUtil {

        public static void writeJSON(HttpServletResponse response, String json) throws IOException {
            if (json != null) {
                try (PrintWriter writer = response.getWriter()) {
                    response.setContentType("application/json;charset=UTF-8");
                    writer.write(json);
                }
            }
        }
    }

    private static class RegexUtil {

        //http://www.restapitutorial.com/lessons/restfulresourcenaming.html
        private static final Pattern regexAllUsers = Pattern.compile("/users");
        private static final Pattern regexUserId = Pattern.compile("/users/([0-9]*)");
        private static final Pattern regexUserName = Pattern.compile("/user/name/([a-zA-Z 0-9]*)");
        private static final Pattern regexUserFriends = Pattern.compile("/users/([0-9]*)/friends");
        private static final Pattern regexPostsOfFriends = Pattern.compile("/users/([0-9]*)/friends/posts");
        private static final Pattern regexPostsOfUser = Pattern.compile("/users/([0-9]*)/posts");
        private static final Pattern regexPhotosOfUser = Pattern.compile("/users/([0-9]*)/photos");
        private static final Pattern regexAlbunsOfUserId = Pattern.compile("/users/([0-9]*)/albuns");
        private static final Pattern regexPostIdComments = Pattern.compile("/posts/([0-9]*)/comments");
        private static final Pattern regexUserLikesPost = Pattern.compile("/likes");
        private static final Pattern regexLikesOfPost = Pattern.compile("/posts/([0-9]*)/likes");
        private static final Pattern regexCountLikesOfPost = Pattern.compile("/posts/([0-9]*)/likes/count");

        private static final Pattern regexStartFriendship = Pattern.compile("/users/friends");
        private static final Pattern regexLogin = Pattern.compile("/login");
        private static final Pattern regexPostPhoto = Pattern.compile("/photos");
        private static final Pattern regexPostComment = Pattern.compile("/comments");

        public static Integer MatchPostIdComments(String requestUri) throws ServletException {
            Matcher matcher = regexPostIdComments.matcher(requestUri);
            System.out.println(matcher);
            if (matcher.find() && matcher.groupCount() > 0) {
                System.out.println("POSTID - END:" + matcher.end() + " -- REQ:" + (requestUri.length()));
                if (matcher.end() == requestUri.length()) {
                    String s = matcher.group(1);
                    if (s != null && s.trim().length() > 0) {
                        int id = Integer.parseInt(s);
                        return id;
                    }
                }
            }
            return null;
        }

        public static Integer MatchCountLikesOfPost(String requestUri) throws ServletException {
            Matcher matcher = regexCountLikesOfPost.matcher(requestUri);
            System.out.println(matcher);
            if (matcher.find() && matcher.groupCount() > 0) {
                System.out.println("COUNT LIKES OF POST - END:" + matcher.end() + " -- REQ:" + (requestUri.length()));
                if (matcher.end() == requestUri.length()) {
                    String s = matcher.group(1);
                    if (s != null && s.trim().length() > 0) {
                        int id = Integer.parseInt(s);
                        return id;
                    }
                }
            }
            return null;
        }

        public static Integer MatchLikesOfPost(String requestUri) throws ServletException {
            Matcher matcher = regexLikesOfPost.matcher(requestUri);
            System.out.println(matcher);
            if (matcher.find() && matcher.groupCount() > 0) {
                System.out.println("LIKESOFPOST - END:" + matcher.end() + " -- REQ:" + (requestUri.length()));
                if (matcher.end() == requestUri.length()) {
                    String s = matcher.group(1);
                    if (s != null && s.trim().length() > 0) {
                        int id = Integer.parseInt(s);
                        return id;
                    }
                }
            }
            return null;
        }

        public static Integer MatchUserId(String requestUri) throws ServletException {
            Matcher matcher = regexUserId.matcher(requestUri);
            System.out.println(matcher);
            if (matcher.find() && matcher.groupCount() > 0) {
                System.out.println("ID - END:" + matcher.end() + " -- REQ:" + (requestUri.length()));
                if (matcher.end() == requestUri.length()) {
                    String s = matcher.group(1);
                    if (s != null && s.trim().length() > 0) {
                        int isbn = Integer.parseInt(s);
                        return isbn;
                    }
                }
            }
            return null;
        }

        /**
         * ALBUMS OF USER ID
         *
         * @param requestUri
         * @return
         * @throws ServletException
         */
        public static Integer MatchAlbunsOfUserId(String requestUri) throws ServletException {
            Matcher matcher = regexAlbunsOfUserId.matcher(requestUri);
            System.out.println(matcher);
            if (matcher.find() && matcher.groupCount() > 0) {
                System.out.println("ALBUMS - END:" + matcher.end() + " -- REQ:" + (requestUri.length()));
                if (matcher.end() == requestUri.length()) {
                    String s = matcher.group(1);
                    if (s != null && s.trim().length() > 0) {
                        int isbn = Integer.parseInt(s);
                        return isbn;
                    }
                }
            }
            return null;
        }

        /**
         * ALBUMS OF USER ID
         *
         * @param requestUri
         * @return
         * @throws ServletException
         */
        public static Integer MatchPhotosOfUserId(String requestUri) throws ServletException {
            Matcher matcher = regexPhotosOfUser.matcher(requestUri);
            System.out.println(matcher);
            if (matcher.find() && matcher.groupCount() > 0) {
                System.out.println("PHOTOS - END:" + matcher.end() + " -- REQ:" + (requestUri.length()));
                if (matcher.end() == requestUri.length()) {
                    String s = matcher.group(1);
                    if (s != null && s.trim().length() > 0) {
                        int isbn = Integer.parseInt(s);
                        return isbn;
                    }
                }
            }
            return null;
        }

        public static String MatchName(String requestUri) throws ServletException {
            Matcher matcher = regexUserName.matcher(requestUri);
            if (matcher.find() && matcher.groupCount() > 0) {
                System.out.println("NAME - END:" + matcher.end() + " -- REQ:" + (requestUri.length()));

                String s = matcher.group(1);
                if (s != null && s.trim().length() > 0) {
                    String name = s;
                    return name;
                }
            }
            return null;
        }

        public static Integer MatchPostsOfUser(String requestUri) throws ServletException {
            Matcher matcher = regexPostsOfUser.matcher(requestUri);
            if (matcher.find() && matcher.groupCount() > 0) {
                if (matcher.end() == requestUri.length()) {
                    System.out.println("POST - END:" + matcher.end() + " -- REQ:" + (requestUri.length()));

                    String s = matcher.group(1);
                    if (s != null && s.trim().length() > 0) {
                        Integer n = Integer.valueOf(s);
                        return n;
                    }
                }

            }
            return null;
        }

        public static Integer MatchPostsOfFriends(String requestUri) throws ServletException {
            Matcher matcher = regexPostsOfFriends.matcher(requestUri);
            if (matcher.find() && matcher.groupCount() > 0) {
                if (matcher.end() == requestUri.length()) {
                    System.out.println("POSTS OF FRIENDS - END:" + matcher.end() + " -- REQ:" + (requestUri.length()));
                    String posts = matcher.group(1);
                    if (posts != null && posts.trim().length() > 0) {
                        Integer n = Integer.valueOf(posts);
                        return n;
                    }
                }

            }
            return null;
        }

        public static Integer MatchUserFriends(String requestUri) throws ServletException {
            Matcher matcher = regexUserFriends.matcher(requestUri);
            if (matcher.find() && matcher.groupCount() > 0) {
                if (matcher.end() == requestUri.length()) {
                    System.out.println("FRIENDS - END:" + matcher.end() + " -- REQ:" + (requestUri.length()));

                    String s = matcher.group(1);

                    if (s != null && s.trim().length() > 0) {
                        int isbn = Integer.parseInt(s);
                        return isbn;
                    }
                }
            }
            return null;
        }

    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        System.out.println("GET GET!!!!!!!!!!!!!!");
        Integer offset = 0, limit = Integer.MAX_VALUE;
        if (request.getParameterMap().containsKey("offset")) {
            offset = Integer.valueOf(request.getParameter("offset"));
        }
        if (request.getParameterMap().containsKey("limit")) {
            limit = Integer.valueOf(request.getParameter("limit"));
        }
        System.out.println(offset + "   &&&   " + limit);
        String servletAddress = "http://" + request.getServerName() + ":" + request.getServerPort() + "/WebServiceFacebook/";
        String requestUri = request.getRequestURI();
        System.out.println("URI:" + requestUri);
        FacebookDAO dao = new FacebookDAO(servletAddress);
        Integer idUser = RegexUtil.MatchUserId(requestUri);
        String userName = RegexUtil.MatchName(requestUri);
        Integer idUserFriends = RegexUtil.MatchUserFriends(requestUri);
        Integer idUserPosts = RegexUtil.MatchPostsOfUser(requestUri);
        Integer idUserPostsOfFriends = RegexUtil.MatchPostsOfFriends(requestUri);
        Integer idUserAlbuns = RegexUtil.MatchAlbunsOfUserId(requestUri);
        Integer idUserPhotos = RegexUtil.MatchPhotosOfUserId(requestUri);
        Integer idPostComments = RegexUtil.MatchPostIdComments(requestUri);
        Integer idLikesOfPost = RegexUtil.MatchLikesOfPost(requestUri);
        Integer idPostCountLikes = RegexUtil.MatchCountLikesOfPost(requestUri);

        System.out.println("idPostsOfFriends:" + idUserPostsOfFriends + " -- UserId:" + idUser + " -- userName:" + userName + " -- friendOfUser:" + idUserFriends + " -- userOfPost:" + userName + " -- userOfPost:" + idUserPosts + " -- idUserAlbuns:" + idUserAlbuns + " -- idUserPhotos:" + idUserPhotos + " -- idPostComments:" + idPostComments);
        System.out.println("Friends of user:" + idUserFriends);
        if (idUser != null) {
            try {
                User u = dao.getUserById(idUser);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(u);
                System.out.println("RESULTADO BUSCA:" + u);
                ServletUtil.writeJSON(response, json);
                System.out.println("User:" + u);
            } catch (FacebookDAOException ex) {
                throw new ServletException("Impossível encontrar usuário com id = " + idUser + "!", ex);
            }
        } else if (userName != null) {
            try {
                List<User> u = dao.searchUsersByName(userName, offset, limit);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(u);
                System.out.println("RESULTADO BUSCA:" + u);
                ServletUtil.writeJSON(response, json);
                System.out.println(u);
            } catch (FacebookDAOException ex) {
                throw new ServletException("Impossível encontrar resultados com para \"" + userName + "\"!", ex);
            }

        } else if (idUserFriends != null) {
            try {
                List<User> users = dao.getFriendsOfUser(new User(idUserFriends, null, null, null), 0, 100);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(users);
                for (User u : users) {
                    System.out.println(u);
                }
                ServletUtil.writeJSON(response, json);

            } catch (FacebookDAOException ex) {
                throw new ServletException("Impossível encontrar os amigos do usuário com id = " + idUserFriends + "!", ex);
            }
        } else if (idUserPosts != null) {
            System.out.println("######## LIST POSTS OF USER "+idUserPosts);
            try {
                List<Post> posts = dao.getAllPostsOfUser(new User(idUserPosts), offset, limit);
                System.out.println(posts);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(posts);
                ServletUtil.writeJSON(response, json);

            } catch (FacebookDAOException ex) {
                throw new ServletException("Impossível encontrar os posts do usuário de id = " + idUserPosts + "!", ex);
            }
        } else if (idUserPostsOfFriends != null) {
            List<Post> posts;
            try {
                posts = dao.getAllFriendsPostsOfUser(new User(idUserPostsOfFriends), offset, limit);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(posts);
                ServletUtil.writeJSON(response, json);
            } catch (FacebookDAOException ex) {
                Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (idUserAlbuns != null) {
            try {
                User u = new User(idUserAlbuns);
                List<Album> albuns = dao.getAllAlbunsOfUser(u, offset, limit);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(albuns);
                for (Album a : albuns) {
                    System.out.println(a);
                }
                ServletUtil.writeJSON(response, json);

            } catch (FacebookDAOException ex) {
                throw new ServletException("Impossível encontrar os álbuns do usuário de id = " + idUserAlbuns + "!", ex);
            }
        } else if (idUserPhotos != null) {
            try {
                User u = new User(idUserPhotos);
                List<Photo> photos = dao.getAllPhotosOfUser(u, offset, limit);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(photos);
                ServletUtil.writeJSON(response, json);

            } catch (FacebookDAOException ex) {
                throw new ServletException("Impossível encontrar as photos do usuário de id = " + idUserAlbuns + "!", ex);
            }
        } else if (idPostComments != null) {
            try {
                List<Comment> comments = dao.getAllCommentsOfPost(new Post(idPostComments), offset, limit);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(comments);
                ServletUtil.writeJSON(response, json);
            } catch (FacebookDAOException ex) {
                throw new ServletException("Impossível encontrar comentários para o post de id = " + idPostComments + "!", ex);
            }
        } else if (idLikesOfPost != null) {
            List<User> users = null;
            try {
                users = dao.getListOfLikesPost(new Post(idLikesOfPost), offset, limit);

                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(users);
                ServletUtil.writeJSON(response, json);

            } catch (FacebookDAOException ex) {
                Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (idPostCountLikes != null) {
            try {
                Integer number = dao.getNumberOfLikesPost(new Post(idPostCountLikes));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(number);
                ServletUtil.writeJSON(response, json);
            } catch (FacebookDAOException ex) {
                Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

//processRequest(request, response);
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
        //processRequest(request, response);
        System.out.println("POST POST!!!!!!!!!!!!!!");
        String requestUri = request.getRequestURI();
        String servletAddress = "http://" + request.getServerName() + ":" + request.getServerPort() + "/WebServiceFacebook/";
        System.out.println("ADD:" + servletAddress);
        if (RegexUtil.regexLogin.matcher(requestUri).find()) {

            FacebookDAO dao = new FacebookDAO(servletAddress);
            try {
                User u = dao.login(request.getParameter("email"), request.getParameter("password"));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(u);
                ServletUtil.writeJSON(response, json);
            } catch (FacebookDAOException ex) {
                System.out.println("Erro login!" + ex.getMessage());;
            }
            System.out.println("POST LOGIN!");
        } else if (RegexUtil.regexPostPhoto.matcher(requestUri).find()) {
            System.out.println("POST PHOTO!!!!!!!!!!!!!!!!!!");
            doFilePost64(request);
            /*
            FacebookDAO dao = new FacebookDAO(servletAddress);
            try {
                User u = dao.login(request.getParameter("email"), request.getParameter("password"));
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(u);
                ServletUtil.writeJSON(response, json);
            } catch (FacebookDAOException ex) {
                System.out.println("Erro login!" + ex.getMessage());;
            }*/
            System.out.println("POST LOGIN!");
        } else if (RegexUtil.regexUserLikesPost.matcher(requestUri).find()) {
            //EXEMPLO: http://localhost:8080/WebServiceFacebook/facebook/likes?user=1&post=2
            User u = new User(Integer.valueOf(request.getParameter("user")));
            Post p = new Post(Integer.valueOf(request.getParameter("post")));
            System.out.println("Like:" + u + "--" + p);
            FacebookDAO dao = new FacebookDAO(servletAddress);
            try {
                dao.addLikeInPost(u, p);
                response.getWriter().write("LIKE");
                //Gson gson = new GsonBuilder().setPrettyPrinting().create();
                //String json = gson.toJson(u);
                //ServletUtil.writeJSON(response, json);
            } catch (FacebookDAOException ex) {
                System.out.println("Erro login!" + ex.getMessage());;
            }
            System.out.println("POST LOGIN!");
        } else if (RegexUtil.regexStartFriendship.matcher(requestUri).find()) {
            //EXEMPLO: http://localhost:8080/WebServiceFacebook/facebook/likes?user=1&post=2
            User u1 = new User(Integer.valueOf(request.getParameter("user1")));
            User u2 = new User(Integer.valueOf(request.getParameter("user2")));
            FacebookDAO dao = new FacebookDAO(servletAddress);
            try {
                dao.saveFriendship(u1, u2);
                dao.saveFriendship(u2, u1);
                response.getWriter().write("FRIENDS");
                //Gson gson = new GsonBuilder().setPrettyPrinting().create();
                //String json = gson.toJson(u);
                //ServletUtil.writeJSON(response, json);
            } catch (FacebookDAOException ex) {
                System.out.println("Erro login!" + ex.getMessage());;
            }
            System.out.println("POST LOGIN!");
        } else if (RegexUtil.regexPostComment.matcher(requestUri).find()) {
            //EXEMPLO: http://localhost:8080/WebServiceFacebook/facebook/likes?user=1&post=2
            String content = request.getParameter("content");
            User u = new User(Integer.valueOf(request.getParameter("user")));
            Post p = new Post(Integer.valueOf(request.getParameter("post")));
            Comment c = new Comment(u, content);
            System.out.println("PostComment:" + c);
            FacebookDAO dao = new FacebookDAO(servletAddress);
            try {
                int idPost = dao.saveComment(c, p);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(idPost);
                ServletUtil.writeJSON(response, json);
            } catch (FacebookDAOException ex) {
                System.out.println("Impossível salvar post!" + ex.getMessage());;
            }
            System.out.println("POST LOGIN!");
        }
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
