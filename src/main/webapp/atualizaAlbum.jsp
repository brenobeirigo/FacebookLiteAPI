<%-- 
    Document   : atualizaAlbum
    Created on : 27/06/2016, 09:59:02
    Author     : vitor
--%>

<%@page import="model.Album"%>
<%@page import="dao.FacebookDAO"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%
            int id_Album = Integer.parseInt(request.getParameter("idAlbum"));
            FacebookDAO dao = new FacebookDAO();
            Album album = dao.getAlbumById(id_Album);
        %>
        <form action="facebook?logica=EditarAlbum" method="post">
            <input type="hidden" name="formid" id="formid"  value="<%= album.getId() %>">
            <input type="text" name="formnome" id="formnome" value="<%= album.getName() %>">
            <input type="submit" value="Enviar">
        </form>
            <br><br>
            <a href="index.html">Voltar para página inicial</a>
    </body>
</html>
