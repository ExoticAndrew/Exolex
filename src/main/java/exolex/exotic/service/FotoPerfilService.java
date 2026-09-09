package exolex.exotic.service;
import com.cloudinary.Transformation;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import exolex.exotic.exception.ArquivoInvalidoException;
import exolex.exotic.model.Usuario;
import exolex.exotic.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class FotoPerfilService {

    private static final long TAMANHO_MAXIMO = 3 * 1024 * 1024;

    private final Cloudinary cloudinary;
    private final UsuarioRepository usuarioRepository;

    public String atualizarFoto(Usuario usuario, MultipartFile arquivo) {
        if (arquivo.isEmpty()) {
            throw new ArquivoInvalidoException("Nenhum arquivo enviado");
        }
        if (arquivo.getSize() > TAMANHO_MAXIMO) {
            throw new ArquivoInvalidoException("Arquivo maior que 2MB");
        }

        String contentType = arquivo.getContentType();
        boolean tipoValido = contentType != null && (
                contentType.equals("image/png") ||
                        contentType.equals("image/jpeg") ||
                        contentType.equals("image/webp")
        );
        if (!tipoValido) {
            throw new ArquivoInvalidoException("Formato inválido. Envie PNG, JPEG ou WEBP");
        }

        try {
            Transformation transformation = new Transformation()
                    .width(512)
                    .height(512)
                    .crop("limit");

            Map<String, Object> resultado = cloudinary.uploader().upload(
                    arquivo.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "exolex/avatares",
                            "public_id", "usuario-" + usuario.getId(),
                            "overwrite", true,
                            "transformation", transformation
                    )
            );

            String url = (String) resultado.get("secure_url");
            usuario.setFotoUrl(url);
            usuarioRepository.save(usuario);

            return url;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ArquivoInvalidoException("Não foi possível processar a imagem");
        }
    }
}