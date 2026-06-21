package br.com.blade.indicafilme.controller.doc;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Indica Filmes API",
        version = "v1",
        description = "API REST do sistema Indica Filmes." +
            "Endpoints públicos permitem buscar e sortear filmes com filtros. "+
            "Endpoints admin (protegidos por X-Admin-Key) permitem gerenciar o catálogo.",
        contact = @Contact(
            name = "Administrador",
            email = "redacted@users.noreply.github.com"
        )
    ),
    servers = {
        @Server(url = "/", description = "Servidor atual")
    }
)
@SecurityScheme(
    name = "AdminApiKey",
    description = "Chave de API para acesso aos endpoints de administração. " +
        "Envia no header: X-Admin-Key: <sua_chave>",
    type = SecuritySchemeType.APIKEY,
    paramName = "X-Admin-Key",
    in = SecuritySchemeIn.HEADER
)
public class IndicaFilmesSwagger {}
