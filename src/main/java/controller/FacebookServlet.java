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
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
//import java.util.Base64;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.Album;
import model.AlbumComment;
import model.Comment;
import model.Photo;
import model.PhotoComment;
import model.Post;
import model.PostComment;
import model.User;
import model.logica.Logica;
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
    
    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //Codificação correta
        request.setCharacterEncoding("utf-8");
        //Qual é a lógica?
        String parametro = request.getParameter("logica");
        //Todas as lógicas estão no pacote model.logica
        String nomeDaClasse = "model.logica." + parametro;
        try {
            Class<?> classe = Class.forName(nomeDaClasse);    
            Logica logica = (Logica) classe.newInstance();
            // Recebe o String após a execução da lógica
            String pagina = logica.executa(request, response);
            // Faz o forward para a página JSP
            request.getRequestDispatcher(pagina).forward(request, response);
        } catch (Exception e) {
            throw new ServletException("A lógica de negócios causou uma exceção", e);
        }

    }

 /*   public boolean saveFileEconded(String encodedPath, String path) throws FileNotFoundException, IOException {
        byte[] decoded = Base64.getMimeDecoder().decode(encodedPath);
        FileOutputStream fos = new FileOutputStream(path);
        fos.write(decoded);
        return true;
    }

    public boolean doFilePost64(HttpServletRequest request) throws FacebookDAOException {
        System.out.println("Do file post 45");
        if (request.getContentType() == null) {
            System.out.println("Content type null");
            return false;
        }

        String s = null;
        try {
            ServletInputStream input = request.getInputStream();
            s = IOUtils.toString(input);
            Pattern regex = Pattern.compile("name=(.*)&email=(.*)&password=(.*)&birthday=(.*)&profile=(.*)&cover=(.*)");
            Matcher m = regex.matcher(s);
            if (m.find() && m.groupCount() > 0) {
                String name = m.group(1);
                String email = m.group(2);
                String password = m.group(3);
                String birthday = m.group(4);
                String profile = m.group(5);
                String cover = m.group(6);
                System.out.println(name + " - " + email + " - " + password + " - " + birthday);//+" - "+profile);//+" - "+cover);
                System.out.println(profile.length());
                //
                //
                System.out.println("CALENDAR");
                Calendar birth = Calendar.getInstance();
                System.out.println("DATE");
                String date[] = (birthday.split("-"));
                System.out.println("BIRTH"+birthday);
                birth.set(Integer.valueOf(date[0]), Integer.valueOf(date[1]), Integer.valueOf(date[2]));
                //System.out.println(birth);
                System.out.println("USER");
                User u = new User(name, email, birth);
                //System.out.println(u);
                String pathFile = "C:/Users/BBEIRIGO/Documents/NetBeansProjects/WebServiceFacebook/src/main/webapp/";
                FacebookDAO dao = new FacebookDAO(pathFile);
                User cadastrado = dao.saveUser(u, password);
                new File(pathFile+"/photos/"+cadastrado.getId()+"/Cover").mkdirs();
                new File(pathFile+"/photos/"+cadastrado.getId()+"/Profile").mkdirs();
                System.out.println(cadastrado);
                System.out.println("cover:"+cadastrado.getCoverPhoto().getPath());
                ///
                String coverPath = cadastrado.getCoverPhoto().getPath();
                String profilePath = cadastrado.getProfilePhoto().getPath();
                System.out.println(coverPath);
                if (saveFileEconded(URLDecoder.decode(cover, "UTF-8"), coverPath)) {
                    System.out.println("ENCODE COVER!!!");
                } else {
                    System.out.println("NO ENCODE COVER!!!");
                }
                if (saveFileEconded(URLDecoder.decode(profile, "UTF-8"), profilePath)) {
                    System.out.println("ENCODE PROFILE!!!");
                } else {
                    System.out.println("NO ENCODE PROFILE!!!");
                }
            } else {
                System.out.println("Não achou!");
            }
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
        }

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
        private static final Pattern regexAlbumId = Pattern.compile("/album/([0-9]*)");
        
        private static final Pattern regexPOSTLogin = Pattern.compile("/login");
        private static final Pattern regexPOSTPhoto = Pattern.compile("/photos");
        private static final Pattern regexPOSTRegister = Pattern.compile("/register");
        private static final Pattern regexPOSTComment = Pattern.compile("/comments");        
        private static final Pattern regexRemoveAlbumComment = Pattern.compile("/removeAlbumComment");
        private static final Pattern regexRemovePhotoComment = Pattern.compile("/removePhotoComment");
        private static final Pattern regexRemovePostComment = Pattern.compile("/removePostComment");
        private static final Pattern regexUpdateAlbum = Pattern.compile("/updateAlbum");
        
       
        public static Integer MatchAlbumId(String requestUri) throws ServletException {
            Matcher matcher = regexAlbumId.matcher(requestUri);
            System.out.println(matcher);
            if (matcher.find() && matcher.groupCount() > 0) {
                System.out.println("ALBUM - END:" + matcher.end() + " -- REQ:" + (requestUri.length()));
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

    }*/

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
        processRequest(request, response);
        /*Integer offset = 0, limit = Integer.MAX_VALUE;
        if (request.getParameterMap().containsKey("offset")) {
            offset = Integer.valueOf(request.getParameter("offset"));
        }
        if (request.getParameterMap().containsKey("limit")) {
            limit = Integer.valueOf(request.getParameter("limit"));
        }
        System.out.println(offset + "   &&&   " + limit);
        String servletAddress = "http://" + request.getServerName() + ":" + request.getServerPort() + "/WebServiceFacebook/";
        //Requested URI
        String requestUri = URLDecoder.decode(request.getRequestURI(), "UTF-8");
        System.out.println("URI:" + requestUri);

        //Create FacebookDAO
        FacebookDAO dao = new FacebookDAO(servletAddress);

        //Parameters
        Integer idUser = RegexUtil.MatchUserId(requestUri);
        String userName = RegexUtil.MatchName(requestUri);
        Integer idUserFriends = RegexUtil.MatchUserFriends(requestUri);
        Integer idUserPosts = RegexUtil.MatchPostsOfUser(requestUri);
        Integer idUserPostsOfFriends = RegexUtil.MatchPostsOfFriends(requestUri);
        Integer idUserAlbuns = RegexUtil.MatchAlbunsOfUserId(requestUri);
        Integer idUserPhotos = RegexUtil.MatchPhotosOfUserId(requestUri);
        Integer idPostComments = RegexUtil.MatchPostIdComments(requestUri);
        Integer idPostLikes = RegexUtil.MatchLikesOfPost(requestUri);
        Integer idPostCountLikes = RegexUtil.MatchCountLikesOfPost(requestUri);
        Integer idAlbum = RegexUtil.MatchAlbumId(requestUri);

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

        }else if(idAlbum!=null){
            try {
                Album a = dao.getAlbumById(idAlbum);
                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                String json = gson.toJson(a);
                System.out.println("RESULTADO BUSCA:" + a);
                ServletUtil.writeJSON(response, json);
                System.out.println(a);
            } catch (FacebookDAOException ex) {
                throw new ServletException("Impossível encontrar resultados com para o album \"" + idAlbum + "\"!", ex);
            }
            
        }
        else if (idUserFriends != null) {
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
            System.out.println("######## LIST POSTS OF USER " + idUserPosts);
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
        } else if (idPostLikes != null) {
            List<User> users = null;
            try {
                users = dao.getListOfLikesPost(new Post(idPostLikes), offset, limit);

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
        }*/
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
        /*System.out.println("POST POST!!!!!!!!!!!!!!");
        String requestUri = request.getRequestURI();
        String servletAddress = "http://" + request.getServerName() + ":" + request.getServerPort() + "/WebServiceFacebook/";
        System.out.println("ADD:" + servletAddress);
        if (RegexUtil.regexPOSTLogin.matcher(requestUri).find()) {

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
        } else if (RegexUtil.regexPOSTPhoto.matcher(requestUri).find()) {
            System.out.println("POST PHOTO!!!!!!!!!!!!!!!!!!");
            try {
                //File post para celular
                doFilePost64(request);
                //File post de formulário
                doFilePost(request);
            } catch (FacebookDAOException ex) {
               Logger.getLogger(FacebookServlet.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else if (RegexUtil.regexPOSTRegister.matcher(requestUri).find()) {
              //File post de formulário
                doFilePost(request);
        } else if (RegexUtil.regexUpdateAlbum.matcher(requestUri).find()) {
            // Exemplo http://localhost:8080/FacebookLiteAPI/facebook/updateAlbum?idAlbum=3&name=familias
            System.out.println("UPDATE ALBUM");
            int idAlbum=0;
            String name = "";
            try {
                idAlbum = Integer.parseInt(request.getParameter("idAlbum"));
                name = request.getParameter("name");
                Album a = new Album(idAlbum);
                a.setName(name);
                FacebookDAO dao = new FacebookDAO(servletAddress);                
                dao.updateAlbum(a);
            } catch (FacebookDAOException ex) {
                System.out.println("Erro ao remover comentário de id: " +idAlbum);
            }
        }else if (RegexUtil.regexRemoveAlbumComment.matcher(requestUri).find()) {
            System.out.println("REMOVE COMMENT");
            int idComment=0;
            try {
                idComment = Integer.parseInt(request.getParameter("idAlbumComment"));
                AlbumComment c = new AlbumComment(idComment);
                FacebookDAO dao = new FacebookDAO(servletAddress);                
                dao.removeComment(c);
            } catch (FacebookDAOException ex) {
                System.out.println("Erro ao remover comentário de id: " +idComment);
            }
        }else if (RegexUtil.regexRemovePostComment.matcher(requestUri).find()) {
            System.out.println("REMOVE COMMENT");
            int idComment=0;
            try {
                idComment = Integer.parseInt(request.getParameter("idPostComment"));
                PostComment c = new PostComment(idComment);
                FacebookDAO dao = new FacebookDAO(servletAddress);                
                dao.removeComment(c);
            } catch (FacebookDAOException ex) {
                System.out.println("Erro ao remover comentário de id: " +idComment);
            }
        }else if (RegexUtil.regexRemovePhotoComment.matcher(requestUri).find()) {
            System.out.println("REMOVE COMMENT");
            int idComment=0;
            try {
                idComment = Integer.parseInt(request.getParameter("idPhotoComment"));
                PhotoComment c = new PhotoComment(idComment);
                FacebookDAO dao = new FacebookDAO(servletAddress);                
                dao.removeComment(c);
            } catch (FacebookDAOException ex) {
                System.out.println("Erro ao remover comentário de id: " +idComment);
            }
        } else if (RegexUtil.regexUserLikesPost.matcher(requestUri).find()) {
            //EXEMPLO: http://localhost:8080/WebServiceFacebook/facebook/likes?user=1&post=2
            User u = new User(Integer.valueOf(request.getParameter("user")));
            Post p = new Post(Integer.valueOf(request.getParameter("post")));
            System.out.println("Like:" + u + "--" + p);
            FacebookDAO dao = new FacebookDAO(servletAddress);
            try {
                dao.addLikeInPost(u, p);
                response.getWriter().write("LIKE");
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
            } catch (FacebookDAOException ex) {
                System.out.println("Erro login!" + ex.getMessage());;
            }
            System.out.println("POST LOGIN!");
        } else if (RegexUtil.regexPOSTComment.matcher(requestUri).find()) {
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
        }*/
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
