package exolex.exotic.map;

import exolex.exotic.dtos.ClienteRequestDTO;
import exolex.exotic.dtos.ClienteResponseDTO;
import exolex.exotic.model.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteRequestDTO dto) {
        Cliente cliente = new Cliente();
        cliente.setNome(dto.nome());
        cliente.setDocumento(dto.documento());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
        return cliente;
    }

    public void updateEntityFromDTO(ClienteRequestDTO dto, Cliente cliente) {
        cliente.setNome(dto.nome());
        cliente.setDocumento(dto.documento());
        cliente.setEmail(dto.email());
        cliente.setTelefone(dto.telefone());
    }

    public ClienteResponseDTO toResponseDTO(Cliente cliente) {
        return new ClienteResponseDTO(
                cliente.getId(),
                cliente.getNome(),
                cliente.getDocumento(),
                cliente.getEmail(),
                cliente.getTelefone()
        );
    }
}