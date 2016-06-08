/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package dao;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import model.Album;
import model.AlbumComment;
import model.Comment;
import model.Photo;
import model.PhotoComment;
import model.Post;
import model.PostComment;
import model.User;
import util.ConnectionFactory;

/**
 *
 * @author BBEIRIGO
 */
public class FacebookDAO implements InterfaceFacebookDAO {

    private String server;
    private String port;
    private String db;
    private String user;
    private String password;
    private String servletAddress;

    private Connection geraConexao() throws FacebookDAOException {
        Connection conn;
        try {
            conn = ConnectionFactory.getConnection(server, port, db, user, password);
        } catch (Exception e) {
            throw new FacebookDAOException("Falha na conexão.", e);
        }
        return conn;
    }

    public FacebookDAO(String servletAddress) {
        this.server = "localhost";
        this.port = "3306";
        this.db = "facebookdb";
        this.user = "root";
        this.password = "123456";
        this.servletAddress = servletAddress;
    }
    
    public FacebookDAO() {
        this.server = "localhost";
        this.port = "3306";
        this.db = "facebookdb";
        this.user = "root";
        this.password = "123456";
    }

    @Override
    public User saveUser(User user, String password) throws FacebookDAOException {
        PreparedStatement ps = null;
        ResultSet rs = null;
        Connection conn = null;
        Integer idSaved = null;
        User u = null;
        if (user == null) {
            throw new FacebookDAOException("O valor passado não pode ser nulo.");
        }

        try {
            String SQL = "INSERT INTO `facebookdb`.`user` (`name`, `email`, `birthDate`, `password`) VALUES (?, ?, ?, ?);";
            conn = geraConexao();
            ps = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            
            //Caso seja necessário trabalhar com datas ou timestamps utilizar setDate e setTime em conjunto com o Calendar. Exemplo:
            //Cria objeto calendar
            Calendar c = Calendar.getInstance();

            //Armazena data
            c = user.getDateOfBirth();
            //Altera statement
            ps.setDate(3, new java.sql.Date(c.getTimeInMillis()));
            ps.setString(4, password);
            
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idSaved = rs.getInt(1);
            }
            int idAlbumCover = saveAlbum(new Album("Cover"),new User(idSaved));
            int idPhotoCover = savePhoto(new Photo("photos/"+idSaved+"/Cover/photo_"+System.nanoTime()+".jpg"), new Album(idAlbumCover));
            
            int idAlbumProfile = saveAlbum(new Album("Profile"),new User(idSaved));
            int idPhotoProfile = savePhoto(new Photo("photos/"+idSaved+"/Profile/photo_"+System.nanoTime()+".jpg"), new Album(idAlbumProfile));
            
            String updateSQL = "UPDATE `facebookdb`.`user` SET `profilePhoto`=?, `coverPhoto`=? WHERE `idUser`=?";
            conn = geraConexao();
            ps = conn.prepareStatement(updateSQL,Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, idPhotoProfile);
            ps.setInt(2, idPhotoCover);
            ps.setInt(3,idSaved);
            ps.executeUpdate();
            
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idSaved = rs.getInt(1);
            }
            u = getUserById(idSaved);
        } catch (SQLException sqle) {
            throw new FacebookDAOException("Erro ao inserir dados: \"" + user.getName() + "\", \"" + user.getEmail() + "\".", sqle);
        }  finally {
            ConnectionFactory.closeConnection(conn, ps);
        }
        return u;

    }

    @Override
    public void removeUser(User user) throws FacebookDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        if (user == null) {
            throw new FacebookDAOException("O valor passado não pode ser nulo.");
        }
        try {
            conn = geraConexao();
            ps = conn.prepareStatement("DELETE FROM `facebookdb`.`user` WHERE `idUser`=?;");
            ps.setLong(1, user.getId());
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new FacebookDAOException("Erro ao excluir usuario de ID = " + user.getId() + ".", sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps);
        }
    }

    @Override
    public void updateUser(User user) throws FacebookDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        if (user == null) {
            throw new FacebookDAOException("O valor passado não pode ser nulo.");
        }
        try {
            String SQL = "UPDATE `facebookdb`.`user` SET `name`=?, `email`=?, `birthDate`=? WHERE `idUser`=?;";
            conn = geraConexao();
            ps = conn.prepareStatement(SQL);
            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            Calendar c = Calendar.getInstance();
            //Armazena data
            c = user.getDateOfBirth();
            //Altera statement
            ps.setDate(3, new java.sql.Date(c.getTimeInMillis()));
            ps.setInt(4, user.getId());

            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new FacebookDAOException("Erro ao atualizar dados do usuario com ID = " + user.getId() + ".", sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps);
        }
    }

    @Override
    public List<User> searchUsersByName(String sub, int offset, int n) throws FacebookDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        try {
            conn = geraConexao();
            ps = conn.prepareStatement("select * from `facebookdb`.`user` where name like ? order by name asc limit ?,?");
            ps.setString(1, '%' + sub + "%");
            ps.setInt(2, offset);
            ps.setInt(3, n);
            rs = ps.executeQuery();
            List<User> list = new ArrayList<User>();

            while (rs.next()) {
                User u = new User(rs.getInt("idUser"), rs.getString("name"), getPhotoById(rs.getInt("profilePhoto")), getPhotoById(rs.getInt("coverPhoto")));
                list.add(u);
            }
            return list;
        } catch (SQLException sqle) {
            throw new FacebookDAOException(sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps, rs);
        }
    }

    @Override
    public List<User> getFriendsOfUser(User user, int offset, int n) throws FacebookDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        try {

            conn = geraConexao();
            ps = conn.prepareStatement("select * from user where idUser in (select f.idUser1 from friendship f where f.idUser2 = ?) or idUser in (select f.idUser2 from friendship f where f.idUser1 = ?) order by name asc limit ?,?");
            ps.setInt(1, user.getId());
            ps.setInt(2, user.getId());
            ps.setInt(3, offset);
            ps.setInt(4, n);
            rs = ps.executeQuery();
            List<User> list = new ArrayList();
            while (rs.next()) {
                int id = rs.getInt(1);
                String nome = rs.getString(2);
                int profilePhoto = rs.getInt("profilePhoto");
                int coverPhoto = rs.getInt("coverPhoto");
                User u = new User(id, nome, getPhotoById(profilePhoto), getPhotoById(coverPhoto));
                list.add(u);
            }
            return list;
        } catch (SQLException sqle) {
            throw new FacebookDAOException(sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps, rs);
        }
    }

    @Override
    public User getUserById(int idUser) throws FacebookDAOException {
        User u = null;
        try {
            Connection conn;
            PreparedStatement pstm;
            conn = geraConexao();
            pstm = conn.prepareStatement("select * from user where idUser = ?");
            pstm.setInt(1, idUser);
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Photo profilePhoto = getPhotoById(rs.getInt("profilePhoto"));
                System.out.println("P:" + profilePhoto);
                u = new User(rs.getInt("idUser"), rs.getString("name"), profilePhoto, getPhotoById(rs.getInt("coverPhoto")));
            }
            ConnectionFactory.closeConnection(conn);

        } catch (FacebookDAOException | SQLException e) {
            throw new FacebookDAOException("Impossível retornar usuário com id = " + idUser + ".", e);
        }
        return u;
    }

    @Override
    public List<Comment> getLikedCommentsOfUser(User user, int offset, int n) throws FacebookDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        conn = ConnectionFactory.getConnection("localhost", "3306", "facebookdb", "root", "123456");
        List<Comment> list = new ArrayList<>();
        int iduser = user.getId();
        try {
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //Pega todos os comentarios em albums e adiciona a lista
            ps = conn.prepareStatement("select * FROM albumcomment WHERE albumcomment.idUser = ? order by albumcomment.creationDate");
            ps.setInt(1, iduser);
            rs = ps.executeQuery();
            if (!rs.next()) {
                System.out.println("Não foram encontradas Curtidas em comentários.");
                return new ArrayList<Comment>();
            }
            for (int i = 0; i < n && rs.next(); ++i) {
                int id = rs.getInt(1);
                int idalbum = rs.getInt(2);
                int idUser = rs.getInt(3);
                System.out.println(rs.getDate(4));
                Calendar timestamp = Calendar.getInstance();
                List<User> lista = new ArrayList<User>();
                PreparedStatement aux = null;
                //pega a lista de usuarios que curtiram o comentario
                aux = conn.prepareStatement("select * FROM user_likes_albumcomment  where user_likes_albumcomment.idAlbumComment = ?");
                aux.setInt(1, id);
                ResultSet aux2 = null;
                aux2 = aux.executeQuery();
                while (aux2.next()) {
                    int idusera = aux2.getInt(1);
                    User a = getUserById(idusera);
                    lista.add(a);
                }
                String content = rs.getString(6);
                User userComent = getUserById(idUser);
                Album album = getAlbumById(idalbum);
                Calendar creation = Calendar.getInstance();
                creation.setTimeInMillis(rs.getTimestamp("commentTime").getTime());
                AlbumComment novo = new AlbumComment(id, content, userComent, creation, album);
                list.add(novo);

            }

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //Pega todos os comentarios em fotos e adiciona a lista
            ps = conn.prepareStatement("select * FROM photocomment WHERE photocomment.idUser = ? order by photocomment.commentTime");
            ps.setInt(1, iduser);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new FacebookDAOException(" Naõ foram encontradas curtidas em comentarios em albums ");
            }
            for (int i = 0; i < n && rs.next(); ++i) {
                int id = rs.getInt(1);
                int idUser = rs.getInt(2);
                int idphoto = rs.getInt(3);
                Calendar timestamp = Calendar.getInstance();
                System.out.println(rs.getTime(5));
                List<User> lista = new ArrayList<User>();
                PreparedStatement aux = null;
                //pega a lista de usuarios que curtiram o comentario
                aux = conn.prepareStatement("select * FROM user_likes_photocomment  where user_likes_photocomment.idPhotoComment = ?");
                aux.setInt(1, id);
                ResultSet aux2 = null;
                aux2 = aux.executeQuery();
                while (aux2.next()) {
                    int idusera = aux2.getInt(1);
                    User a;
                    a = getUserById(idusera);
                    lista.add(a);
                }
                String content = rs.getString(4);

                User userComent = getUserById(idUser);
                Photo photo = getPhotoById(idphoto);
                Calendar creation = Calendar.getInstance();
                creation.setTimeInMillis(rs.getTimestamp("commentTime").getTime());
                PhotoComment novo = new PhotoComment(id, userComent, content, creation, photo);
                list.add(novo);
            }

            ///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            //Pega todos os comentarios em posts e adiciona a lista
            ps = conn.prepareStatement("select * FROM postcomment WHERE postcomment.idCommentatorUser = ? order by postcomment.commentTime");
            ps.setInt(1, iduser);
            rs = ps.executeQuery();
            if (!rs.next()) {
                throw new FacebookDAOException(" Naõ foram encontradas curtidas em comentarios em albums ");
            }
            for (int i = 0; i < n && rs.next(); ++i) {
                int id = rs.getInt(1);
                int idUser = rs.getInt(2);
                int idpost = rs.getInt(3);
                Calendar timestamp = Calendar.getInstance();
                timestamp.set(rs.getDate(5).getDay(), rs.getDate(5).getMonth(), rs.getDate(5).getYear(), rs.getDate(5).getHours(), rs.getDate(5).getMinutes());
                List<User> lista = new ArrayList<User>();
                PreparedStatement aux = null;
                //pega a lista de usuarios que curtiram o comentario
                aux = conn.prepareStatement("select * FROM user_likes_postcomment  where user_likes_postcomment.idPostComment = ?");
                aux.setInt(1, id);
                ResultSet aux2 = null;
                aux2 = aux.executeQuery();
                while (aux2.next()) {
                    int idusera = aux2.getInt(1);
                    User a = getUserById(idusera);
                    lista.add(a);
                }
                String content = rs.getString(4);

                User userComent = getUserById(idUser);
                Post post = getPostById(idpost);
                Calendar creation = Calendar.getInstance();
                creation.setTimeInMillis(rs.getTimestamp("commentTime").getTime());
                PostComment novo = new PostComment(id, content, userComent, creation, post);
                list.add(novo);
            }

        } catch (SQLException sqle) {
            throw new FacebookDAOException(sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps, rs);
        }

        return list;
    }

    @Override
    public List<Album> getLikedAlbunsOfUser(User user, int offset, int n) throws FacebookDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        conn = ConnectionFactory.getConnection("localhost", "3306", "facebookdb", "root", "123456");
        List<Album> list = new ArrayList<Album>();
        int iduser = user.getId();
        try {
            ps = conn.prepareStatement("select * FROM user_likes_album WHERE user_likes_album.idUser = ? order by user_likes_album.timeLike");
            ps.setInt(1, iduser);
            rs = ps.executeQuery();
            for (int i = 0; i < n && rs.next(); ++i) {
                int idAlbum = rs.getInt(2);
                Album a = getAlbumById(idAlbum);
                list.add(a);

            }

        } catch (SQLException sqle) {
            throw new FacebookDAOException(sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps, rs);
        }

        return list;
    }

    @Override
    public List<Photo> getLikedPhotosOfUser(User user, int offset, int n) throws FacebookDAOException {
        List<Photo> list = new ArrayList<Photo>();
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        int iduser = user.getId();
        conn = ConnectionFactory.getConnection("localhost", "3306", "facebookdb", "root", "123456");
        try {
            ps = conn.prepareStatement("select * FROM user_likes_photo WHERE  user_likes_photo.idUser = ? order by user_likes_photo.timeLike");
            ps.setInt(1, iduser);
            rs = ps.executeQuery();

            for (int i = 0; i < n && rs.next(); ++i) {
                int idphoto = rs.getInt(2);
                Photo p = getPhotoById(idphoto);
                list.add(p);
            }

        } catch (SQLException sqle) {
            throw new FacebookDAOException(sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps, rs);
        }
        return list;
    }

    @Override
    public List<Post> getAllFriendsPostsOfUser(User user, int offset, int limit) throws FacebookDAOException {
        List<Post> list = new ArrayList();
        List<User> listofbrodis = new ArrayList();
        listofbrodis = getFriendsOfUser(user, 0, Integer.MAX_VALUE);
        int numOfBrodis = listofbrodis.size();
        for (int i = 0; i < numOfBrodis; i++) {
            User aux = listofbrodis.get(i);
            List<Post> aux2 = new ArrayList();
            aux2 = getAllPostsOfUser(aux, 0, 0);
            for (int j = 0; j < aux2.size(); j++) {
                list.add(aux2.get(j));
            }
        }

        Collections.sort(list);
        List<Post> lista = new ArrayList();
        for (int i = 0; i < list.size(); i++) {
            lista.add(list.get(i));
        }
        return list;
    }

    @Override
    public Integer saveAlbum(Album album, User user) throws FacebookDAOException {
        ResultSet rs = null;//Apenas existe para a função 
        PreparedStatement ps = null;
        Connection conn = null;
        Integer idSaved = null;
        conn = geraConexao();
        try {
            ps = conn.prepareStatement("INSERT INTO `facebookdb`.`album` (`idUser`, `name`) VALUES (?, ?)", Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, user.getId());
            ps.setString(2, album.getName());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idSaved = rs.getInt(1);
            }
        } catch (SQLException sqle) {
            throw new FacebookDAOException("ERro em saveAlubm",sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps, rs);

        }
        return idSaved;
    }

    @Override
    public void removeAlbum(Album album) throws FacebookDAOException {
        ResultSet rs = null;//Apenas existe para a função 
        PreparedStatement ps = null;
        Connection conn = null;
        conn = ConnectionFactory.getConnection("localhost", "3306", "facebookdb", "root", "123456");
        int idAlbum = album.getId();
        try {
            ps = conn.prepareStatement("DELETE FROM `facebookdb`.`album` WHERE `idAlbum`='?'");
            ps.setInt(1, idAlbum);
            ps.executeQuery();
        } catch (SQLException sqle) {
            throw new FacebookDAOException(sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps, rs);

        }
    }

    @Override
    public void updateAlbum(Album album) throws FacebookDAOException {

        Connection conexao = geraConexao();
        PreparedStatement ps = null;

        if (album == null) {
            throw new FacebookDAOException("Álbum não pode ser nulo!!!");
        }

        try {
            String SQL = "UPDATE album SET name=? WHERE idAlbum=?";
            ps = conexao.prepareStatement(SQL);
            ps.setString(1, album.getName());
            ps.setInt(2, album.getId());

            ps.executeUpdate();

        } catch (SQLException ex) {
            Logger.getLogger(FacebookDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        ConnectionFactory.closeConnection(conexao, ps);
    }

    @Override
    public List<Album> getAllAlbunsOfUser(User user, int offset, int n) throws FacebookDAOException {
        Connection conexao = geraConexao();
        PreparedStatement ps = null;
        ResultSet rs;
        List<Album> lista = new ArrayList<>();
        if (user == null) {
            throw new FacebookDAOException("Usuário não pode ser nulo!!!");
        }

        try {
            String SQL = "SELECT * FROM album WHERE idUser=?";
            ps = conexao.prepareStatement(SQL);
            ps.setInt(1, user.getId());
            rs = ps.executeQuery();

            while (rs.next()) {
                int idAlbum = rs.getInt("idAlbum");
                int idUser = rs.getInt("idUser");
                String nomeAlbum = rs.getString("name");
                Time c = rs.getTime("creationDate");

                lista.add(getAlbumById(idAlbum));
            }

        } catch (SQLException ex) {
            throw new FacebookDAOException("Impossível retornar dados do usuário id = " + user.getId(), ex);
        } finally {
            ConnectionFactory.closeConnection(conexao, ps);
        }
        return lista;
    }

    @Override
    public Album getAlbumById(int idAlbum) throws FacebookDAOException {
        Connection conexao = geraConexao();
        PreparedStatement ps = null;
        ResultSet rs;
        Album a = null;

        if (idAlbum < 0) {
            throw new FacebookDAOException("idAlbum inválido!!!");
        }

        try {
            String SQL = "SELECT * FROM album WHERE idAlbum=?";
            ps = conexao.prepareStatement(SQL);
            ps.setInt(1, idAlbum);
            rs = ps.executeQuery();
            while (rs.next()) {
                int idUser = rs.getInt("idUser");
                String nomeAlbum = rs.getString("name");
                a = new Album(idAlbum, nomeAlbum, null);
            }

        } catch (SQLException ex) {
            throw new FacebookDAOException("Impossível retornar álbum de id = " + idAlbum, ex);
        }
        ConnectionFactory.closeConnection(conexao, ps);
        return a;
    }

    @Override
    public void addLikeInAlbum(User user, Album album) throws FacebookDAOException {
        Connection conexao = geraConexao();
        PreparedStatement ps = null;
        Album a = null;

        if (user == null || album == null) {
            throw new FacebookDAOException("Album ou usuário inválidos!!!");
        }

        try {
            String SQL = "INSERT INTO user_likes_album (idUser, idAlbum) VALUES (?, ?);";
            ps = conexao.prepareStatement(SQL);
            ps.setInt(1, user.getId());
            ps.setInt(2, album.getId());

            ps.execute();

        } catch (SQLException ex) {
            Logger.getLogger(FacebookDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        ConnectionFactory.closeConnection(conexao, ps);
    }

    @Override
    public void removeLikeInAlbum(User user, Album album) throws FacebookDAOException {
        Connection conexao = geraConexao();
        PreparedStatement ps = null;
        Album a = null;

        if (user == null || album == null) {
            throw new FacebookDAOException("Album ou usuário inválidos!!!");
        }

        try {
            String SQL = "DELETE FROM user_likes_album WHERE idUser =? AND idAlbum =?;";
            ps = conexao.prepareStatement(SQL);
            ps.setInt(1, user.getId());
            ps.setInt(2, album.getId());

            ps.execute();

        } catch (SQLException ex) {
            Logger.getLogger(FacebookDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        ConnectionFactory.closeConnection(conexao, ps);
    }

    @Override
    public List<User> getListOfLikesAlbum(Album album) {
        Connection conexao;
        PreparedStatement ps;
        ResultSet rs;
        List<User> lista = new ArrayList<>();

        try {
            conexao = geraConexao();
            String SQL = "SELECT * FROM user_likes_album WHERE idAlbum=?";
            ps = conexao.prepareStatement(SQL);
            ps.setInt(1, album.getId());
            rs = ps.executeQuery();

            while (rs.next()) {
                int idUser = rs.getInt("idUser");
                lista.add(getUserById(idUser));
            }

            ConnectionFactory.closeConnection(conexao, ps);

        } catch (SQLException | FacebookDAOException ex) {
            Logger.getLogger(FacebookDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        return lista;
    }

    @Override
    public Integer savePhoto(Photo photo, Album album) throws FacebookDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        ResultSet rs = null;
        Integer idSaved=0;
        if (photo == null) {
            throw new FacebookDAOException("O valor passado não pode ser nulo.");
        }

        try {
            conn = geraConexao();
            String SQL = " INSERT INTO photo (idAlbum,path) VALUES (?,?);";
            ps = conn.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            //Obtendo o Album que ira ser salvo a foto;
            ps.setObject(1, album.getId());
            //Obtendo o caminho da foto;
            ps.setString(2, photo.getPath());
            //Executando
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idSaved = rs.getInt(1);
            }
        } catch (SQLException sqle) {
            throw new FacebookDAOException("Erro ao inserir dados: Caminho da foto: \"" + photo.getPath() + "\"", sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps);
        }
        return idSaved;
    }

    @Override
    public void removePhoto(Photo photo) throws FacebookDAOException {
        PreparedStatement ps = null;
        Connection conn = null;
        if (photo == null) {
            throw new FacebookDAOException("O valor passado não pode ser nulo.");
        }
        try {
            conn = geraConexao();
            ps = conn.prepareStatement("delete from photo where path=?");
            ps.setString(1, photo.getPath());
            ps.executeUpdate();
        } catch (SQLException sqle) {
            throw new FacebookDAOException("Erro ao excluir Photo com o Endereço :" + photo.getPath(), sqle);
        } finally {
            ConnectionFactory.closeConnection(conn, ps);
        }
    }

    @Override
    public void updatePhoto(Photo photo) throws FacebookDAOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Photo> getAllPhotosOfAlbum(Album album, int offset, int n) throws FacebookDAOException {
        List<Photo> photos = new ArrayList();

        Connection conexao = geraConexao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        try {
            String SQL = "SELECT * FROM photo WHERE idAlbum = ?";
            ps = conexao.prepareStatement(SQL);
            ps.setInt(1, album.getId());
            rs = ps.executeQuery();
            while (rs.next()) {
                photos.add(getPhotoById(rs.getInt("idPhoto")));
            }
        } catch (SQLException ex) {
            Logger.getLogger(FacebookDAO.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            ConnectionFactory.closeConnection(conexao, ps, rs);
        }
        return photos;

    }

    @Override
    public Photo getPhotoById(int idPhoto) throws FacebookDAOException {
        Photo photo = null;
        Connection conexao = geraConexao();
        PreparedStatement ps;
        try {
            String SQL = "SELECT * FROM photo WHERE idPhoto = ?";
            ps = conexao.prepareStatement(SQL);
            ps.setInt(1, idPhoto);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                Calendar creation = Calendar.getInstance();
                creation.setTimeInMillis(rs.getTimestamp("uploadTime").getTime());
                photo = new Photo(rs.getInt("idPhoto"), this.servletAddress + rs.getString("path"), creation);
                ps.close();
            }

        } catch (SQLException ex) {
            Logger.getLogger(FacebookDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        return photo;
    }

    @Override
    public void addLikeInPhoto(User user, Photo album) throws FacebookDAOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void removeLikeInPhoto(User user, Photo album) throws FacebookDAOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<User> getListOfLikesPhoto(Photo photo) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Integer saveComment(Comment comment, Album a) throws FacebookDAOException {
        Connection conexao = geraConexao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer idSaved = null;
        try {
            String SQL = "INSERT INTO albumcomment (idAlbum, idUser, content) VALUES (?, ?, ?)";
            ps = conexao.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, a.getId());
            ps.setInt(2, comment.getCommentator().getId());
            ps.setString(3, comment.getContent());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idSaved = rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new FacebookDAOException("Impossível fazer comentário em Álbum!", ex);
        } finally {
            ConnectionFactory.closeConnection(conexao, ps, rs);
        }

        return idSaved;
    }

    @Override
    public Integer saveComment(Comment comment, Photo photo) throws FacebookDAOException {
        Connection conexao = geraConexao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer idSaved = null;
        try {
            String SQL = "INSERT INTO photocomment (idUser, idPhoto, content) VALUES (?, ?, ?)";
            ps = conexao.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, comment.getCommentator().getId());
            ps.setInt(2, photo.getId());
            ps.setString(3, comment.getContent());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            if (rs.next()) {
                idSaved = rs.getInt(1);
            }
        } catch (SQLException ex) {
            throw new FacebookDAOException("Impossível fazer comentário em Foto!", ex);
        } finally {
            ConnectionFactory.closeConnection(conexao, ps, rs);
        }
        return idSaved;
    }

    @Override
    public Integer saveComment(Comment comment, Post p) throws FacebookDAOException {
        Connection conexao = geraConexao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        Integer idSaved = null;
        try {
            String SQL = "INSERT INTO postcomment (idCommentatorUser, idPost, content) VALUES (?, ?, ?)";
            ps = conexao.prepareStatement(SQL, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, comment.getCommentator().getId());
            ps.setInt(2, p.getId());
            ps.setString(3, comment.getContent());
            //System.out.println("COM:"+comment.getCommentator().getId()+" - "+p.getId()+" - "+comment.getContent());
            //System.out.println("COM2:"+comment.getCommentator().getId()+" - "+p.getId()+" - "+comment.getContent());
            ps.executeUpdate();
            rs = ps.getGeneratedKeys();
            //System.out.println("COM3:"+comment.getCommentator().getId()+" - "+p.getId()+" - "+comment.getContent());

            if (rs.next()) {
                idSaved = rs.getInt(1);
            }
        } catch (Exception ex) {
            //ex.printStackTrace();
            throw new FacebookDAOException("Impossível fazer comentário em Post!", ex);
        } finally {
            ConnectionFactory.closeConnection(conexao, ps, rs);
        }

        return idSaved;
    }

    @Override
    public void removeComment(Comment comment) throws FacebookDAOException {
        Connection conexao = geraConexao();
        PreparedStatement ps;
        try {
            if (comment instanceof AlbumComment) {

                AlbumComment var = (AlbumComment) comment;
                String SQL = "DELETE FROM albumcomment where idAlbumComment = ?";

                ps = conexao.prepareStatement(SQL);

                ps.setInt(1, var.getId());

                ps.execute();
                ps.close();
            }

            if (comment instanceof PhotoComment) {

                PhotoComment var = (PhotoComment) comment;
                String SQL = "DELETE FROM photocomment where idPhotoComment = ?";

                ps = conexao.prepareStatement(SQL);

                ps.setInt(1, var.getId());

                ps.execute();
                ps.close();
            }

            if (comment instanceof PostComment) {

                PostComment var = (PostComment) comment;
                String SQL = "DELETE FROM postcomment where idPostComment = ?";

                ps = conexao.prepareStatement(SQL);

                ps.setInt(1, var.getId());

                ps.execute();
                ps.close();
            }

        } catch (SQLException e) {
            Logger.getLogger(FacebookDAO.class.getName()).log(Level.SEVERE, null, e);
        }

    }

    @Override
    public void updateComment(Comment comment) throws FacebookDAOException {
        Connection conexao = geraConexao();
        PreparedStatement ps;
        try {

            if (comment instanceof AlbumComment) {
                AlbumComment var = (AlbumComment) comment;

                String SQL = "UPDATE albumcomment SET content = ? WHERE idAlbumComment = ?";
                ps = conexao.prepareStatement(SQL);

                ps.setString(1, var.getContent());
                ps.setInt(2, var.getId());

                ps.execute();
                ps.close();
            }

            if (comment instanceof PhotoComment) {
                PhotoComment var = (PhotoComment) comment;

                String SQL = "UPDATE photocomment SET content = ? WHERE idPhotoComment = ?";
                ps = conexao.prepareStatement(SQL);

                ps.setString(1, var.getContent());
                ps.setInt(2, var.getId());

                ps.execute();
                ps.close();

            }

            if (comment instanceof PostComment) {
                PostComment var = (PostComment) comment;

                String SQL = "UPDATE postcomment SET content = ? WHERE idPostComment = ?";
                ps = conexao.prepareStatement(SQL);

                ps.setString(1, var.getContent());
                ps.setInt(2, var.getId());

                ps.execute();
                ps.close();

            }

        } catch (SQLException ex) {
            Logger.getLogger(FacebookDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<Comment> getAllCommentsOfPost(Post post, int offset, int n) throws FacebookDAOException {

        List<Comment> l = new ArrayList<>();
        Connection conexao = geraConexao();
        PreparedStatement ps;
        try {
            String SQL = "SELECT * FROM postcomment WHERE idPost = ? ORDER BY commentTime";
            ps = conexao.prepareStatement(SQL);
            ps.setInt(1, post.getId());
            ResultSet rs = ps.executeQuery();
            for (int i = 0; i < n && rs.next(); ++i) {
                Calendar creation = Calendar.getInstance();
                creation.setTimeInMillis(rs.getTimestamp("commentTime").getTime());
                User commentator = getUserById(rs.getInt("idCommentatorUser"));
                PostComment c = new PostComment(rs.getInt(1), rs.getString(4), new User(commentator.getId(), commentator.getName(), new Photo(commentator.getProfilePhoto().getId(), commentator.getProfilePhoto().getPath())), creation, post);
                l.add(c);
            }

            ps.close();

        } catch (SQLException ex) {
            Logger.getLogger(FacebookDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (l == null) {
            throw new FacebookDAOException("Não houve resultado.");
        }
        return l;
    }

    @Override
    public List<Comment> getAllCommentsOfPhoto(Photo photo, int offset, int n) throws FacebookDAOException {
        List<Comment> l = new ArrayList<>();
        Connection conexao = geraConexao();
        PreparedStatement ps;
        try {
            String SQL = "SELECT * FROM photocomment WHERE idPhoto = ? ORDER BY commentTime";
            ps = conexao.prepareStatement(SQL);

            ps.setInt(1, photo.getId());

            ResultSet rs = ps.executeQuery();

            if (rs == null) {
                throw new FacebookDAOException("Não houve Resultados.");
            }
            for (int i = 0; i < n && rs.next(); ++i) {
                Calendar creation = Calendar.getInstance();
                creation.setTimeInMillis(rs.getTimestamp("commentTime").getTime());
                PhotoComment c = new PhotoComment(rs.getInt("idPhotoComment"), getUserById(rs.getInt("idUser")), rs.getString("content"), creation, getPhotoById(rs.getInt("idPhoto")));
                l.add(c);
            }

            ps.close();

        } catch (SQLException ex) {
            Logger.getLogger(FacebookDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (l.isEmpty()) {
            throw new FacebookDAOException("Não houve resultado.");
        }
        return l;

    }

    @Override
    public List<Comment> getAllCommentsOfAlbum(Album album, int offset, int n) throws FacebookDAOException {
        List<Comment> l = new ArrayList<>();
        Connection conexao = geraConexao();
        PreparedStatement ps;
        try {
            String SQL = "SELECT * FROM albumcomment WHERE idAlbum = ? ORDER BY creationdate";
            ps = conexao.prepareStatement(SQL);

            ps.setInt(1, album.getId());

            ResultSet rs = ps.executeQuery();

            if (rs == null) {
                throw new FacebookDAOException("Não houve Resultados.");
            }

            for (int i = 0; i < n && rs.next(); ++i) {
                User u = new User(rs.getInt("idUser"), rs.getString("name"), getPhotoById(rs.getInt("profilePhoto")), getPhotoById(rs.getInt("coverPhoto")));
                u.setId(rs.getInt(2));
                AlbumComment c = new AlbumComment(rs.getInt(1), rs.getString(6), u, null, album);
                l.add(c);
            }

            ps.close();

        } catch (SQLException ex) {
            Logger.getLogger(FacebookDAO.class.getName()).log(Level.SEVERE, null, ex);
        }

        if (l.isEmpty()) {
            throw new FacebookDAOException("Não houve resultado.");
        }
        return l;
    }

    @Override
    public Comment getCommentById(int idComment, int type) throws FacebookDAOException {
        PreparedStatement pstm;
        switch (type) {
            case 1: {
                //COMENTARIO DE POST
                PostComment p = null;
                try {
                    pstm = geraConexao().prepareStatement("select * from postcomment where idPostComment = ?");
                    pstm.setInt(1, idComment);
                    ResultSet rs = pstm.executeQuery();

                    while (rs.next()) {
                        User u = null;//getUserById(rs.getInt("idCommentatorUser"));
                        Post post = null;//getPostById(rs.getInt("idPost"));
                        p = new PostComment(rs.getInt("idPostComment"), rs.getString("content"), u, null, post);
                    }
                    pstm.close();
                } catch (FacebookDAOException | SQLException e) {
                    throw new FacebookDAOException(e.getMessage());
                }
                return p;
            }
            case 2: {
                //COMENTARIO DE ALBUM

                AlbumComment p = null;
                try {
                    pstm = geraConexao().prepareStatement("select * from albumcomment where idAlbumComment = ?");
                    pstm.setInt(1, idComment);
                    ResultSet rs = pstm.executeQuery();

                    while (rs.next()) {
                        User u = null;//getUserById(rs.getInt("idUser"));
                        Album a = null;//getAlbumById(rs.getInt("idAlbum"));
                        p = new AlbumComment(rs.getInt("idAlbumComment"), rs.getString("content"), u, null, a);
                    }
                    pstm.close();

                } catch (FacebookDAOException | SQLException e) {
                    throw new FacebookDAOException(e.getMessage());
                }
                return p;
            }
            case 3: {
                //COMENTARIO DE PHOTO

                PhotoComment p = null;
                try {
                    pstm = geraConexao().prepareStatement("select * from photocomment where idPhotoComment = ?");
                    pstm.setInt(1, idComment);
                    ResultSet rs = pstm.executeQuery();

                    while (rs.next()) {
                        User u = null;//getUserById(rs.getInt("idUser"));
                        Photo photo = null;//getPhotoById(rs.getInt("idPhoto"));
                        Calendar creation = Calendar.getInstance();
                        creation.setTimeInMillis(rs.getTimestamp("commentTime").getTime());
                        p = new PhotoComment(rs.getInt("idPhotoComment"), u, rs.getString("content"), creation, photo);
                    }
                    pstm.close();

                } catch (FacebookDAOException | SQLException e) {
                    throw new FacebookDAOException(e.getMessage());
                }
                return p;
            }
            default:
                break;
        }
        return null;
    }

    @Override
    public void addLikeInComment(User user, Comment comment) throws FacebookDAOException {
        PreparedStatement pstm;
        if (comment instanceof AlbumComment) {
            AlbumComment a = (AlbumComment) comment;
            try {
                pstm = geraConexao().prepareStatement("insert into user_likes_albumcomment (idUser, idAlbumComment) values (?,?);");
                pstm.setInt(1, user.getId());
                pstm.setInt(2, a.getId());
                pstm.execute();
                pstm.close();
            } catch (FacebookDAOException | SQLException e) {
                throw new FacebookDAOException(e.getMessage());
            }
        } else if (comment instanceof PhotoComment) {
            PhotoComment a = (PhotoComment) comment;
            try {
                pstm = geraConexao().prepareStatement("insert into user_likes_photocomment (idUser, idPhotoComment) values (?,?);");
                pstm.setInt(1, user.getId());
                pstm.setInt(2, a.getId());
                pstm.execute();
                pstm.close();
            } catch (FacebookDAOException | SQLException e) {
                throw new FacebookDAOException(e.getMessage());
            }
        } else if (comment instanceof PostComment) {
            PostComment a = (PostComment) comment;
            try {
                pstm = geraConexao().prepareStatement("insert into user_likes_postcomment (idUser, idPostComment) values (?,?);");
                pstm.setInt(1, user.getId());
                pstm.setInt(2, a.getId());
                pstm.execute();
                pstm.close();
            } catch (FacebookDAOException | SQLException e) {
                throw new FacebookDAOException(e.getMessage());
            }
        }
    }

    @Override
    public void removeLikeInComment(User user, Comment comment) throws FacebookDAOException {
        PreparedStatement pstm;
        if (comment instanceof AlbumComment) {
            AlbumComment a = (AlbumComment) comment;
            try {
                pstm = geraConexao().prepareStatement("delete from user_likes_albumcomment where idUser = ? and idAlbumComment=?;");
                pstm.setInt(1, user.getId());
                pstm.setInt(2, a.getId());
                pstm.execute();
                pstm.close();
            } catch (FacebookDAOException | SQLException e) {
                throw new FacebookDAOException(e.getMessage());
            }
        } else if (comment instanceof PhotoComment) {
            PhotoComment a = (PhotoComment) comment;
            try {
                pstm = geraConexao().prepareStatement("delete from user_likes_photocomment where idUser=? and idPhotoComment=?;");
                pstm.setInt(1, user.getId());
                pstm.setInt(2, a.getId());
                pstm.execute();
                pstm.close();
            } catch (FacebookDAOException | SQLException e) {
                throw new FacebookDAOException(e.getMessage());
            }
        } else if (comment instanceof PostComment) {
            PostComment a = (PostComment) comment;
            try {
                pstm = geraConexao().prepareStatement("delete from user_likes_postcomment where idUser=? and idPostComment=?;");
                pstm.setInt(1, user.getId());
                pstm.setInt(2, a.getId());
                pstm.execute();
                pstm.close();
            } catch (FacebookDAOException | SQLException e) {
                throw new FacebookDAOException(e.getMessage());
            }
        }
    }

    @Override
    public List<User> getListOfLikesComment(Comment comment) throws FacebookDAOException {
        List<User> list = new ArrayList<>();
        List<Integer> ids = new ArrayList<>();
        PreparedStatement pstm;
        if (comment instanceof AlbumComment) {
            AlbumComment a = (AlbumComment) comment;
            try {
                pstm = geraConexao().prepareStatement("select * from user_likes_albumcomment where idAlbumComment = ?;");
                pstm.setInt(1, a.getId());
                ResultSet rs = pstm.executeQuery();

                while (rs.next()) {
                    ids.add(rs.getInt("idUser"));
                }

                pstm.close();

            } catch (FacebookDAOException | SQLException e) {
                throw new FacebookDAOException(e.getMessage());
            }

        } else if (comment instanceof PhotoComment) {
            PhotoComment a = (PhotoComment) comment;
            try {
                pstm = geraConexao().prepareStatement("select * from user_likes_photocomment where idPhotoComment = ?;");
                pstm.setInt(1, a.getId());
                ResultSet rs = pstm.executeQuery();

                while (rs.next()) {
                    ids.add(rs.getInt("idUser"));
                }

                pstm.close();
            } catch (FacebookDAOException | SQLException e) {
                throw new FacebookDAOException(e.getMessage());
            }

        } else if (comment instanceof PostComment) {
            PostComment a = (PostComment) comment;
            try {
                pstm = geraConexao().prepareStatement("select * from user_likes_postcomment where idPostComment = ?;");
                pstm.setInt(1, a.getId());
                ResultSet rs = pstm.executeQuery();

                while (rs.next()) {
                    ids.add(rs.getInt("idUser"));
                }

                pstm.close();
            } catch (FacebookDAOException | SQLException e) {
                throw new FacebookDAOException(e.getMessage());
            }
        }
        for (int i = 0; i < ids.size(); i++) {
            User user = getUserById(ids.get(i));
            list.add(user);
        }
        return list;
    }

    @Override
    public Integer savePost(Post post) throws FacebookDAOException {
        Connection conn = geraConexao();
        Integer idSaved = null;
        ResultSet rs = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("insert into post (idAuthorUser, content) values (?,?)", Statement.RETURN_GENERATED_KEYS);
            pstm.setInt(1, post.getAuthor().getId());
            pstm.setString(2, post.getContent());
            pstm.executeUpdate();
            rs = pstm.getGeneratedKeys();
            if (rs.next()) {
                idSaved = rs.getInt(1);
            }

        } catch (SQLException e) {
            throw new FacebookDAOException("Impossível salvar post!", e);
        } finally {
            ConnectionFactory.closeConnection(conn, pstm, rs);
        }
        return idSaved;
    }

    @Override
    public void saveFriendship(User u1, User u2) throws FacebookDAOException {
        Connection conn = geraConexao();
        ResultSet rs = null;
        PreparedStatement pstm = null;
        try {
            pstm = conn.prepareStatement("insert into friendship (idUser1, idUser2) values (?,?);");
            pstm.setInt(1, u1.getId());
            pstm.setInt(2, u2.getId());
            pstm.executeUpdate();
        } catch (SQLException e) {
            throw new FacebookDAOException("Impossível começar amizade!", e);
        } finally {
            ConnectionFactory.closeConnection(conn, pstm, rs);
        }
    }

    @Override
    public void removePost(Post post) throws FacebookDAOException {
        PreparedStatement pstm;
        Connection conexao = geraConexao();
        try {
            pstm = conexao.prepareStatement("delete from post where idPost = ?");
            pstm.setInt(1, post.getId());
            pstm.executeUpdate();
            pstm.close();
        } catch (SQLException e) {
            throw new FacebookDAOException("Impossível remover post " + post.getId() + ".", e);
        }
    }

    @Override
    public void updatePost(Post post) throws FacebookDAOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<Post> getAllPostsOfUser(User user, int offset, int n) throws FacebookDAOException {
        List<Post> listPost = new ArrayList();
        PreparedStatement pstm;
        try {
            pstm = geraConexao().prepareStatement("select * from post where idAuthorUser = ?");
            pstm.setInt(1, user.getId());
            ResultSet rs = pstm.executeQuery();
            while (rs.next()) {
                Calendar creation = Calendar.getInstance();
                creation.setTimeInMillis(rs.getTimestamp("creationTime").getTime());
                User u = getUserById(user.getId());
                Photo photo = new Photo(u.getProfilePhoto().getId(), u.getProfilePhoto().getPath());
                Post post = new Post(rs.getInt("idPost"), new User(user.getId(), user.getName(), photo), rs.getString("content"), creation);
                listPost.add(post);
            }
            pstm.close();
        } catch (SQLException e) {
            throw new FacebookDAOException("Impossível buscar posts do usuário " + user.getId() + ".", e);
        }
        return listPost;
    }

    @Override
    public Post getPostById(int idPost) throws FacebookDAOException {
        Post p = null;
        PreparedStatement pstm;
        try {
            pstm = geraConexao().prepareStatement("select * from post where idPost = ?");
            pstm.setInt(1, idPost);
            ResultSet rs = pstm.executeQuery();

            while (rs.next()) {
                Calendar creation = Calendar.getInstance();
                creation.setTimeInMillis(rs.getTimestamp("creationTime").getTime());
                User user = getUserById(rs.getInt("idAuthorUser"));
                p = new Post(rs.getInt("idPost"), user, rs.getString("content"), creation);
            }
            pstm.close();

        } catch (FacebookDAOException | SQLException e) {
            throw new FacebookDAOException(e.getMessage());
        }
        return p;
    }

    @Override
    public void addLikeInPost(User user, Post post) throws FacebookDAOException {
        Connection conexao = geraConexao();
        PreparedStatement ps = null;

        if (user == null || post == null) {
            throw new FacebookDAOException("Post ou usuário inválidos!!!");
        }

        try {
            String SQL = "INSERT INTO user_likes_post (idUser, idPost) VALUES (?, ?);";
            ps = conexao.prepareStatement(SQL);
            ps.setInt(1, user.getId());
            ps.setInt(2, post.getId());

            ps.execute();

        } catch (SQLException ex) {
            Logger.getLogger(FacebookDAO.class.getName()).log(Level.SEVERE, null, ex);
        }
        ConnectionFactory.closeConnection(conexao, ps);
    }

    @Override
    public void removeLikeInPost(User user, Post idPost) throws FacebookDAOException {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<User> getListOfLikesPost(Post post, int offset, int limit) throws FacebookDAOException {
        List<User> list = new ArrayList<>();
        Connection conn = geraConexao();
        PreparedStatement pstm = null;
        ResultSet rs = null;
        try {
            pstm = conn.prepareStatement("select * from user_likes_post where idPost = ? LIMIT ?,?");
            pstm.setInt(1, post.getId());
            pstm.setInt(2, offset);
            pstm.setInt(3, limit);
            rs = pstm.executeQuery();
            while (rs.next()) {
                User u = getUserById(rs.getInt("idUser"));
                list.add(new User(u.getId(), u.getName(), new Photo(u.getProfilePhoto().getId(), u.getProfilePhoto().getPath())));
            }
            pstm.close();
        } catch (SQLException e) {
            throw new FacebookDAOException("Impossível retornar lista de likes para:" + post.getId(), e);
        } finally {
            ConnectionFactory.closeConnection(conn, pstm, rs);
        }
        return list;
    }

    @Override
    public List<Photo> getAllPhotosOfUser(User user, int offset, int limit) throws FacebookDAOException {
        List<Album> albuns = getAllAlbunsOfUser(user, 0, Integer.MAX_VALUE);
        List photos = new ArrayList();
        for (Album a : albuns) {
            photos.addAll(getAllPhotosOfAlbum(a, 0, Integer.MAX_VALUE));
        }
        System.out.println("PHOTOS:" + photos);
        Collections.sort(photos);
        List<Photo> allPhotos = new ArrayList();
        for (int i = 0; i < Math.min(photos.size(), limit); i++) {
            allPhotos.add((Photo) photos.get(i));
        }
        return allPhotos;

    }

    @Override
    public User login(String email, String password) throws FacebookDAOException {
        User u = null;
        Connection conn = null;
        ResultSet rs = null;
        PreparedStatement pstm = null;
        try {
            conn = geraConexao();
            pstm = conn.prepareStatement("select * from user where email=? and password=?");
            pstm.setString(1, email);
            pstm.setString(2, password);
            rs = pstm.executeQuery();
            while (rs.next()) {
                Photo profilePhoto = getPhotoById(rs.getInt("profilePhoto"));
                System.out.println("P:" + profilePhoto);
                u = new User(rs.getInt("idUser"), rs.getString("name"), profilePhoto, getPhotoById(rs.getInt("coverPhoto")));
            }
        } catch (SQLException e) {
            throw new FacebookDAOException("Erro ao retornar usuário.", e);
        } finally {
            ConnectionFactory.closeConnection(conn, pstm, rs);
        }
        return u;
    }

    @Override
    public Integer getNumberOfLikesPost(Post post) throws FacebookDAOException {
        Connection conn = geraConexao();
        PreparedStatement ps = null;
        ResultSet rs = null;
        int countLikes = 0;

        if (post == null) {
            throw new FacebookDAOException("Post inválido!!!");
        }
        try {
            String SQL = "SELECT COUNT(*) FROM user_likes_post WHERE idPost=?";
            ps = conn.prepareStatement(SQL);
            ps.setInt(1, post.getId());
            rs = ps.executeQuery();
            while (rs.next()) {
                countLikes = rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new FacebookDAOException("Erro ao encontrar o número de likes do post = " + countLikes + ".", e);
        } finally {
            ConnectionFactory.closeConnection(conn, ps, rs);
        }
        return countLikes;
    }
}
