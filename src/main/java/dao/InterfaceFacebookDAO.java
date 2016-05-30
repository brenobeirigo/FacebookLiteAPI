package dao;

import java.util.List;
import model.Album;
import model.User;
import model.Comment;
import model.Photo;
import model.Post;

/**
 *
 * @author BBEIRIGO
 */
public interface InterfaceFacebookDAO {
    /***** USER ***************************************************************/
    //0 - Check user
    User login(String email, String password) throws FacebookDAOException;
    //1 - Cadastra usuário
    void saveUser(User user) throws FacebookDAOException;
    //2 - Apaga usuário user
    void removeUser(User user) throws FacebookDAOException;
    //3 - Atualiza dados do usuário user (id não é atualizado)
    void updateUser(User user) throws FacebookDAOException;
    //4 - Retorna usuário com id = idUser
    User getUserById(int idUser) throws FacebookDAOException;
    //5 - Retorna todos os usuários cujo nome contenha a substring sub
    List<User> searchUsersByName(String sub, int offset, int limit) throws FacebookDAOException;
    //6 - Retorna todos os amigos do usuário user ordenados por ordem alfabética   
    List<User> getFriendsOfUser(User user, int offset, int limit) throws FacebookDAOException;
    //7 - Retorna comentários curtidos pelo usuário (mais recente primeiro)
    List<Comment> getLikedCommentsOfUser(User user, int offset, int limit) throws FacebookDAOException;
    //8 - Retorna álbuns curtidos pelo usuário (mais recente primeiro)
    List<Album> getLikedAlbunsOfUser(User user, int offset, int limit) throws FacebookDAOException;
    //9 - Retorna fotos curtidas pelo usuário (mais recente primeiro)  
    List<Photo> getLikedPhotosOfUser(User user, int offset, int limit) throws FacebookDAOException;
    //10 - Retorna postagens dos amigos de um usuário (mais recente primeiro)  
    List<Post> getAllFriendsPostsOfUser(User user, int offset, int limit) throws FacebookDAOException;
    
    /***** ALBUM **************************************************************/
    //11 - Cadastra álbum
    void saveAlbum(Album album, User user) throws FacebookDAOException;
    //12 - Apaga álbum
    void removeAlbum(Album album) throws FacebookDAOException;
    //13 - Atualiza dados do album (id não é atualizado)
    void updateAlbum(Album album) throws FacebookDAOException;
    //14 - Retorna todos os albuns do usuário user
    List<Album> getAllAlbunsOfUser(User user, int offset, int limit) throws FacebookDAOException;
    //15 - Retorna o álbum de id = idAlbum
    Album getAlbumById(int idAlbum) throws FacebookDAOException;
    //16 - Usuário curte álbum
    void addLikeInAlbum(User user, Album album) throws FacebookDAOException;
    //17 - Usuário "descurte" álbum
    void removeLikeInAlbum(User user, Album album) throws FacebookDAOException;
    //18 - Retorna lista de usuários que curtiram o álbum
    //Note que é possível criar um objeto apenas com o atributo id
    List<User> getListOfLikesAlbum(Album album) throws FacebookDAOException;
    
    /***** PHOTO **************************************************************/
    //19 - Cadastra photo
    void savePhoto(Photo photo, Album album) throws FacebookDAOException;
    //20 - Apaga photo
    void removePhoto(Photo photo) throws FacebookDAOException;
    //21 - Atualiza dados da foto (id não é atualizado)
    void updatePhoto(Photo photo) throws FacebookDAOException;
    //22A - Retorna todas as fotos do álbum
    List<Photo> getAllPhotosOfAlbum(Album album, int offset, int limit) throws FacebookDAOException;
    //22B - Retorna todas as fotos de um usuário
    List<Photo> getAllPhotosOfUser(User user, int offset, int limit) throws FacebookDAOException;
    //23 - Retorna a foto de id = idPhoto
    Photo getPhotoById(int idPhoto) throws FacebookDAOException;
    //24 - Usuário curte photo
    void addLikeInPhoto(User user, Photo album) throws FacebookDAOException;
    //25 - Usuário "descurte" photo
    void removeLikeInPhoto(User user, Photo album) throws FacebookDAOException;
    //26 - Retorna lista de usuários que curtiram a foto
    //Note que é possível criar um objeto apenas com o atributo id
    List<User> getListOfLikesPhoto(Photo photo);
    
    
    /***** COMMENT ************************************************************/
    //DICA: Verificar o tipo do comentário dentro dos métodos (instanceof)
    //para decidir em qual tabela de comentários salvar (PostComment, PhotoComment, AlbumComment)
    //27A - Cadastra comentário em album
    Integer saveComment(Comment comment, Album album) throws FacebookDAOException;
    //27B - Cadastra comentário em photo
    Integer saveComment(Comment comment, Photo photo) throws FacebookDAOException;
    //27C - Cadastra comentário em post
    Integer saveComment(Comment comment, Post post) throws FacebookDAOException;
    //28 - Apaga comentário
    void removeComment(Comment comment) throws FacebookDAOException;
    //29 - Atualiza dados do comentário (id não é atualizado)
    void updateComment(Comment comment) throws FacebookDAOException;
    //30 - Retorna todos os comentários de um post (PostComment) ordenados pelo número de curtidas
    List<Comment> getAllCommentsOfPost(Post post, int offset, int limit) throws FacebookDAOException;
    //31 - Retorna todos os comentários de uma foto (PhotoComment) ordenados pelo número de curtidas
    List<Comment> getAllCommentsOfPhoto(Photo photo, int offset, int limit) throws FacebookDAOException;
    //32 - Retorna todos os comentários de um álbum (AlbumComment) ordenados pelo número de curtidas
    List<Comment> getAllCommentsOfAlbum(Album album, int offset, int limit) throws FacebookDAOException;
    //33 - Retorna o comentário de id = idComment
    Comment getCommentById(int idComment, int type) throws FacebookDAOException;
    //34 - Usuário curte comentário
    void addLikeInComment(User user, Comment comment) throws FacebookDAOException;
    //35 - Usuário "descurte" comentário
    void removeLikeInComment(User user, Comment comment) throws FacebookDAOException;
    //36 - Retorna a lista de usuários que curtiram o comentário
    //Note que é possível criar objetos apenas com o atributo id
    List<User> getListOfLikesComment(Comment comment) throws FacebookDAOException;
    
    /***** POST ***************************************************************/
    //37 - Cadastra postagem
    Integer savePost(Post post) throws FacebookDAOException;
    //38 - Apaga postagem
    void removePost(Post post) throws FacebookDAOException;
    //39 - Atualiza dados da postagem (id não é atualizado)
    void updatePost(Post post) throws FacebookDAOException;
    //40 - Retorna todas as postagens de um usuário (mais recente primeiro)
    List<Post> getAllPostsOfUser(User user, int offset, int limit) throws FacebookDAOException;
    //41 - Retorna a postagem de id = idPost
    Post getPostById(int idPost) throws FacebookDAOException;
    //42 - Usuário curte post
    void addLikeInPost(User user, Post idPost) throws FacebookDAOException;
    //43 - Usuário "descurte" post
    void removeLikeInPost(User user, Post idPost) throws FacebookDAOException;    
    //44 - Retorna a lista de usuários que curtiram o post
    //Note que é possível criar objetos apenas com o atributo id
    List<User> getListOfLikesPost(Post post, int offset, int limit) throws FacebookDAOException;
    //45 - Number of likes
    Integer getNumberOfLikesPost(Post post) throws FacebookDAOException;
    //46 - Start Friendship
    public void saveFriendship(User user1, User user2) throws FacebookDAOException; 
}
