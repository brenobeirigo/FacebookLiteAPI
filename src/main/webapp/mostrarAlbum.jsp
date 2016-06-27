<%-- 
    Document   : mostrarAlbum
    Created on : 27/06/2016, 08:35:22
    Author     : vitor
--%>

<%@page import="model.Album"%>
<%@page contentType="text/html" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
    <head>
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <title>JSP Page</title>
    </head>
    <body>
        <%
            Album album = (Album)request.getAttribute("Album");
        %>
        <table border="l" cellpadding="0" cellspacing="0">
            <tr>
                <th>id_Album</th>
                <th>Nome</th>
            </tr>

            <tr>
                <td>
                    <a href= "atualizaAlbum.jsp?idAlbum=<%=album.getId() %>">
                        <%=album.getId() %>
                    </a>
                </td>
                <td><%=album.getName() %></td>
            </tr>
        </table>
            <br><br>
            <a href="index.html">Voltar para página inicial</a>
    </body>
</html>
