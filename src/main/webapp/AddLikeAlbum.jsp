<%-- 
    Document   : AddLikeAlbum
    Created on : 06/07/2016, 14:04:21
    Author     : elias
--%>

<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>Adicionar Likes</title>
    </head>
    <body>
        <form action="facebook?logica=AddLikeAlbum" method="post">
            Album_id: 
            <input type="text" name="idAlbum" id="idAlbum">
            User_id: 
            <input type="text" name="idUser" id="idUser">
            <input type="submit" value="Enviar">
        </form>
    </body>
</html>
