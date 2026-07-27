package exolex.exotic.service;

import exolex.exotic.dtos.CadastroRequestDTO;
import exolex.exotic.dtos.LoginRequestDTO;
import exolex.exotic.dtos.LoginResponseDTO;
import exolex.exotic.exception.CredenciaisInvalidasException;
import exolex.exotic.exception.EmailJaCadastradoException;
import exolex.exotic.model.Usuario;
import exolex.exotic.repository.UsuarioRepository;
import exolex.exotic.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public LoginResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(CredenciaisInvalidasException::new);

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new CredenciaisInvalidasException();
        }

        String token = jwtService.gerarToken(usuario.getEmail());
        return new LoginResponseDTO(token, usuario.getNome());
    }

    public LoginResponseDTO cadastrar(CadastroRequestDTO dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new EmailJaCadastradoException();
        }

        Usuario usuario = new Usuario();
        usuario.setNome(dto.nome());
        usuario.setEmail(dto.email());
        usuario.setSenha(passwordEncoder.encode(dto.senha()));

        usuarioRepository.save(usuario);

        String token = jwtService.gerarToken(usuario.getEmail());
        return new LoginResponseDTO(token, usuario.getNome());
    }
}